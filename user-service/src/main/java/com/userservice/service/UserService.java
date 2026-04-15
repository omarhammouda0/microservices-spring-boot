package com.userservice.service;

import com.userservice.dto.UserCreateDTO;
import com.userservice.dto.UserResponseDTO;
import com.userservice.dto.UserUpdateDTO;
import com.userservice.event.UserEventPublisher;
import com.userservice.exception.types.UserAlreadyDeletedException;
import com.userservice.exception.types.UserAlreadyExistsException;
import com.userservice.exception.types.UserNotActiveException;
import com.userservice.exception.types.UserNotFoundException;
import com.userservice.mapper.UserMapper;
import com.userservice.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * Application service for user account management.
 *
 * <p>Responsibilities:
 * <ul>
 *     <li>CRUD operations on user accounts (create, read, update, soft-delete)</li>
 *     <li>RBAC enforcement via {@link HelperService} using the caller's role header</li>
 *     <li>Self-access checks: a user may read/update/delete only their own profile
 *         unless they are an ADMIN</li>
 *     <li>Publishing {@code UserUpdated} events on mutation so downstream caches
 *         (e.g. product-service Redis cache) stay consistent</li>
 * </ul>
 *
 * <p>All authorization checks in this service trust the {@code userRole} string
 * that the API Gateway derives from the signed JWT and injects via the
 * {@code X-User-Role} header. Direct access to this service is blocked at the
 * infrastructure level (docker-compose network isolation).
 */
