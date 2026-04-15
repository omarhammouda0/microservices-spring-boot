package com.userservice.dto;

import com.userservice.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Self-registration payload for a new user account.")
public record RegisterRequestDTO(

        @Schema(
                description = "Full name of the user.",
                example = "Jane Doe",
                minLength = 5,
                maxLength = 100,
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "The name can not be empty")
        @Size(min = 5, max = 100, message = "The name should be between 5 and 100 characters")
        String name,

        @Schema(
                description = "Unique email address.",
                example = "jane.doe@example.com",
                format = "email",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "The email can not be empty")
        @Email(message = "Please enter a valid email format")
        String email,

        @Schema(
                description = "Password: min 10 chars, at least one upper, one lower, one digit, one special.",
                example = "StrongPass!123",
                format = "password",
                minLength = 10,
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "The password can not be empty")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{10,}$",
                message = "Password must be at least 10 characters long " +
                        "and contain at least one uppercase letter," +
                        " one lowercase letter," +
                        " one digit," +
                        " and one special character")
        String password,

        @Schema(
                description = "Account role. USER for regular customers, ADMIN for administrators.",
                example = "USER",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "The role can not be empty")
        Role role

) { }
