package com.devsuperior.dscommerce.users.commands.create;

import java.time.LocalDate;

public record CreateUserCommand(
        String name,
        String email,
        String phone,
        LocalDate dateOfBirth,
        String password
) {
}
