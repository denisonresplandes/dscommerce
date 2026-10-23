package com.devsuperior.dscommerce.orders.application.exception;

import com.devsuperior.dscommerce.shared.application.NotFoundException;

/**
 * Nenhum pedido corresponde ao identificador pedido. Vira HTTP 404 no {@code GlobalExceptionHandler}.
 */
public class OrderNotFoundException extends NotFoundException {

    public OrderNotFoundException(Long id) {
        super("Order with id %d not found.".formatted(id));
    }
}
