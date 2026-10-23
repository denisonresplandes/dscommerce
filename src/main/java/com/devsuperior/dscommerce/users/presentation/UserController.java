package com.devsuperior.dscommerce.users.presentation;

import com.devsuperior.dscommerce.users.application.UserCommandService;
import com.devsuperior.dscommerce.users.application.UserQueryService;
import com.devsuperior.dscommerce.users.application.view.UserView;
import com.devsuperior.dscommerce.users.presentation.dto.CreateUserRequest;
import com.devsuperior.dscommerce.users.presentation.dto.UserResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * Controller REST do recurso {@code /users}.
 *
 * <p><b>Um controller por recurso.</b> Antes havia quatro controllers (um por endpoint) e quatro handlers. Em
 * camadas, o controller agrupa os endpoints do mesmo recurso e continua <b>fino</b>: recebe a requisição, chama o
 * service e devolve a resposta. Nenhuma regra de negócio mora aqui, e ele não conhece {@code domain} nem
 * {@code infrastructure}.
 *
 * <p>Erros não são tratados aqui: as exceções sobem e o {@code GlobalExceptionHandler} as converte em HTTP.
 */
@RestController
@RequestMapping("/users")
@Validated
public class UserController {

    private final UserCommandService commandService;
    private final UserQueryService queryService;

    public UserController(UserCommandService commandService, UserQueryService queryService) {
        this.commandService = commandService;
        this.queryService = queryService;
    }

    @PostMapping
    public ResponseEntity<UserResponse> create(@RequestBody @Valid CreateUserRequest request,
                                               UriComponentsBuilder uriBuilder) {
        UserView created = commandService.create(request.toCommand());
        UserResponse response = UserWebMapper.toResponse(created);

        URI location = uriBuilder.path("/users/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    public List<UserResponse> findAll() {
        return queryService.findAll()
                .stream()
                .map(UserWebMapper::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public UserResponse findById(@PathVariable @NotNull @Positive Long id) {
        return UserWebMapper.toResponse(queryService.findById(id));
    }

    // "/email" é um segmento literal: o Spring o prioriza sobre "/{id}", então não há ambiguidade.
    @GetMapping("/email")
    public UserResponse findByEmail(@RequestParam @NotBlank String email) {
        return UserWebMapper.toResponse(queryService.findByEmail(email));
    }
}
