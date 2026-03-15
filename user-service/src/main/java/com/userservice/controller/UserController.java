package com.userservice.controller;

import com.userservice.dto.UserCreateDTO;
import com.userservice.dto.UserResponseDTO;
import com.userservice.dto.UserUpdateDTO;
import com.userservice.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor

@RestController
@RequestMapping("/users")
@Validated

public class UserController {

    private final UserService userService;

    @GetMapping("/test")
    public String hello() {
        return "Hello from User Service!";
    }

    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserCreateDTO user) {
        return ResponseEntity.status ( HttpStatus.CREATED ).body ( userService.createUser ( user ) );
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById
            (@PathVariable @Positive Long id) {

        return ResponseEntity.ok ( userService.getUserById ( id ) );
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        return ResponseEntity.ok ( userService.getAllUsers ( ) );
    }

    @GetMapping("/active")
    public ResponseEntity<List<UserResponseDTO>> getAllActiveUsers() {
        return ResponseEntity.ok ( userService.getActiveUsers ( ) );
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable @Positive Long id ,
                                                      @Valid @RequestBody UserUpdateDTO updateDTO) {

        return ResponseEntity.ok ( userService.updateUser ( id , updateDTO ) );
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable @Positive Long id) {
        userService.deleteUser ( id );
        return ResponseEntity.noContent ( ).build ( );
    }


}
