package com.devsuperior.dscommerce.users.queries.findbyid;

import com.devsuperior.dscommerce.users.domain.UserRepository;
import com.devsuperior.dscommerce.users.domain.vo.UserId;
import com.devsuperior.dscommerce.users.exceptions.UserNotFoundException;
import com.devsuperior.dscommerce.users.queries.FindUserResponse;
import com.devsuperior.dscommerce.users.queries.FindUserResponseMapper;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class FindUserByIdHandler {

    private final UserRepository userRepository;

    public FindUserByIdHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public FindUserResponse execute(FindUserByIdQuery query) {
        Objects.requireNonNull(query, "find user query is required");
        UserId id = new UserId(query.id());
        return userRepository.findById(id)
                .map(FindUserResponseMapper::toResponse)
                .orElseThrow(() -> new UserNotFoundException(
                        id.value()
                )
        );
    }
}
