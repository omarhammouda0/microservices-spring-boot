package com.productservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Schema(description = "Payload used by an ADMIN to create a new product.")
public record ProductCreateDTO(

        @Schema(
                description = "Product display name.",
                example = "Wireless Mouse",
                minLength = 3,
                maxLength = 100,
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "The product name is mandatory")
        @Size(min = 3, max = 100, message = "The product name must be between 3 and 100 characters")
        String name,

        @Schema(
                description = "Unit price in USD. Must be strictly positive.",
                example = "29.99",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "The product price is mandatory")
        @Positive(message = "The product price must be positive")
        Double price,

        @Schema(
                description = "ID of the user (seller) that owns this product.",
                example = "1",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "The user ID is mandatory")
        @Positive(message = "The user ID must be positive")
        Long userId

) {
}
