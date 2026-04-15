package com.orderservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "Line item inside an order response.")
public record OrderItemResponseDTO(

        @Schema(description = "Unique identifier of the order line item.", example = "1001")
        Long id,

        @Schema(description = "ID of the product purchased.", example = "100")
        Long productId,

        @Schema(description = "Quantity purchased.", example = "2")
        Integer quantity,

        @Schema(description = "Unit price in USD, captured at purchase time.", example = "29.99")
        Double itemPrice,

        @Schema(description = "Timestamp when the line item was created (UTC).", example = "2026-01-15T10:30:00Z")
        Instant createdAt

) {
}
