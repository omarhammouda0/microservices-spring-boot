package com.userservice.controller;

import com.userservice.dto.AuthResponseDTO;
import com.userservice.dto.LoginRequestDTO;
import com.userservice.dto.RegisterRequestDTO;
import com.userservice.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/auth")
@Validated
@Tag(name = "Authentication", description = "Endpoints for user authentication and registration")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(
            summary = "Register a new user",
            description = "Creates a new user account and returns a JWT token."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data or email already exists"),
            @ApiResponse(responseCode = "422", description = "Validation error")
    })
    public ResponseEntity<AuthResponseDTO> createUser(@Valid @RequestBody RegisterRequestDTO user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registerUser(user));
    }

    @PostMapping("/login")
    @Operation(
            summary = "Login user",
            description = "Authenticates a user and returns a JWT token."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "401", description = "Invalid email or password"),
            @ApiResponse(responseCode = "403", description = "Account is inactive"),
            @ApiResponse(responseCode = "422", description = "Validation error")
    })
    public ResponseEntity<AuthResponseDTO> loginUser(@Valid @RequestBody LoginRequestDTO user) {
        return ResponseEntity.status(HttpStatus.OK).body(authService.logInUser(user));
    }
}