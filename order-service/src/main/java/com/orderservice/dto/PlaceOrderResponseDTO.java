package com.orderservice.dto;

import com.orderservice.enums.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "Response returned immediately after an order is placed.")
public record PlaceOrderResponseDTO(

        @Schema(description = "Unique order identifier.", example = "500")
        Long orderId,

        @Schema(description = "Current order status.", example = "PENDING")
        OrderStatus status,

        @Schema(description = "Timestamp when the order was created (UTC).", example = "2026-01-15T10:30:00Z")
        Instant createdAt,

        @Schema(description = "Total amount in USD.", example = "59.98")
        Double totalAmount

) {
}
