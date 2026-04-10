package com.productervice.dto;

import jakarta.validation.constraints.PositiveOrZero;

public record InventoryUpdateDTO(
        @PositiveOrZero(message = "Quantity cannot be negative")
        Integer quantity
) {}
