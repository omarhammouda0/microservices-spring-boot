package com.productservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Schema(description = "Partial update payload for a product. All fields are optional.")
public record ProductUpdateDTO(

        @Schema(
                description = "New product name.",
                example = "Wireless Mouse Pro",
                minLength = 3,
                maxLength = 100,
                nullable = true
        )
        @Size(min = 3, max = 100, message = "The product name must be between 3 and 100 characters")
        String name,

        @Schema(
                description = "New unit price in USD.",
                example = "34.99",
                nullable = true
        )
        @Positive(message = "The product price must be positive")
        Double price

) {
}
