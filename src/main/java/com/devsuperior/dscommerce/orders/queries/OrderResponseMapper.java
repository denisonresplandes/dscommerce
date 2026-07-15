package com.devsuperior.dscommerce.orders.queries;

import com.devsuperior.dscommerce.orders.domain.OrderSummary;

import java.util.Objects;

public final class OrderResponseMapper {

    private OrderResponseMapper() {}

    public static OrderResponse toResponse(OrderSummary summary) {
        Objects.requireNonNull(summary, "order summary is required");
        return new OrderResponse(
                summary.id().value(),
                summary.userId().value(),
                summary.status(),
                summary.createdAt()
        );
    }
}
