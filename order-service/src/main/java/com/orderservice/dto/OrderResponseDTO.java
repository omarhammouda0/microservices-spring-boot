package com.orderservice.dto;

import com.orderservice.enums.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;

@Schema(description = "Full order representation including line items and status.")
public record OrderResponseDTO(

        @Schema(description = "Unique order identifier.", example = "500")
        Long id,

        @Schema(description = "ID of the user that placed the order.", example = "1")
        Long userId,

        @Schema(description = "Line items included in the order.")
        List<OrderItemResponseDTO> items,

        @Schema(description = "Current order status.", example = "CONFIRMED")
        OrderStatus status,

        @Schema(description = "Total amount in USD.", example = "59.98")
        Double totalAmount,

        @Schema(description = "Timestamp when the order was created (UTC).", example = "2026-01-15T10:30:00Z")
        Instant createdAt,

        @Schema(description = "Timestamp when the order was last updated (UTC).", example = "2026-01-15T10:35:00Z")
        Instant updatedAt

) {
}
