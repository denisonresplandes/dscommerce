package com.devsuperior.dscommerce.users.queries.findbyemail;

import com.devsuperior.dscommerce.shared.domain.DomainException;
import com.devsuperior.dscommerce.users.domain.UserReadRepository;
import com.devsuperior.dscommerce.users.domain.vo.Email;
import com.devsuperior.dscommerce.users.exceptions.UserNotFoundException;
import com.devsuperior.dscommerce.users.queries.UserResponse;
import com.devsuperior.dscommerce.users.queries.UserResponseMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FindUserByEmailHandler {

    private final UserReadRepository userReadRepository;

    public FindUserByEmailHandler(UserReadRepository userReadRepository) {
        this.userReadRepository = userReadRepository;
    }

    @Transactional(readOnly = true)
    public UserResponse execute(FindUserByEmailQuery query) {
        DomainException.requireNonNull(query, "user by email query cannot be null");
        return userReadRepository.findByEmail(new Email(query.email()))
                .map(UserResponseMapper::toResponse)
                .orElseThrow(() -> new UserNotFoundException(
                        query.email()
                )
        );
    }
}