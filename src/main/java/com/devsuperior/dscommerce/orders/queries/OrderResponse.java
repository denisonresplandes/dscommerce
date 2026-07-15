package com.devsuperior.dscommerce.orders.queries;

import com.devsuperior.dscommerce.orders.domain.OrderStatus;

import java.time.LocalDateTime;

public record OrderResponse(
        Long id,
        Long userId,
        OrderStatus status,
        LocalDateTime createdAt
) {
}
