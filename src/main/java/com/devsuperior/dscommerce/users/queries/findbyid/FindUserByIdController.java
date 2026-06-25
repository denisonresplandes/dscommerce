package com.devsuperior.dscommerce.users.queries.findbyid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class FindUserByIdController {

    private final FindUserByIdHandler findUserByIdHandler;

    public FindUserByIdController(FindUserByIdHandler findUserByIdHandler) {
        this.findUserByIdHandler = findUserByIdHandler;
    }

    @GetMapping("/{id}")
    public FindUserByIdResponse findById(@PathVariable Long id) {
        return findUserByIdHandler.execute(new FindUserByIdQuery(id));
    }
}
