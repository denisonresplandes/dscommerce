package com.devsuperior.dscommerce.users.queries.findbyemail;

import java.time.LocalDate;

public record FindUserByEmailResponse(
        Long id,
        String name,
        String email,
        String phone,
        LocalDate dateOfBirth
) {
}
