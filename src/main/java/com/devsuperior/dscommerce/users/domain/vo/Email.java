package com.devsuperior.dscommerce.users.domain.vo;

import com.devsuperior.dscommerce.shared.domain.DomainException;

import java.util.regex.Pattern;

public record Email(String value) {

    /**
     * Tamanho máximo, igual ao da coluna {@code tb_user.email} ({@code length = 100}). Sem este limite, um e-mail
     * longo passava pela validação e só falhava no banco, devolvendo erro 500 ao cliente. (O RFC permite até 254
     * caracteres, mas o schema do projeto é a restrição real.)
     */
    public static final int MAX_LENGTH = 100;

    private static final Pattern PATTERN = Pattern
            .compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    public Email {
        // validação e normalização
        DomainException.requireNonNull(value, "user mail address is required");
        value = value.trim().toLowerCase();
        if (value.length() > MAX_LENGTH) {
            throw new DomainException("user email address is too long.");
        }
        if (value.isBlank() || !PATTERN.matcher(value).matches()) {
            throw new DomainException("invalid user email address");
        }
    }
}
