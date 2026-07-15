package com.devsuperior.dscommerce.orders.exceptions;

public class OrderNotFoundException extends RuntimeException {

    public OrderNotFoundException(Long id) {
        super("Order with id %d not found.".formatted(id));
    }
}
