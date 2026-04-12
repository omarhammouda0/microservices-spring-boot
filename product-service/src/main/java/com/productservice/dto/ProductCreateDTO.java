package com.productservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record ProductCreateDTO(

        @NotBlank(message = "The product name is mandatory")
        @Size(min = 3, max = 100, message = "The product name must be between 3 and 100 characters")
        String name ,

        @NotNull (message = "The product price is mandatory")
        @Positive (message = "The product price must be positive")
        Double price ,

        @NotNull (message = "The user ID is mandatory")
        @Positive (message = "The user ID must be positive")
        Long userId

) {
}
