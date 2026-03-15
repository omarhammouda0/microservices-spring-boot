package com.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UserUpdateDTO(



        @Size(min = 4 , max = 50 , message = "Name must be between 4 and 50 characters")
        String name,


        @Size (min = 5 , max = 100 , message = "Email must be between 5 and 100 characters")
        @Email(message = "Invalid email format")
        String email
) {
}
