package com.devsuperior.dscommerce.users.exceptions;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(Long id) {
        super("User with id %d not found.".formatted(id));
    }

    public UserNotFoundException(String email) {
        super("User with email %s not found.".formatted(email));
    }
}
