package com.devsuperior.dscommerce.users.application.exception;

import com.devsuperior.dscommerce.shared.application.ConflictException;

/**
 * Já existe um usuário cadastrado com este e-mail. Vira HTTP 409 (Conflict) no {@code GlobalExceptionHandler}.
 *
 * <p>É um {@link ConflictException} (e não uma {@code DomainException}) porque o e-mail em si é válido: o que
 * falha é a colisão com um dado que já existe, algo que um value object isolado não tem como saber.
 */
public class EmailAlreadyInUseException extends ConflictException {

    public EmailAlreadyInUseException(String email) {
        super("A user with e-mail %s already exists.".formatted(email));
    }
}
