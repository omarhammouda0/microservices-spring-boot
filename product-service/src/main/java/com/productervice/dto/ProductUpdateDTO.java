package com.productervice.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record ProductUpdateDTO(


        @Size(min = 3, max = 100, message = "The product name must be between 3 and 100 characters")
        String name ,

        @Positive(message = "The product price must be positive")
        Double price

) {
}
