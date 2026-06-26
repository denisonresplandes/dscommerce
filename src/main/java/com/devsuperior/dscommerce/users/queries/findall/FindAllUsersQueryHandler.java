package com.devsuperior.dscommerce.users.queries.findall;

import com.devsuperior.dscommerce.users.domain.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FindAllUsersQueryHandler {

    private final UserRepository userRepository;

    public FindAllUsersQueryHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<FindAllUserResponse> execute() {
        return userRepository.findAll()
                .stream()
                .map(FindAllUserResponseMapper::toResponse)
                .toList();
    }
}
