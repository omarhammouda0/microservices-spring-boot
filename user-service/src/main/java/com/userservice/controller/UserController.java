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


    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserCreateDTO user ,
                                                      @RequestHeader("X-User-Role") String userRole) {

        return ResponseEntity.status ( HttpStatus.CREATED ).body ( userService.createUser ( user , userRole ) );
    }


    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById
            (@PathVariable @Positive Long id ,
             @RequestHeader("X-User-Id") Long currentUserId  ,
             @RequestHeader("X-User-Role") String userRole ) {

        return ResponseEntity.ok ( userService.getUserById ( id , currentUserId ,  userRole ) );
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers(@RequestHeader("X-User-Role") String userRole) {
        return ResponseEntity.ok ( userService.getAllUsers ( userRole) );
    }

    @GetMapping("/active")
    public ResponseEntity<List<UserResponseDTO>> getAllActiveUsers(@RequestHeader("X-User-Role") String userRole
     ) {
        return ResponseEntity.ok ( userService.getActiveUsers (userRole ) );
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable @Positive Long id ,
                                                      @RequestHeader("X-User-Role") String userRole,
                                                      @RequestHeader("X-User-Id") Long currentUserId,
                                                      @Valid @RequestBody UserUpdateDTO updateDTO) {

        return ResponseEntity.ok ( userService.updateUser ( id , updateDTO ,userRole , currentUserId) );
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable @Positive Long id ,
                                           @RequestHeader("X-User-Role") String userRole ,
                                           @RequestHeader("X-User-Id") Long currentUserId) {
        userService.deleteUser ( id , userRole ,  currentUserId );
        return ResponseEntity.noContent ( ).build (  );
    }


}
