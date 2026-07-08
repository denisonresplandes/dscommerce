package com.devsuperior.dscommerce.users.queries.findbyemail;

import com.devsuperior.dscommerce.users.queries.FindUserResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class FindUserByEmailController {

    private final FindUserByEmailHandler handler;

    public FindUserByEmailController(FindUserByEmailHandler handler) {
        this.handler = handler;
    }

    @GetMapping("/email")
    public FindUserResponse findByEmail(@RequestParam String email) {
        return handler.execute(new FindUserByEmailQuery(email));
    }
}
