package com.devsuperior.dscommerce.users.queries.findbyemail;

import com.devsuperior.dscommerce.users.queries.UserResponse;
import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@Validated
public class FindUserByEmailController {

    private final FindUserByEmailHandler handler;

    public FindUserByEmailController(FindUserByEmailHandler handler) {
        this.handler = handler;
    }

    @GetMapping("/email")
    public UserResponse findByEmail(@RequestParam @NotBlank String email) {
        return handler.execute(new FindUserByEmailQuery(email));
    }
}
