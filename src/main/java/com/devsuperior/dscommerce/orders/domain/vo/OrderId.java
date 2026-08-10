package com.devsuperior.dscommerce.orders.domain.vo;

import com.devsuperior.dscommerce.shared.domain.DomainException;

public record OrderId(
        Long value
) {
    public OrderId {
        DomainException.requirePositive(value, "order id must be a positive number.");
    }
}
