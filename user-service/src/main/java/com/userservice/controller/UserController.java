package com.userservice.controller;

import com.userservice.dto.UserCreateDTO;
import com.userservice.dto.UserResponseDTO;
import com.userservice.dto.UserUpdateDTO;
import com.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/users")
@Validated
@Tag(name = "User Management", description = "Endpoints for managing users in the system")
@SecurityRequirement(name = "BearerAuth")
public class UserController {

    private final UserService userService;

    @PostMapping
    @Operation(
            summary = "Create a new user",
            description = "Creates a new user account. Requires ADMIN role."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Requires ADMIN role"),
            @ApiResponse(responseCode = "409", description = "User with this email already exists")
    })
    public ResponseEntity<UserResponseDTO> createUser(
            @Valid @RequestBody UserCreateDTO user,
            @Parameter(description = "User role (ADMIN required)", required = true) @RequestHeader("X-User-Role") String userRole) {

        log.info("Creating user by admin");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.createUser(user, userRole));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get user by ID",
            description = "Retrieves user information. Users can only view their own profile; admins can view any user."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Cannot access another user's profile"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UserResponseDTO> getUserById(
            @Parameter(description = "User ID", required = true, example = "1") @PathVariable @Positive Long id,
            @Parameter(description = "Current user ID", required = true) @RequestHeader("X-User-Id") Long currentUserId,
            @Parameter(description = "User role", required = true) @RequestHeader("X-User-Role") String userRole) {

        log.info("Fetching user by id: {} by requester: {}", id, currentUserId);
        return ResponseEntity.ok(userService.getUserById(id, currentUserId, userRole));
    }

    @GetMapping
    @Operation(
            summary = "Get all users (Admin only)",
            description = "Retrieves a paginated list of all users. Requires ADMIN role."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Requires ADMIN role")
    })
    public ResponseEntity<Page<UserResponseDTO>> getAllUsers(
            @Parameter(description = "User role (ADMIN required)", required = true) @RequestHeader("X-User-Role") String userRole,
            @Parameter(description = "Pagination parameters") @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {

        log.info("Fetching all users with pagination - Page: {}, Size: {}",
                pageable.getPageNumber(), pageable.getPageSize());
        return ResponseEntity.ok(userService.getAllUsers(userRole, pageable));
    }

    @GetMapping("/active")
    @Operation(
            summary = "Get active users (Admin only)",
            description = "Retrieves a paginated list of all active (non-deleted) users. Requires ADMIN role."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Active users retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Requires ADMIN role")
    })
    public ResponseEntity<Page<UserResponseDTO>> getAllActiveUsers(
            @Parameter(description = "User role (ADMIN required)", required = true) @RequestHeader("X-User-Role") String userRole,
            @Parameter(description = "Pagination parameters") @PageableDefault(size = 10, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {

        log.info("Fetching active users with pagination - Page: {}, Size: {}",
                pageable.getPageNumber(), pageable.getPageSize());
        return ResponseEntity.ok(userService.getActiveUsers(userRole, pageable));
    }

    @PatchMapping("/{id}")
    @Operation(
            summary = "Update user",
            description = "Updates user information. Users can update their own profile; admins can update any user."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Cannot update another user's profile"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "409", description = "Email already exists")
    })
    public ResponseEntity<UserResponseDTO> updateUser(
            @Parameter(description = "User ID", required = true, example = "1") @PathVariable @Positive Long id,
            @Parameter(description = "User role", required = true) @RequestHeader("X-User-Role") String userRole,
            @Parameter(description = "Current user ID", required = true) @RequestHeader("X-User-Id") Long currentUserId,
            @Valid @RequestBody UserUpdateDTO updateDTO) {

        log.info("Updating user: {} by requester: {}", id, currentUserId);
        return ResponseEntity.ok(userService.updateUser(id, updateDTO, userRole, currentUserId));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete user (soft delete)",
            description = "Soft deletes a user by setting is_active=false. Users can delete their own account; admins can delete any user."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Cannot delete another user's account"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "409", description = "User already deleted")
    })
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "User ID", required = true, example = "1") @PathVariable @Positive Long id,
            @Parameter(description = "User role", required = true) @RequestHeader("X-User-Role") String userRole,
            @Parameter(description = "Current user ID", required = true) @RequestHeader("X-User-Id") Long currentUserId) {

        log.info("Deleting user: {} by requester: {}", id, currentUserId);
        userService.deleteUser(id, userRole, currentUserId);
        return ResponseEntity.noContent().build();
    }
}