package com.devsuperior.dscommerce.users.commands.create;

import com.devsuperior.dscommerce.users.commands.create.dto.CreateUserResponse;
import com.devsuperior.dscommerce.users.domain.User;

public final class CreateUserMapper {

    private CreateUserMapper() {}

    public static CreateUserResponse toResponse(User user) {
        return new CreateUserResponse(
                user.getId().value(),
                user.getName().value(),
                user.getEmail().value(),
                user.getPhone().value(),
                user.getDateOfBirth().value()
        );
    }
}
