package com.devsuperior.dscommerce.users.domain.vo;

import java.util.Objects;
import java.util.regex.Pattern;

public record Email(String value) {

    private static final Pattern PATTERN = Pattern
            .compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    public Email {
        // validação e normalização
        value = Objects.requireNonNull(value, "mail address is required")
                .trim()
                .toLowerCase();
        if (value.isBlank() || !PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("invalid email address");
        }
    }
}
