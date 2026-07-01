package com.devsuperior.dscommerce.users.infrastructure.security;

import com.devsuperior.dscommerce.users.domain.UserPasswordHasher;
import com.devsuperior.dscommerce.users.domain.vo.PasswordHash;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class Argon2UserPasswordHasher implements UserPasswordHasher {

    private final PasswordEncoder encoder;

    public Argon2UserPasswordHasher(PasswordEncoder encoder) {
        this.encoder = encoder;
    }

    @Override
    public PasswordHash toHash(String password) {
        return new PasswordHash(encoder.encode(password));
    }

    @Override
    public boolean matches(String rawPassword, PasswordHash passwordHash) {
        return encoder.matches(rawPassword, passwordHash.value());
    }
}
