package com.devsuperior.dscommerce.users.queries.findbyemail;

import com.devsuperior.dscommerce.users.domain.UserRepository;
import com.devsuperior.dscommerce.users.domain.vo.Email;
import com.devsuperior.dscommerce.users.exceptions.UserNotFoundException;
import com.devsuperior.dscommerce.users.queries.FindUserResponse;
import com.devsuperior.dscommerce.users.queries.FindUserResponseMapper;
import org.springframework.stereotype.Service;

@Service
public class FindUserByEmailHandler {

    private final UserRepository userRepository;

    public FindUserByEmailHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public FindUserResponse execute(FindUserByEmailQuery query) {
        return userRepository.findByEmail(new Email(query.email()))
                .map(FindUserResponseMapper::toResponse)
                .orElseThrow(() -> new UserNotFoundException(
                        query.email()
                )
        );
    }
}