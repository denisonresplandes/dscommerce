package com.devsuperior.dscommerce.orders.queries;

import com.devsuperior.dscommerce.orders.domain.OrderSummary;

public final class OrderResponseMapper {

    private OrderResponseMapper() {}

    public static OrderResponse toResponse(OrderSummary summary) {
        return new OrderResponse(
                summary.id().value(),
                summary.userId().value(),
                summary.status(),
                summary.createdAt()
        );
    }
}
