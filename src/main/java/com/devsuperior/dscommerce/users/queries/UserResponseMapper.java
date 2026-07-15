package com.devsuperior.dscommerce.users.queries;

import com.devsuperior.dscommerce.users.domain.UserSummary;

import java.util.Objects;

public final class UserResponseMapper {

    private UserResponseMapper() {
    }

    public static UserResponse toResponse(UserSummary summary) {
        Objects.requireNonNull(summary, "user summary is required");
        return new UserResponse(
                summary.id().value(),
                summary.personalInfo().name().value(),
                summary.contactsInfo().email().value(),
                summary.contactsInfo().phone().value(),
                summary.personalInfo().dateOfBirth().value()
        );
    }
}
