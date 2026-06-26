package com.devsuperior.dscommerce.users.queries.findbyid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class FindUserByIdController {

    private final FindUserByIdQueryHandler handler;

    public FindUserByIdController(FindUserByIdQueryHandler handler) {
        this.handler = handler;
    }

    @GetMapping("/{id}")
    public FindUserByIdResponse findById(@PathVariable Long id) {
        return handler.execute(new FindUserByIdQuery(id));
    }
}
