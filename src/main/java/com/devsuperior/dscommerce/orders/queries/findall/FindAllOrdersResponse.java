package com.devsuperior.dscommerce.orders.queries.findall;

import com.devsuperior.dscommerce.orders.domain.OrderStatus;

import java.time.LocalDateTime;

public record FindAllOrdersResponse(
        Long id,
        Long userId,
        OrderStatus status,
        LocalDateTime createdAt
) {
}
