package com.userservice.service;

import com.userservice.dto.UserCreateDTO;
import com.userservice.dto.UserResponseDTO;
import com.userservice.dto.UserUpdateDTO;
import com.userservice.exception.types.UserAlreadyDeletedException;
import com.userservice.exception.types.UserAlreadyExistsException;
import com.userservice.exception.types.UserNotActiveException;
import com.userservice.exception.types.UserNotFoundException;
import com.userservice.mapper.UserMapper;
import com.userservice.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service

@Slf4j
@AllArgsConstructor

public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserResponseDTO createUser(UserCreateDTO createDTO) {

        String email = createDTO.email ();

        if (userRepository.existsByEmailIgnoreCase(email))

            throw new UserAlreadyExistsException ( email );

        var toSave = userMapper.toUser (  createDTO );
        var saved = userRepository.save ( toSave );

        log.info (  "User with email {} created successfully" , email );

        return userMapper.toUserDTO (  saved );

    }

    public UserResponseDTO getUserById(Long id) {

        var user = userRepository.findById ( id ).orElseThrow ( () ->
                new UserNotFoundException ( id ) );

        if ( ! user.getIsActive ())
            throw new UserNotActiveException ( id );

        return userMapper.toUserDTO ( user );

    }

    public List<UserResponseDTO> getAllUsers() {

        return userRepository.findAll ( )
                .stream ( )
                .map ( userMapper::toUserDTO )
                .collect ( Collectors.toList ( ) );
    }

    public List<UserResponseDTO> getActiveUsers() {

        return userRepository.getActiveUsers ()

                .stream ()
                .map ( userMapper::toUserDTO )
                .collect ( Collectors.toList ( ) );

    }

    public UserResponseDTO updateUser(Long id, UserUpdateDTO updateDTO) {

        if (updateDTO.email () == null && updateDTO.name () == null  )
            throw new IllegalStateException ( "Should add at least one new value" );

        var user = userRepository.findById ( id ).orElseThrow ( () ->
                new UserNotFoundException ( id ) );

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

        return userMapper.toUserDTO ( saved );

    }

    public void deleteUser(Long id) {

        var user = userRepository.findById ( id )
                .orElseThrow ( () -> new UserNotFoundException ( id ) );

        if (! user.getIsActive () )
            throw new UserAlreadyDeletedException ( id );

        user.setIsActive (  false );
        userRepository.save ( user );

        log.info ( "User with email {} deleted successfully" , user.getEmail () );

    }


}
