package com.devsuperior.dscommerce.users.commands.create;

import com.devsuperior.dscommerce.users.commands.create.dto.CreateUserResponse;
import com.devsuperior.dscommerce.users.domain.User;
import com.devsuperior.dscommerce.users.domain.UserPasswordHasher;
import com.devsuperior.dscommerce.users.domain.UserRepository;
import com.devsuperior.dscommerce.users.domain.vo.*;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class CreateUserHandler {

    private final UserPasswordHasher hasher;
    private final UserRepository userRepository;

    public CreateUserHandler(UserPasswordHasher hasher, UserRepository userRepository) {
        this.hasher = hasher;
        this.userRepository = userRepository;
    }

    public CreateUserResponse execute(CreateUserCommand command) {
        Objects.requireNonNull(command, "create user command is required");
        User newUser = createUser(command);
        User userSaved = userRepository.save(newUser);
        return CreateUserResponseMapper.toResponse(userSaved);
    }

    private User createUser(CreateUserCommand command) {
        return User.create(
                new PersonalInfo(
                        new Name(command.name()),
                        new DateOfBirth(command.dateOfBirth())
                ),
                new ContactsInfo(
                        new Email(command.email()),
                        new Phone(command.phone())
                ),
                new Credentials(
                        hasher.toHash(command.password())
                )
        );
    }
}
