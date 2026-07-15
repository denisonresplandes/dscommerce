package com.devsuperior.dscommerce.users.domain.vo;

import java.util.Objects;

public record UserId(
        Long value
) {
    public UserId {
        Objects.requireNonNull(value, "user id is required");
        if (value <= 0) {
            throw new IllegalArgumentException("user id can't be less or equal zero");
        }
    }
}
