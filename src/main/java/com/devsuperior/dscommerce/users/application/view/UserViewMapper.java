package com.devsuperior.dscommerce.users.application.view;

import com.devsuperior.dscommerce.users.domain.User;

/**
 * Converte o agregado {@link User} em {@link UserView}.
 *
 * <p>Só é usado no caso de uso de <b>escrita</b>: depois de salvar, o {@code UserCommandService} devolve uma view
 * (e não o agregado) para que as camadas externas nunca toquem no objeto de domínio. As consultas não passam por
 * aqui — o banco já devolve {@link UserView} pronto.
 */
public final class UserViewMapper {

    private UserViewMapper() {}

    public static UserView toView(User user) {
        return new UserView(
                user.getId().value(),
                user.getName().value(),
                user.getEmail().value(),
                user.getPhone().value(),
                user.getDateOfBirth().value()
        );
    }
}
