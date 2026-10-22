package com.devsuperior.dscommerce.users.domain.vo;

import com.devsuperior.dscommerce.shared.domain.DomainException;

public record Phone(String value) {

    public Phone {
        // validação e normalização
        DomainException.requireNonNull(value, "user phone number is required");
        value = value.trim().replaceAll("\\D", "");
        if (value.isBlank()) {
            throw new DomainException("user phone number cannot be blank");
        }
        // regra de domínio local para o Brasil.
        if (value.length() != 11) {
            throw new DomainException("user phone number length must be 11 digits");
        }
    }
}
