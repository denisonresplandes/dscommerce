package com.devsuperior.dscommerce.users.queries.findall;

import com.devsuperior.dscommerce.users.queries.UserResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users")
public class FindAllUsersController {

    private final FindAllUsersHandler handler;

    public FindAllUsersController(FindAllUsersHandler handler) {
        this.handler = handler;
    }

    @GetMapping
    public List<UserResponse> findAll() {
        return handler.execute();
    }
}
