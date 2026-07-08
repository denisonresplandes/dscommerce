package com.devsuperior.dscommerce.users.commands.create;

import com.devsuperior.dscommerce.users.commands.create.dto.CreateUserResponse;
import com.devsuperior.dscommerce.users.domain.User;

import java.util.Objects;

public final class CreateUserResponseMapper {

    private CreateUserResponseMapper() {}

    public static CreateUserResponse toResponse(User user) {
        Objects.requireNonNull(user, "user response is required");
        return new CreateUserResponse(
                user.getId().value(),
                user.getName().value(),
                user.getEmail().value(),
                user.getPhone().value(),
                user.getDateOfBirth().value()
        );
    }
}
