package com.devsuperior.dscommerce.users.commands.create;

import com.devsuperior.dscommerce.shared.domain.DomainException;
import com.devsuperior.dscommerce.users.commands.create.dto.CreateUserCommand;
import com.devsuperior.dscommerce.users.commands.create.dto.CreateUserResponse;
import com.devsuperior.dscommerce.users.domain.User;
import com.devsuperior.dscommerce.users.domain.UserPasswordHasher;
import com.devsuperior.dscommerce.users.domain.UserWriteRepository;
import com.devsuperior.dscommerce.users.domain.vo.*;
import org.springframework.stereotype.Service;

@Service
public class CreateUserHandler {

    private final UserPasswordHasher hasher;
    private final UserWriteRepository userWriteRepository;

    public CreateUserHandler(UserPasswordHasher hasher, UserWriteRepository userWriteRepository) {
        this.hasher = hasher;
        this.userWriteRepository = userWriteRepository;
    }

    public CreateUserResponse execute(CreateUserCommand command) {
        DomainException.requireNonNull(command, "create user command cannot be null");
        User newUser = createUser(command);
        User userSaved = userWriteRepository.save(newUser);
        return CreateUserMapper.toResponse(userSaved);
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
