package com.devsuperior.dscommerce.users.queries.findbyemail;

import com.devsuperior.dscommerce.users.domain.User;

import java.util.Objects;

public final class FindUserByEmailResponseMapper {

    private FindUserByEmailResponseMapper() {}

    public static FindUserByEmailResponse toResponse(User user) {
        Objects.requireNonNull(user, "user is required");
        return new FindUserByEmailResponse(
                user.getId().value(),
                user.getName().value(),
                user.getEmail().value(),
                user.getPhone().value(),
                user.getDateOfBirth().value()
        );
    }
}
