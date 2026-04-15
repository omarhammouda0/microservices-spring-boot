package com.userservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response returned after successful login or registration.")
public record AuthResponseDTO(

        @Schema(
                description = "Signed JWT access token. Send it in the Authorization header as: Bearer <token>",
                example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqYW5lQGV4YW1wbGUuY29tIn0.abc123..."
        )
        String accessToken,

        @Schema(description = "Token type. Always 'Bearer'.", example = "Bearer")
        String tokenType

) {
}
