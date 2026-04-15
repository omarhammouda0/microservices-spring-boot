package com.productservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "Public product representation returned by the API.")
public record ProductResponseDTO(

        @Schema(description = "Unique product identifier.", example = "100")
        Long id,

        @Schema(description = "Product display name.", example = "Wireless Mouse")
        String name,

        @Schema(description = "Unit price in USD.", example = "29.99")
        Double price,

        @Schema(description = "ID of the user (seller) that owns this product.", example = "1")
        Long userId,

        @Schema(description = "Timestamp when the product was created (UTC).", example = "2026-01-15T10:30:00Z")
        Instant createdDate

) {
}
