package com.userservice.service;

import com.userservice.dto.AuthResponseDTO;
import com.userservice.dto.LoginRequestDTO;
import com.userservice.dto.RegisterRequestDTO;
import com.userservice.enums.Role;
import com.userservice.exception.types.UserAlreadyExistsException;
import com.userservice.exception.types.UserNotFoundException;
import com.userservice.mapper.UserMapper;
import com.userservice.repository.UserRepository;
import com.userservice.security.JwtService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@AllArgsConstructor
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthResponseDTO registerUser(RegisterRequestDTO dto) {

        String email = dto.email ( ).trim ( );

        if (userRepository.existsByEmailIgnoreCase ( email )) {
            throw new UserAlreadyExistsException ( "Email already exists" );
        }

        String password = passwordEncoder.encode ( dto.password ( ) );

        var user = userMapper.toUser ( dto );

        user.setPassword ( password );
        user.setRole(dto.role() != null ? dto.role().toString() : Role.USER.toString());

        var saved = userRepository.save( user );

        var token = jwtService.generateToken (  saved );

        return new AuthResponseDTO ( token , "Bearer"  );


    }

    public AuthResponseDTO logInUser (LoginRequestDTO dto) {

        String email = dto.email ( ).trim ( );
        String password = dto.password ( );

        authenticationManager.authenticate ( new UsernamePasswordAuthenticationToken ( email, password ) );

        var user = userRepository.findByEmailIgnoreCase(email).
                orElseThrow ( () -> new UserNotFoundException ( "User not found" ) );

        var token = jwtService.generateToken ( user );

        return new AuthResponseDTO ( token , "Bearer" );

    }

}
