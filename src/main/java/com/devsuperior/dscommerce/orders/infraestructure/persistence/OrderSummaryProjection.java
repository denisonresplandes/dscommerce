package com.devsuperior.dscommerce.orders.infraestructure.persistence;

import com.devsuperior.dscommerce.orders.domain.OrderStatus;

import java.time.LocalDateTime;

public interface OrderSummaryProjection {

    Long getId();

    Long getUserId();

    OrderStatus getStatus();

    LocalDateTime getCreatedAt();
}
