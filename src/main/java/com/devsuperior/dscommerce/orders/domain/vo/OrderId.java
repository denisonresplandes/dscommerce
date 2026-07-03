package com.devsuperior.dscommerce.orders.domain.vo;

import java.util.Objects;

public record OrderId(
        Long value
) {
    public OrderId {
        Objects.requireNonNull(value, "order id is required");
        if (value <= 0) {
            throw new IllegalArgumentException("order id must be greater than zero");
        }
    }
}
