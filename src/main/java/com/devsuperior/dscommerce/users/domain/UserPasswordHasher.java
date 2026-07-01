package com.devsuperior.dscommerce.users.domain;

import com.devsuperior.dscommerce.users.domain.vo.PasswordHash;

public interface UserPasswordHasher {

    PasswordHash toHash(String password);

    boolean matches(String rawPassword, PasswordHash passwordHash);
}