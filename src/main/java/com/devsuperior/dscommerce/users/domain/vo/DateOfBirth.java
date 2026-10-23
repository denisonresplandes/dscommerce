package com.devsuperior.dscommerce.users.domain.vo;

import com.devsuperior.dscommerce.shared.domain.DomainException;

import java.time.LocalDate;

public record DateOfBirth(LocalDate value) {

    public DateOfBirth {
        DomainException.requireNonNull(value, "user date of birth is required");
        if (value.isAfter(LocalDate.now())) {
            throw new DomainException("user date of birth cannot be later than now.");
        }
    }
}