@Service
@Slf4j
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserEventPublisher userEventPublisher;
    private final HelperService helperService;

    /**
     * Creates a new user account. Admin-only operation.
     *
     * @param createDTO validated creation payload (name, email)
     * @param userRole  the caller's role (must be {@code ADMIN})
     * @return the newly created user as a response DTO
     * @throws com.userservice.exception.types.NotAuthorizedException   if the caller is not ADMIN
     * @throws com.userservice.exception.types.UserAlreadyExistsException if the email is already registered
     */
    @Transactional
    public UserResponseDTO createUser(UserCreateDTO createDTO, String userRole) {

        helperService.checkIfAdmin(userRole);

        String email = createDTO.email();

        if (userRepository.existsByEmailIgnoreCase(email))
            throw new UserAlreadyExistsException(email);

        var toSave = userMapper.toUser(createDTO);
        var saved = userRepository.save(toSave);

        log.info("User with email {} created successfully", email);

        return userMapper.toUserDTO(saved);
    }

    /**
     * Fetches a single user by id.
     *
     * <p>Access rules:
     * <ul>
     *     <li>A user may fetch their own profile.</li>
     *     <li>An ADMIN may fetch any user.</li>
     * </ul>
     *
     * @param id            target user id
     * @param currentUserId id of the caller (from {@code X-User-Id} header)
     * @param userRole      role of the caller (from {@code X-User-Role} header)
     * @return the user as a response DTO
     * @throws com.userservice.exception.types.UserNotFoundException    if no user with {@code id} exists
     * @throws com.userservice.exception.types.NotAuthorizedException   if the caller is neither the target user nor ADMIN
     * @throws com.userservice.exception.types.UserNotActiveException   if the target user is soft-deleted
     */
    @Transactional(readOnly = true)
    public UserResponseDTO getUserById(Long id, Long currentUserId, String userRole) {

        var user = userRepository.findById(id).orElseThrow(() ->
                new UserNotFoundException(id));

        if (!Objects.equals(currentUserId, user.getId())) {
            helperService.checkIfAdmin(userRole);
        }

        if (!user.getIsActive())
            throw new UserNotActiveException(id);

        return userMapper.toUserDTO(user);
    }

    /**
     * Returns a paginated list of all users (active and soft-deleted). Admin-only.
     *
     * @param userRole caller role (must be {@code ADMIN})
     * @param pageable pagination/sort parameters
     * @return a page of user DTOs
     * @throws com.userservice.exception.types.NotAuthorizedException if the caller is not ADMIN
     */
    @Transactional(readOnly = true)
    public Page<UserResponseDTO> getAllUsers(String userRole, Pageable pageable) {

        helperService.checkIfAdmin(userRole);

        log.info("Fetching all users with pagination - Page: {}, Size: {}",
                pageable.getPageNumber(), pageable.getPageSize());

        return userRepository.findAll(pageable)
                .map(userMapper::toUserDTO);
    }

    /**
     * Returns a paginated list of only active (non-soft-deleted) users. Admin-only.
     *
     * @param userRole caller role (must be {@code ADMIN})
     * @param pageable pagination/sort parameters
     * @return a page of active user DTOs
     * @throws com.userservice.exception.types.NotAuthorizedException if the caller is not ADMIN
     */
    @Transactional(readOnly = true)
    public Page<UserResponseDTO> getActiveUsers(String userRole, Pageable pageable) {

        helperService.checkIfAdmin(userRole);

        log.info("Fetching active users with pagination - Page: {}, Size: {}",
                pageable.getPageNumber(), pageable.getPageSize());

        return userRepository.findAllByIsActiveTrue(pageable)
                .map(userMapper::toUserDTO);
    }

    /**
     * Partially updates a user's name and/or email.
     *
     * <p>Access rules:
     * <ul>
     *     <li>A user may update their own profile.</li>
     *     <li>An ADMIN may update any user.</li>
     * </ul>
     *
     * <p>On success, publishes a {@code UserUpdated} event so downstream caches
     * (product-service Redis) can invalidate their snapshots.
     *
     * @param id            target user id
     * @param updateDTO     partial payload; at least one of {name, email} must be non-null
     * @param userRole      caller role
     * @param currentUserId caller id
     * @return the updated user DTO
     * @throws IllegalStateException                                      if both fields are null
     * @throws com.userservice.exception.types.UserNotFoundException      if the user does not exist
     * @throws com.userservice.exception.types.NotAuthorizedException     if the caller is neither owner nor ADMIN
     * @throws com.userservice.exception.types.UserAlreadyExistsException if the new email is already taken
     */
    @Transactional
    public UserResponseDTO updateUser(Long id, UserUpdateDTO updateDTO, String userRole, Long currentUserId) {

        if (updateDTO.email() == null && updateDTO.name() == null)
            throw new IllegalStateException("Should add at least one new value");

        var user = userRepository.findById(id).orElseThrow(() ->
                new UserNotFoundException(id));

        if (!Objects.equals(currentUserId, user.getId())) {
            helperService.checkIfAdmin(userRole);
        }

        if (updateDTO.email() != null) {
            var newEmail = updateDTO.email().trim();

            if (userRepository.existsByEmailIgnoreCaseAndIdNot(newEmail, id))
                throw new UserAlreadyExistsException(newEmail);

            user.setEmail(newEmail);
        }

        if (updateDTO.name() != null) {
            var newName = updateDTO.name().trim();
            user.setName(newName);
        }

        var saved = userRepository.save(user);
        log.info("User with email {} updated successfully", saved.getEmail());
        userEventPublisher.publishUserUpdateEvent(id);

        return userMapper.toUserDTO(saved);
    }

    /**
     * Soft-deletes a user by setting {@code isActive = false}.
     *
     * <p>Access rules:
     * <ul>
     *     <li>A user may delete their own account.</li>
     *     <li>An ADMIN may delete any user.</li>
     * </ul>
     *
     * <p>Publishes a {@code UserUpdated} event so product-service Redis cache is
     * invalidated and no new products can be attached to this user.
     *
     * @param id            target user id
     * @param userRole      caller role
     * @param currentUserId caller id
     * @throws com.userservice.exception.types.UserNotFoundException       if the user does not exist
     * @throws com.userservice.exception.types.NotAuthorizedException      if the caller is neither owner nor ADMIN
     * @throws com.userservice.exception.types.UserAlreadyDeletedException if the user is already soft-deleted
     */
    @Transactional
    public void deleteUser(Long id, String userRole, Long currentUserId) {

        var user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        if (!Objects.equals(currentUserId, user.getId())) {
            helperService.checkIfAdmin(userRole);
        }

        if (!user.getIsActive())
            throw new UserAlreadyDeletedException(id);

        user.setIsActive(false);
        userRepository.save(user);

        userEventPublisher.publishUserUpdateEvent(id);
        log.info("User with email {} deleted successfully", user.getEmail());
    }

    /**
     * Internal lookup used by other services (e.g. product-service via Feign).
     *
     * <p>This method skips RBAC checks because the endpoint exposing it
     * ({@link com.userservice.controller.InternalUserController}) is only
     * reachable inside the Docker network.
     *
     * @param id user id
     * @return the user DTO
     * @throws com.userservice.exception.types.UserNotFoundException  if the user does not exist
     * @throws com.userservice.exception.types.UserNotActiveException if the user is soft-deleted
     */
    @Transactional(readOnly = true)
    public UserResponseDTO getInternalUserById(Long id) {

        var user = userRepository.findById(id).orElseThrow(() ->
                new UserNotFoundException(id));

        if (!user.getIsActive())
            throw new UserNotActiveException(id);

        return userMapper.toUserDTO(user);
    }
}