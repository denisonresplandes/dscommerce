package com.devsuperior.dscommerce.users.infrastructure.persistence;

import java.time.LocalDate;

public interface UserSummaryProjection {

    Long getId();

    String getName();

    LocalDate getDateOfBirth();

    String getEmail();

    String getPhone();
}
