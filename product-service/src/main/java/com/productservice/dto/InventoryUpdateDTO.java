package com.productservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.PositiveOrZero;

@Schema(description = "Admin-only payload used to set the stock quantity for a product.")
public record InventoryUpdateDTO(

        @Schema(
                description = "New on-hand quantity. Must be zero or positive.",
                example = "100",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @PositiveOrZero(message = "Quantity cannot be negative")
        Integer quantity
) {}
