package com.devsuperior.dscommerce.orders.domain;

import com.devsuperior.dscommerce.orders.domain.vo.OrderId;
import com.devsuperior.dscommerce.users.domain.vo.UserId;

import java.time.LocalDateTime;

public record OrderSummary(
        OrderId id,
        UserId userId,
        OrderStatus status,
        LocalDateTime createdAt
) {
}