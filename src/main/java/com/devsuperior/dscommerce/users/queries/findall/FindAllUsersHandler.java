package com.devsuperior.dscommerce.users.queries.findall;

import com.devsuperior.dscommerce.users.domain.UserRepository;
import com.devsuperior.dscommerce.users.queries.FindUserResponse;
import com.devsuperior.dscommerce.users.queries.FindUserResponseMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FindAllUsersHandler {

    private final UserRepository userRepository;

    public FindAllUsersHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<FindUserResponse> execute() {
        return userRepository.findAll()
                .stream()
                .map(FindUserResponseMapper::toResponse)
                .toList();
    }
}
