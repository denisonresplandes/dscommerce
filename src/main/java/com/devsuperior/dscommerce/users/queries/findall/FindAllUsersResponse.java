package com.devsuperior.dscommerce.users.queries.findall;

import java.time.LocalDate;

public record FindAllUsersResponse(
        Long id,
        String name,
        String email,
        String phone,
        LocalDate dateOfBirth
) {
}
