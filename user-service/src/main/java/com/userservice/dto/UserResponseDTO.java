package com.userservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "Public user representation returned by the API.")
public record UserResponseDTO(

        @Schema(description = "Unique user identifier.", example = "42")
        Long id,

        @Schema(description = "Full name of the user.", example = "Jane Doe")
        String name,

        @Schema(description = "Email address of the user.", example = "jane.doe@example.com")
        String email,

        @Schema(description = "Account creation timestamp (UTC).", example = "2026-01-15T10:30:00Z")
        Instant createdAt,

        @Schema(description = "Whether the account is currently active (false = soft-deleted).", example = "true")
        boolean active

) {
}
