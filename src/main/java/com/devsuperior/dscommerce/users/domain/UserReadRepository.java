package com.devsuperior.dscommerce.users.domain;

import com.devsuperior.dscommerce.users.domain.vo.Email;
import com.devsuperior.dscommerce.users.domain.vo.UserId;

import java.util.List;
import java.util.Optional;

public interface UserReadRepository {

    Optional<UserSummary> findById(UserId id);

    Optional<UserSummary> findByEmail(Email email);

    List<UserSummary> findAll();

    boolean existsById(UserId id);
}
