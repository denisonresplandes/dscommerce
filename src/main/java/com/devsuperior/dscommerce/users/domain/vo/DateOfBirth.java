package com.devsuperior.dscommerce.users.domain.vo;

import java.time.LocalDate;
import java.util.Objects;

public record DateOfBirth(LocalDate value) {

    public DateOfBirth {
        Objects.requireNonNull(value, "user date of birth is required");
        if (value.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("user date of birth cannot be later than now.");
        }
    }
}
