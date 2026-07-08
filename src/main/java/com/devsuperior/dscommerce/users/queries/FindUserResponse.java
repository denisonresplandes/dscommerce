package com.devsuperior.dscommerce.users.queries;

import java.time.LocalDate;

public record FindUserResponse(
        Long id,
        String name,
        String email,
        String phone,
        LocalDate dateOfBirth
) {
}
