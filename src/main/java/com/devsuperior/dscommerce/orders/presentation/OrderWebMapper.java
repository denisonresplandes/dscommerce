package com.devsuperior.dscommerce.orders.presentation;

import com.devsuperior.dscommerce.orders.application.view.OrderView;
import com.devsuperior.dscommerce.orders.presentation.dto.OrderResponse;

/**
 * Converte {@link OrderView} (modelo da aplicação, vocabulário {@code clientId}) em {@link OrderResponse}
 * (contrato público, campo {@code userId}). É um exemplo concreto de por que manter os dois modelos separados:
 * o nome interno mudou e o JSON da API não.
 */
final class OrderWebMapper {

    private OrderWebMapper() {}

    static OrderResponse toResponse(OrderView view) {
        return new OrderResponse(
                view.id(),
                view.clientId(),
                view.status(),
                view.createdAt()
        );
    }
}
