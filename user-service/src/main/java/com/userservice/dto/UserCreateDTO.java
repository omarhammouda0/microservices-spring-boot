package com.userservice.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Payload used by an ADMIN to create a new user account.")
public record UserCreateDTO(

        @Schema(
                description = "Full name of the user.",
                example = "Jane Doe",
                minLength = 4,
                maxLength = 50,
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "Name cannot be blank")
        @Size(min = 4, max = 50, message = "Name must be between 4 and 50 characters")
        String name,

        @Schema(
                description = "Unique email address used as the login identifier.",
                example = "jane.doe@example.com",
                minLength = 5,
                maxLength = 100,
                format = "email",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "Email cannot be blank")
        @Size(min = 5, max = 100, message = "Email must be between 5 and 100 characters")
        @Email(message = "Invalid email format")
        String email

) {
}
