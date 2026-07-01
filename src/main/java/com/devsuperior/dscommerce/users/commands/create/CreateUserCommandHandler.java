package com.devsuperior.dscommerce.users.commands.create;

import com.devsuperior.dscommerce.users.commands.create.dto.CreateUserResponse;
import com.devsuperior.dscommerce.users.domain.User;
import com.devsuperior.dscommerce.users.domain.UserPasswordHasher;
import com.devsuperior.dscommerce.users.domain.UserRepository;
import com.devsuperior.dscommerce.users.domain.vo.DateOfBirth;
import com.devsuperior.dscommerce.users.domain.vo.Email;
import com.devsuperior.dscommerce.users.domain.vo.Name;
import com.devsuperior.dscommerce.users.domain.vo.Phone;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class CreateUserCommandHandler {

    private final UserPasswordHasher hasher;
    private final UserRepository userRepository;

    public CreateUserCommandHandler(UserPasswordHasher hasher, UserRepository userRepository) {
        this.hasher = hasher;
        this.userRepository = userRepository;
    }

    public CreateUserResponse execute(CreateUserCommand command) {
        Objects.requireNonNull(command, "create user command is required");
        User newUser = User.create(
                new Name(command.name()),
                new Email(command.email()),
                new Phone(command.phone()),
                new DateOfBirth(command.dateOfBirth()),
                hasher.toHash(command.password())
        );
        User userSaved = userRepository.save(newUser);
        return CreateUserResponseMapper.toResponse(userSaved);
    }
}
