package com.devsuperior.dscommerce.users.domain.vo;

import com.devsuperior.dscommerce.shared.domain.DomainException;

public record Name(String value) {

    public Name {
        DomainException.requireNonNull(value, "user name is required");
        if (value.isBlank()) {
            throw new DomainException("user name is required");
        }
        if (value.length() < 2) {
            throw new DomainException("user name is too short.");
        }
        if (value.matches(".*\\d.*")) {
            throw new DomainException("user name cannot contain numbers.");
        }
    }
}
