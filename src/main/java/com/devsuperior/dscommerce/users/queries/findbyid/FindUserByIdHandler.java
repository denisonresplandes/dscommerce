package com.devsuperior.dscommerce.users.queries.findbyid;

import com.devsuperior.dscommerce.shared.domain.DomainException;
import com.devsuperior.dscommerce.users.domain.UserReadRepository;
import com.devsuperior.dscommerce.users.domain.vo.UserId;
import com.devsuperior.dscommerce.users.exceptions.UserNotFoundException;
import com.devsuperior.dscommerce.users.queries.UserResponse;
import com.devsuperior.dscommerce.users.queries.UserResponseMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FindUserByIdHandler {

    private final UserReadRepository userReadRepository;

    public FindUserByIdHandler(UserReadRepository userReadRepository) {
        this.userReadRepository = userReadRepository;
    }

    @Transactional(readOnly = true)
    public UserResponse execute(FindUserByIdQuery query) {
        DomainException.requireNonNull(query, "find user by id query cannot be null");
        UserId id = new UserId(query.id());
        return userReadRepository.findById(id)
                .map(UserResponseMapper::toResponse)
                .orElseThrow(() -> new UserNotFoundException(
                        id.value()
                )
        );
    }
}