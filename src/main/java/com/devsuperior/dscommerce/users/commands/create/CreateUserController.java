package com.devsuperior.dscommerce.users.commands.create;

import com.devsuperior.dscommerce.users.commands.create.dto.CreateUserRequest;
import com.devsuperior.dscommerce.users.commands.create.dto.CreateUserResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/users")
public class CreateUserController {

    private final CreateUserCommandHandler handler;

    public CreateUserController(CreateUserCommandHandler handler) {
        this.handler = handler;
    }

    @PostMapping
    public ResponseEntity<CreateUserResponse> create(@RequestBody @Valid CreateUserRequest request,
                                                     UriComponentsBuilder uriBuilder) {
        CreateUserCommand command = CreateUserRequestMapper.toCommand(request);
        CreateUserResponse response = handler.execute(command);
        URI location = uriBuilder.path("/users/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(location).body(response);
    }
}