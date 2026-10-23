package com.devsuperior.dscommerce.users.domain.vo;

import com.devsuperior.dscommerce.shared.domain.DomainException;

public record PasswordHash(String value) {

    public PasswordHash {
        DomainException.requireNonNull(value, "user password hash is required");
        if (value.isBlank()) {
            throw new DomainException("user password hash cannot be blank.");
        }
        // Verificação básica
        // Hashes Argon2 sempre começam com $argon2id$ ou similar.
        // Hashes BCrypt sempre começam com $b$ ou similar.
        if (!value.startsWith("$argon2") && !value.startsWith("$b$")) {
            throw new DomainException("The provided string is not a valid hash. " +
                    "Did you forget to encode the password?");
        }
    }

    @Override
    public String toString() {
        return "**********";
    }
}
