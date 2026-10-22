package com.devsuperior.dscommerce.users.exceptions;

import com.devsuperior.dscommerce.shared.application.NotFoundException;

public class UserNotFoundException extends NotFoundException {

    public UserNotFoundException(Long id) {
        super("User with id %d not found.".formatted(id));
    }

    public UserNotFoundException(String email) {
        super("User with email %s not found.".formatted(email));
    }
}
