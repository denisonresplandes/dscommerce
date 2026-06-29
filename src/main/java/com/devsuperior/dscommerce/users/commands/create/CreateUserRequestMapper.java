package com.devsuperior.dscommerce.users.commands.create;

import com.devsuperior.dscommerce.users.commands.create.dto.CreateUserRequest;

import java.util.Objects;

public final class CreateUserRequestMapper {

    private CreateUserRequestMapper() {}

    public static CreateUserCommand toCommand(CreateUserRequest request) {
        Objects.requireNonNull(request, "create user request is required");
        return new CreateUserCommand(
                request.name(),
                request.email(),
                request.phone(),
                request.dateOfBirth(),
                request.password()
        );
    }
}
