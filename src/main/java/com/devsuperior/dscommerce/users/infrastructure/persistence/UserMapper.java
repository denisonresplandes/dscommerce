package com.devsuperior.dscommerce.users.infrastructure.persistence;

import com.devsuperior.dscommerce.users.domain.User;
import com.devsuperior.dscommerce.users.domain.vo.*;

import java.util.Objects;
import java.util.Optional;

/**
 * Converte entre o modelo de domínio ({@link User}) e o de persistência ({@link UserEntity}).
 *
 * <p>É o que permite manter os dois modelos separados: o domínio tem value objects e comportamento; a entidade é
 * só um espelho das colunas. {@code toDomain} <b>revalida</b> os dados ao reconstruir os value objects — vale
 * ao carregar um agregado para uma operação de escrita. Para leituras isso é dispensado (veja
 * {@code UserQueryRepository}), e por isso a antiga conversão para "summary" deixou de existir.
 */
public final class UserMapper {

    private UserMapper() {}

    public static UserEntity toEntity(User user) {
        Objects.requireNonNull(user, "user is required");
        return new UserEntity(
                Optional.ofNullable(user.getId())
                        .map(UserId::value)
                        .orElse(null),
                user.getName().value(),
                user.getEmail().value(),
                user.getPhone().value(),
                user.getDateOfBirth().value(),
                user.getPasswordHash().value()
        );
    }

    public static User toDomain(UserEntity entity) {
        Objects.requireNonNull(entity, "entity is required");
        return User.restore(
                new UserId(entity.getId()),
                new PersonalInfo(
                        new Name(entity.getName()),
                        new DateOfBirth(entity.getDateOfBirth())
                ),
                new ContactsInfo(
                        new Email(entity.getEmail()),
                        new Phone(entity.getPhone())
                ),
                new Credentials(
                        new PasswordHash(entity.getPasswordHash())
                )
        );
    }
}