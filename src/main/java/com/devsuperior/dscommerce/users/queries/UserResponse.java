package com.devsuperior.dscommerce.users.queries;

import java.time.LocalDate;

public record UserResponse(
        Long id,
        String name,
        String email,
        String phone,
        LocalDate dateOfBirth
) {
}
