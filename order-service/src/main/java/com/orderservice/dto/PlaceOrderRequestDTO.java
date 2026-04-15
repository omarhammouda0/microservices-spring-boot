package com.orderservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

@Schema(description = "Request body for placing a new order. Must contain at least one item.")
public record PlaceOrderRequestDTO(

        @Schema(
                description = "List of items the user wants to purchase.",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @Valid
        @NotEmpty(message = "Order must contain at least one item")
        List<OrderItemRequestDTO> orderItemRequestDTOList

) {
}
