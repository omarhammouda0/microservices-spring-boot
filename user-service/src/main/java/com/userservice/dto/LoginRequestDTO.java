package com.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(

        @NotBlank (message = "Please enter an email address")
        @Email (message = "Please enter a valid email format")
        String email ,

        @NotBlank (message = "Please enter a password")
        String password

) {
}
