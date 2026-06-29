package com.devsuperior.dscommerce.users.commands.create;

import com.devsuperior.dscommerce.users.commands.create.dto.CreateUserResponse;
import com.devsuperior.dscommerce.users.domain.User;
import com.devsuperior.dscommerce.users.domain.UserRepository;
import com.devsuperior.dscommerce.users.domain.vo.*;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class CreateUserCommandHandler {

    private final UserRepository userRepository;

    public CreateUserCommandHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public CreateUserResponse execute(CreateUserCommand command) {
        Objects.requireNonNull(command, "create user command is required");
        User newUser = User.create(
                new Name(command.name()),
                new Email(command.email()),
                new Phone(command.phone()),
                new DateOfBirth(command.dateOfBirth()),
                new PasswordHash(command.password())
        );
        User userSaved = userRepository.save(newUser);
        return CreateUserResponseMapper.toResponse(userSaved);
    }
}
