package com.devsuperior.dscommerce.users.queries.findbyid;

import com.devsuperior.dscommerce.users.queries.UserResponse;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@Validated
public class FindUserByIdController {

    private final FindUserByIdHandler handler;

    public FindUserByIdController(FindUserByIdHandler handler) {
        this.handler = handler;
    }

    @GetMapping("/{id}")
    public UserResponse findById(@PathVariable @NotNull @Positive Long id) {
        return handler.execute(new FindUserByIdQuery(id));
    }
}
