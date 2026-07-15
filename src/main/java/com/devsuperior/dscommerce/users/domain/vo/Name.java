package com.devsuperior.dscommerce.users.domain.vo;

import java.util.Objects;

public record Name(String value) {

    public Name {
        Objects.requireNonNull(value, "user name is required");
        if (value.isBlank()) {
            throw new IllegalArgumentException("user name is required");
        }
        if (value.length() < 2) {
            throw new IllegalArgumentException("user name is too short.");
        }
        if (value.matches(".*\\d.*")) {
            throw new IllegalArgumentException("user name cannot contain numbers.");
        }
    }
}
