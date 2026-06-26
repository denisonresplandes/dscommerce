package com.devsuperior.dscommerce.users.queries.findbyid;

import com.devsuperior.dscommerce.users.domain.UserRepository;
import com.devsuperior.dscommerce.users.domain.vo.UserId;
import com.devsuperior.dscommerce.users.exceptions.UserNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class FindUserByIdQueryHandler {

    private final UserRepository userRepository;

    public FindUserByIdQueryHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public FindUserByIdResponse execute(FindUserByIdQuery query) {
        return userRepository.findById(new UserId(query.id()))
                .map(FindUserByIdResponseMapper::toResponse)
                .orElseThrow(() -> new UserNotFoundException(
                        query.id()
                )
        );
    }
}
