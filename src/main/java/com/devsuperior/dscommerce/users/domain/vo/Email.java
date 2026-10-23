package com.devsuperior.dscommerce.users.domain.vo;

import com.devsuperior.dscommerce.shared.domain.DomainException;

import java.util.regex.Pattern;

public record Email(String value) {

    private static final Pattern PATTERN = Pattern
            .compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    public Email {
        // validação e normalização
        DomainException.requireNonNull(value, "user mail address is required");
        value = value.trim().toLowerCase();
        if (value.isBlank() || !PATTERN.matcher(value).matches()) {
            throw new DomainException("invalid user email address");
        }
    }
}
