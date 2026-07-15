package com.devsuperior.dscommerce.users.domain;

import com.devsuperior.dscommerce.users.domain.vo.UserId;

public interface UserWriteRepository {

    User save(User user);

    void delete(User user);

    void deleteById(UserId id);
}
