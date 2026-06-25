package com.devsuperior.dscommerce.users.queries.findbyid;

import java.time.LocalDate;

public record FindUserByIdResponse(
        Long id,
        String name,
        String email,
        String phone,
        LocalDate dateOfBirth
) {
}
