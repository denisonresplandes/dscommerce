package com.devsuperior.dscommerce.users.queries;

import com.devsuperior.dscommerce.users.domain.User;
import java.util.Objects;

public final class FindUserResponseMapper {

    private FindUserResponseMapper() {}

    public static FindUserResponse toResponse(User user) {
        Objects.requireNonNull(user, "user is required");
        return new FindUserResponse(
                user.getId().value(),
                user.getName().value(),
                user.getEmail().value(),
                user.getPhone().value(),
                user.getDateOfBirth().value()
        );
    }
}
