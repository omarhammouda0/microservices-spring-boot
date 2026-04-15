package com.userservice.controller;

import com.userservice.dto.UserResponseDTO;
import com.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/users")
@AllArgsConstructor
@Tag(name = "Internal API", description = "Internal endpoints for service-to-service communication")
public class InternalUserController {

    private final UserService userService;

    @GetMapping("/{id}")
    @Operation(
            summary = "Get user by ID (Internal)",
            description = "Internal endpoint for other services to fetch user details. No authentication required."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UserResponseDTO> getUserById(
            @Parameter(description = "User ID", required = true, example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(userService.getInternalUserById(id));
    }
}