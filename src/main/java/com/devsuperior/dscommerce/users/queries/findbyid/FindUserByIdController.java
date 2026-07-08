package com.devsuperior.dscommerce.users.queries.findbyid;

import com.devsuperior.dscommerce.users.queries.FindUserResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class FindUserByIdController {

    private final FindUserByIdHandler handler;

    public FindUserByIdController(FindUserByIdHandler handler) {
        this.handler = handler;
    }

    @GetMapping("/{id}")
    public FindUserResponse findById(@PathVariable Long id) {
        return handler.execute(new FindUserByIdQuery(id));
    }
}
