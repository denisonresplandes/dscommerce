package com.devsuperior.dscommerce.users.domain.vo;

import java.util.Objects;

public record PasswordHash(String value) {

    public PasswordHash {
        Objects.requireNonNull(value, "password hash is required");
        if (value.isBlank()) {
            throw new IllegalArgumentException("password hash cannot be blank.");
        }
        // Verificação básica
        // Hashes Argon2 sempre começam com $argon2id$ ou similar.
        // Hashes BCrypt sempre começam com $b$ ou similar.
        if (!value.startsWith("$argon2") && !value.startsWith("$b$")) {
            throw new IllegalArgumentException("The provided string is not a valid hash. " +
                    "Did you forget to encode the password?");
        }
    }

    @Override
    public String toString() {
        return "**********";
    }
}
