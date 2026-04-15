package com.userservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Credentials for authenticating an existing user.")
public record LoginRequestDTO(

        @Schema(
                description = "Email address used to register the account.",
                example = "jane.doe@example.com",
                format = "email",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "Please enter an email address")
        @Email(message = "Please enter a valid email format")
        String email,

        @Schema(
                description = "Plain-text password.",
                example = "StrongPass!123",
                format = "password",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "Please enter a password")
        String password

) {
}
