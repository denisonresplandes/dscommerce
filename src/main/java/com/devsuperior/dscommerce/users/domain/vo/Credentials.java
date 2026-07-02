package com.devsuperior.dscommerce.users.domain.vo;

import java.util.Objects;

public record Credentials(
        PasswordHash passwordHash
) {

    public Credentials {
        Objects.requireNonNull(passwordHash, "user password is required");
    }

    public Credentials withPasswordHash(PasswordHash newHash) {
        return new Credentials(newHash);
    }
}
