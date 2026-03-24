package com.userservice.mapper;

import com.userservice.dto.AuthResponseDTO;
import com.userservice.dto.RegisterRequestDTO;
import com.userservice.dto.UserCreateDTO;
import com.userservice.dto.UserResponseDTO;
import com.userservice.entity.User;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component

public class UserMapper {

    public User toUser (UserCreateDTO userCreateDTO) {

        return User.builder ()

                .name ( userCreateDTO.name ( ) )
                .email ( userCreateDTO.email ( ) )
                .isActive ( true )

                .build ();

    }

    public User toUser (RegisterRequestDTO registerRequestDTO) {

        return User.builder ( )

                .name ( registerRequestDTO.name ( ) )
                .email ( registerRequestDTO.email ( ) )
                .isActive ( true )

                .build ( );

    }

    public UserResponseDTO toUserDTO (User user) {

        return new UserResponseDTO (

                user.getId () ,
                user.getName ( ),
                user.getEmail ( ) ,
                user.getCreatedAt () ,
                user.getIsActive ()

                );

    }



}
