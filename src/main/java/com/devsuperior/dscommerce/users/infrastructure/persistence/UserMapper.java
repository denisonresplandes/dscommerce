package com.devsuperior.dscommerce.users.infrastructure.persistence;

import com.devsuperior.dscommerce.users.domain.User;
import com.devsuperior.dscommerce.users.domain.vo.*;

import java.util.Objects;
import java.util.Optional;

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