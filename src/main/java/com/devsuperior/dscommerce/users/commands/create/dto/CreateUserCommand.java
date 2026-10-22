package com.devsuperior.dscommerce.users.commands.create.dto;

import java.time.LocalDate;

public record CreateUserCommand(
        String name,
        String email,
        String phone,
        LocalDate dateOfBirth,
        String password
) {
}
