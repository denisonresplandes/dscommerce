package com.devsuperior.dscommerce.users.queries.findbyemail;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class FindUserByEmailController {

    private final FindUserByEmailQueryHandler handler;

    public FindUserByEmailController(FindUserByEmailQueryHandler handler) {
        this.handler = handler;
    }

    @GetMapping("/email")
    public FindUserByEmailResponse findByEmail(@RequestParam String email) {
        return handler.execute(new FindUserByEmailQuery(email));
    }
}
