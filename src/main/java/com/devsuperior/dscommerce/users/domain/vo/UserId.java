package com.devsuperior.dscommerce.users.domain.vo;

import com.devsuperior.dscommerce.shared.domain.DomainException;

public record UserId(
        Long value
) {
    public UserId {
        DomainException.requirePositive(value, "user id must be a positive number.");
    }
}
