package com.devsuperior.dscommerce.users.application.exception;

import com.devsuperior.dscommerce.shared.application.NotFoundException;

/**
 * Nenhum usuário corresponde ao identificador pedido. Vira HTTP 404 no {@code GlobalExceptionHandler}.
 *
 * <p>Fica em {@code application} (e não em {@code domain}) porque "não encontrado" é resultado de um caso de
 * uso de consulta, não violação de uma regra de negócio do agregado.
 */
public class UserNotFoundException extends NotFoundException {

    public UserNotFoundException(Long id) {
        super("User with id %d not found.".formatted(id));
    }

    public UserNotFoundException(String email) {
        super("User with email %s not found.".formatted(email));
    }
}
