package com.devsuperior.dscommerce.users.domain.vo;

import com.devsuperior.dscommerce.shared.domain.DomainException;

public record PasswordHash(String value) {

    public PasswordHash {
        DomainException.requireNonNull(value, "user password hash is required");
        if (value.isBlank()) {
            throw new DomainException("user password hash cannot be blank.");
        }
        // Verificação básica: este valor TEM que ser um hash, nunca uma senha em texto puro.
        // O projeto gera apenas hashes Argon2 (Argon2UserPasswordHasher), cujo formato começa sempre com
        // "$argon2id$" (ou "$argon2i$" / "$argon2d$", as outras variantes do algoritmo).
        // BUG corrigido: antes também se aceitava o prefixo "$b$", que não existe em nenhum formato real
        // (o BCrypt usa "$2a$", "$2b$" ou "$2y$") e deixava passar texto qualquer que começasse assim.
        if (!value.startsWith("$argon2")) {
            throw new DomainException("The provided string is not a valid hash. " +
                    "Did you forget to encode the password?");
        }
    }

    @Override
    public String toString() {
        return "**********";
    }
}
