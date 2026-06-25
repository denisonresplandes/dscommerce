package com.devsuperior.dscommerce.users.domain.vo;

import java.util.Objects;

public record Phone(String value) {

    public Phone {
        // validação e normalização
        value = Objects.requireNonNull(value, "phone number is required")
                .trim()
                .replaceAll("\\D", "");
        if (value.isBlank()) {
            throw new IllegalArgumentException("phone number cannot be blank");
        }
        // regra de domínio local para o Brasil.
        if (value.length() != 11) {
            throw new IllegalArgumentException("phone number length must be 11 digits");
        }
    }
}
