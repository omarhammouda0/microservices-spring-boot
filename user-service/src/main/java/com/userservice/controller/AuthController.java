package com.userservice.controller;

import com.userservice.dto.*;
import com.userservice.service.AuthService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor

@RestController
@RequestMapping("/auth")

public class AuthController {

    private final AuthService authService;


    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> createUser(@Valid @RequestBody RegisterRequestDTO user) {
        return ResponseEntity.status ( HttpStatus.CREATED ).body ( authService.registerUser ( user ) );
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> loginUser(@Valid @RequestBody LoginRequestDTO user) {
        return ResponseEntity.status ( HttpStatus.OK ).body ( authService.logInUser ( user ) );
    }

}
