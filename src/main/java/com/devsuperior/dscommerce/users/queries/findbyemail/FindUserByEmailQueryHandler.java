package com.devsuperior.dscommerce.users.queries.findbyemail;

import com.devsuperior.dscommerce.users.domain.UserRepository;
import com.devsuperior.dscommerce.users.domain.vo.Email;
import com.devsuperior.dscommerce.users.exceptions.UserNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class FindUserByEmailQueryHandler {

    private final UserRepository userRepository;

    public FindUserByEmailQueryHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public FindUserByEmailResponse execute(FindUserByEmailQuery query) {
        return userRepository.findByEmail(new Email(query.email()))
                .map(FindUserByEmailResponseMapper::toResponse)
                .orElseThrow(() -> new UserNotFoundException(
                        query.email()
                )
        );
    }
}
