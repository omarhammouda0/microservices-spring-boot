package com.orderservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "Single line item inside an order request.")
public record OrderItemRequestDTO(

        @Schema(
                description = "ID of the product to purchase.",
                example = "100",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @Positive(message = "the product id can not be zero or negative")
        @NotNull(message = "The product id can not be null")
        Long productId,

        @Schema(
                description = "Quantity requested. Must be strictly positive.",
                example = "2",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @Positive(message = "the quantity can not be zero or negative")
        @NotNull(message = "The quantity can not be null")
        Integer quantity

) {
}
