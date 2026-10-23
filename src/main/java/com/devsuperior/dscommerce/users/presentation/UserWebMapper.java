package com.devsuperior.dscommerce.users.presentation;

import com.devsuperior.dscommerce.users.application.view.UserView;
import com.devsuperior.dscommerce.users.presentation.dto.UserResponse;

/**
 * Converte o modelo da aplicação ({@link UserView}) no DTO da API ({@link UserResponse}).
 *
 * <p>Parece redundante hoje, porque os campos são os mesmos. O valor está em manter os dois modelos
 * <b>independentes</b>: amanhã a API pode formatar a data, esconder um campo ou ganhar uma versão {@code v2}
 * sem que a camada de aplicação saiba — só este mapper muda.
 */
final class UserWebMapper {

    private UserWebMapper() {}

    static UserResponse toResponse(UserView view) {
        return new UserResponse(
                view.id(),
                view.name(),
                view.email(),
                view.phone(),
                view.dateOfBirth()
        );
    }
}
