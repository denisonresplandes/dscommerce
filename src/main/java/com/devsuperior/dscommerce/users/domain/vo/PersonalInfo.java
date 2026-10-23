package com.devsuperior.dscommerce.users.domain.vo;

import com.devsuperior.dscommerce.shared.domain.DomainException;

import java.time.LocalDate;
import java.time.Period;

public record PersonalInfo(
        Name name,
        DateOfBirth dateOfBirth
        // demais atributos.
) {
    public static final int AGE_OF_MAJORITY = 18;

    public PersonalInfo {
        DomainException.requireNonNull(name, "user name is required");
        DomainException.requireNonNull(dateOfBirth, "user date of birth is required");
    }

    public  boolean isAdult() {
        return Period.between(
                dateOfBirth.value(),
                LocalDate.now()
        ).getYears() > AGE_OF_MAJORITY;
    }
}
