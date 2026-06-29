package com.devsuperior.dscommerce.users.queries.findall;

import com.devsuperior.dscommerce.users.domain.User;

import java.util.Objects;

public final class FindAllUsersResponseMapper {

    private FindAllUsersResponseMapper() {}

    public static FindAllUsersResponse toResponse(User user) {
        Objects.requireNonNull(user, "user is required");
        return new FindAllUsersResponse(
                user.getId().value(),
                user.getName().value(),
                user.getEmail().value(),
                user.getPhone().value(),
                user.getDateOfBirth().value()
        );
    }
}