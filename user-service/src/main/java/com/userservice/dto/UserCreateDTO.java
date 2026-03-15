package com.userservice.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserCreateDTO(

        @NotBlank (message = "Name cannot be blank")
        @Size(min = 4 , max = 50 , message = "Name must be between 4 and 50 characters")
        String name,

        @NotBlank (message = "Email cannot be blank")
        @Size (min = 5 , max = 100 , message = "Email must be between 5 and 100 characters")
        @Email (message = "Invalid email format")
        String email

) {
}



