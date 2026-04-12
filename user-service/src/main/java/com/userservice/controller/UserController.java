package com.userservice.controller;

import com.userservice.dto.UserCreateDTO;
import com.userservice.dto.UserResponseDTO;
import com.userservice.dto.UserUpdateDTO;
import com.userservice.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/users")
@Validated
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(
            @Valid @RequestBody UserCreateDTO user,
            @RequestHeader("X-User-Role") String userRole) {

        log.info("Creating user by admin");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.createUser(user, userRole));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(
            @PathVariable @Positive Long id,
            @RequestHeader("X-User-Id") Long currentUserId,
            @RequestHeader("X-User-Role") String userRole) {

        log.info("Fetching user by id: {} by requester: {}", id, currentUserId);
        return ResponseEntity.ok(userService.getUserById(id, currentUserId, userRole));
    }

    @GetMapping
    public ResponseEntity<Page<UserResponseDTO>> getAllUsers(
            @RequestHeader("X-User-Role") String userRole,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {

        log.info("Fetching all users with pagination - Page: {}, Size: {}",
                pageable.getPageNumber(), pageable.getPageSize());
        return ResponseEntity.ok(userService.getAllUsers(userRole, pageable));
    }

    @GetMapping("/active")
    public ResponseEntity<Page<UserResponseDTO>> getAllActiveUsers(
            @RequestHeader("X-User-Role") String userRole,
            @PageableDefault(size = 10, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {

        log.info("Fetching active users with pagination - Page: {}, Size: {}",
                pageable.getPageNumber(), pageable.getPageSize());
        return ResponseEntity.ok(userService.getActiveUsers(userRole, pageable));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable @Positive Long id,
            @RequestHeader("X-User-Role") String userRole,
            @RequestHeader("X-User-Id") Long currentUserId,
            @Valid @RequestBody UserUpdateDTO updateDTO) {

        log.info("Updating user: {} by requester: {}", id, currentUserId);
        return ResponseEntity.ok(userService.updateUser(id, updateDTO, userRole, currentUserId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable @Positive Long id,
            @RequestHeader("X-User-Role") String userRole,
            @RequestHeader("X-User-Id") Long currentUserId) {

        log.info("Deleting user: {} by requester: {}", id, currentUserId);
        userService.deleteUser(id, userRole, currentUserId);
        return ResponseEntity.noContent().build();
    }
}