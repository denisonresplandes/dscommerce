package com.devsuperior.dscommerce.users.commands.create.dto;

import java.time.LocalDate;

public record CreateUserResponse(
        Long id,
        String name,
        String email,
        String phone,
        LocalDate dateOfBirth
) {
}
