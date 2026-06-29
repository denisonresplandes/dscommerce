package com.devsuperior.dscommerce.users.commands.create.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record CreateUserRequest(
        @NotBlank
        String name,

        @NotBlank
        @Email
        String email,

        @NotBlank
        @Pattern(
                regexp = "^\\d{11}$",
                message = "O campo deve conter 11 dígitos"
        )
        String phone,

        @NotNull
        @PastOrPresent
        LocalDate dateOfBirth,

        @NotBlank
        @Size(min = 6, max = 20)
        String password
) {
}
