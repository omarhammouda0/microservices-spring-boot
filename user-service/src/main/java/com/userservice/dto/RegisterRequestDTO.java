package com.userservice.dto;

import com.userservice.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequestDTO(

        @NotNull (message = "The name can not be empty")
        @Size (min = 5 , max = 100 , message = "The name should be between 5 and 100 characters")
        String name ,

        @NotNull (message = "The email can not be empty")
        @Email (message = "Please enter a valid email format")
        String email ,

        @NotNull (message = "The password can not be empty")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{10,}$",
                message = "Password must be at least 10 characters long " +
                        "and contain at least one uppercase letter," +
                        " one lowercase letter," +
                        " one digit," +
                        " and one special character" )
        String password ,

        @NotNull (message = "The role can not be empty")
        Role role

) { }
