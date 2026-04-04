package com.orderservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record OrderItemRequestDTO(


        @Positive (message = "the product id can not be zero or negative")
        @NotNull (message = "The product id can not be null")
        Long productId ,

        @Positive (message = "the quantity can not be zero or negative")
        @NotNull (message = "The quantity can not be null")
        Integer quantity

) {
}
