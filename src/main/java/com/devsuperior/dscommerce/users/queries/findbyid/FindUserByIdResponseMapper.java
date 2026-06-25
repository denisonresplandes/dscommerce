package com.devsuperior.dscommerce.users.queries.findbyid;

import com.devsuperior.dscommerce.users.domain.User;

import java.util.Objects;

public final class FindUserByIdResponseMapper {

    private FindUserByIdResponseMapper() {}

    public static FindUserByIdResponse toResponse(User user) {
        Objects.requireNonNull(user, "user is required");
        return new FindUserByIdResponse(
                user.getId().value(),
                user.getName().value(),
                user.getEmail().value(),
                user.getPhone().value(),
                user.getDateOfBirth().value()
        );
    }
}
