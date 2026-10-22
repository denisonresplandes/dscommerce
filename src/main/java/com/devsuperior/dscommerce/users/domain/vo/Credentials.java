package com.devsuperior.dscommerce.users.domain.vo;

import com.devsuperior.dscommerce.shared.domain.DomainException;

public record Credentials(
        PasswordHash passwordHash
) {

    public Credentials {
        DomainException.requireNonNull(passwordHash, "user password is required");
    }

    public Credentials withPasswordHash(PasswordHash newHash) {
        return new Credentials(newHash);
    }
}
