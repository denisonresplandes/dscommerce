package com.devsuperior.dscommerce.users.queries.findall;

import com.devsuperior.dscommerce.users.domain.User;

import java.util.Objects;

public final class FindAllUserResponseMapper {

    private FindAllUserResponseMapper() {}

    public static FindAllUserResponse toResponse(User user) {
        Objects.requireNonNull(user, "user is required");
        return new FindAllUserResponse(
                user.getId().value(),
                user.getName().value(),
                user.getEmail().value(),
                user.getPhone().value(),
                user.getDateOfBirth().value()
        );
    }
}