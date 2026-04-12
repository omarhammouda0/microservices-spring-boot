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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service

@Slf4j
@AllArgsConstructor

public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserEventPublisher  userEventPublisher;
    private final HelperService helperService;

    @Transactional
    public UserResponseDTO createUser(UserCreateDTO createDTO , String userRole) {

        helperService.checkIfAdmin ( userRole );

        String email = createDTO.email ( );

        if (userRepository.existsByEmailIgnoreCase ( email ))

            throw new UserAlreadyExistsException ( email );

        var toSave = userMapper.toUser ( createDTO );
        var saved = userRepository.save ( toSave );

        log.info ( "User with email {} created successfully" , email );

        return userMapper.toUserDTO ( saved );

    }

    @Transactional(readOnly = true)
    public UserResponseDTO getUserById(Long id , Long currentUserId , String userRole) {

        var user = userRepository.findById ( id ).orElseThrow ( () ->
                new UserNotFoundException ( id ) );


        if (! Objects.equals ( currentUserId , user.getId ( ) )   ) {
            helperService.checkIfAdmin ( userRole );
        }

        if ( ! user.getIsActive ())
            throw new UserNotActiveException ( id );

        return userMapper.toUserDTO ( user );

    }

    @Transactional(readOnly = true)
    public List<UserResponseDTO> getAllUsers(String userRole) {

        helperService.checkIfAdmin ( userRole );

        return userRepository.findAll ( )
                .stream ( )
                .map ( userMapper::toUserDTO )
                .collect ( Collectors.toList ( ) );
    }

    @Transactional (readOnly = true)
    public List<UserResponseDTO> getActiveUsers(String userRole ) {

        helperService.checkIfAdmin ( userRole );

        return userRepository.getActiveUsers ()

                .stream ()
                .map ( userMapper::toUserDTO )
                .collect ( Collectors.toList ( ) );

    }

    @Transactional
    public UserResponseDTO updateUser(Long id, UserUpdateDTO updateDTO , String userRole , Long currentUserId) {

        if (updateDTO.email () == null && updateDTO.name () == null  )
            throw new IllegalStateException ( "Should add at least one new value" );

        var user = userRepository.findById ( id ).orElseThrow ( () ->
                new UserNotFoundException ( id ) );


        if (! Objects.equals ( currentUserId , user.getId ( ) )   ) {
            helperService.checkIfAdmin ( userRole );
        }

        if (updateDTO.email () != null) {
            var newEmail = updateDTO.email ().trim ();

            if (userRepository.existsByEmailIgnoreCaseAndIdNot (newEmail , id ) )
                throw new UserAlreadyExistsException ( newEmail );

            user.setEmail ( newEmail );

        }

        if ( updateDTO.name () != null ) {
            var newName = updateDTO.name ().trim ();
            user.setName ( newName );

        }

        var saved = userRepository.save ( user );
        log.info ( "User with email {} updated successfully" , saved.getEmail () );
        userEventPublisher.publishUserUpdateEvent ( id );

        return userMapper.toUserDTO ( saved );

    }

    @Transactional
    public void deleteUser(Long id , String userRole , Long currentUserId) {

        var user = userRepository.findById ( id )
                .orElseThrow ( () -> new UserNotFoundException ( id ) );

        if (! Objects.equals ( currentUserId , user.getId ( ) )   ) {
            helperService.checkIfAdmin ( userRole );
        }

        if (! user.getIsActive () )
            throw new UserAlreadyDeletedException ( id );

        user.setIsActive (  false );
        userRepository.save ( user );

        userEventPublisher.publishUserUpdateEvent ( id );
        log.info ( "User with email {} deleted successfully" , user.getEmail () );

    }


    @Transactional(readOnly = true)
    public UserResponseDTO getInternalUserById(Long id) {

        var user = userRepository.findById ( id ).orElseThrow ( () ->
                new UserNotFoundException ( id ) );

        if (!user.getIsActive ( ))
            throw new UserNotActiveException ( id );

        return userMapper.toUserDTO ( user );
    }
}
