package com.userservice.service;

import com.userservice.dto.AuthResponseDTO;
import com.userservice.dto.LoginRequestDTO;
import com.userservice.dto.RegisterRequestDTO;
import com.userservice.enums.Role;
import com.userservice.exception.types.InvalidCredentialsException;
import com.userservice.exception.types.UserAlreadyExistsException;
import com.userservice.exception.types.UserNotFoundException;
import com.userservice.mapper.UserMapper;
import com.userservice.repository.UserRepository;
import com.userservice.security.JwtService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


/**
 * Handles self-registration and login for end users.
 *
 * <p>On both register and login, a signed JWT is returned containing the
 * user's email (subject), id, and role. The API Gateway later uses these
 * claims to populate the {@code X-User-Id} and {@code X-User-Role} request
 * headers consumed by downstream services.
 */
@AllArgsConstructor
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    /**
     * Registers a brand-new user account and returns a JWT.
     *
     * <p>Passwords are hashed with BCrypt before persistence. If no role is
     * supplied, the account defaults to {@link Role#USER}.
     *
     * @param dto the validated registration payload
     * @return an {@link AuthResponseDTO} containing the signed JWT
     * @throws UserAlreadyExistsException if the email is already registered
     */
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

    /**
     * Authenticates an existing user and returns a fresh JWT.
     *
     * @param dto the login credentials
     * @return an {@link AuthResponseDTO} containing the signed JWT
     * @throws InvalidCredentialsException if the email/password combination is wrong
     * @throws UserNotFoundException       if authentication succeeds but the user lookup fails
     *                                     (should not happen in practice)
     */
    public AuthResponseDTO logInUser (LoginRequestDTO dto) {

        String email = dto.email ( ).trim ( );
        String password = dto.password ( );

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
        } catch (BadCredentialsException e) {
            throw new InvalidCredentialsException ();
        }

        var user = userRepository.findByEmailIgnoreCase(email).
                orElseThrow ( () -> new UserNotFoundException ( "User not found" ) );

        var token = jwtService.generateToken ( user );

        return new AuthResponseDTO ( token , "Bearer" );

    }

}
