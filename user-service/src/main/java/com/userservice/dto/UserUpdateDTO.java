package com.userservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

@Schema(description = "Partial update payload. At least one field must be non-null.")
public record UserUpdateDTO(

        @Schema(
                description = "New full name (optional).",
                example = "Jane A. Doe",
                minLength = 4,
                maxLength = 50,
                nullable = true
        )
        @Size(min = 4, max = 50, message = "Name must be between 4 and 50 characters")
        String name,

        @Schema(
                description = "New email address (optional).",
                example = "jane.a.doe@example.com",
                minLength = 5,
                maxLength = 100,
                format = "email",
                nullable = true
        )
        @Size(min = 5, max = 100, message = "Email must be between 5 and 100 characters")
        @Email(message = "Invalid email format")
        String email
) {
}
