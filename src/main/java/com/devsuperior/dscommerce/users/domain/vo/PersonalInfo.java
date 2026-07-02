package com.devsuperior.dscommerce.users.domain.vo;

import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;

public record PersonalInfo(
        Name name,
        DateOfBirth dateOfBirth
        // demais atributos.
) {
    public static final int AGE_OF_MAJORITY = 18;

    public PersonalInfo {
        Objects.requireNonNull(name, "user name is required");
        Objects.requireNonNull(dateOfBirth, "user date of birth is required");
    }

    public  boolean isAdult() {
        return Period.between(
                dateOfBirth.value(),
                LocalDate.now()
        ).getYears() > AGE_OF_MAJORITY;
    }
}
