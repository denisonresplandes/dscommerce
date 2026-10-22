package com.devsuperior.dscommerce.orders.exceptions;

import com.devsuperior.dscommerce.shared.application.NotFoundException;

public class OrderNotFoundException extends NotFoundException {

    public OrderNotFoundException(Long id) {
        super("Order with id %d not found.".formatted(id));
    }
}
