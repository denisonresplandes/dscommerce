package com.devsuperior.dscommerce.users.queries.findall;

import com.devsuperior.dscommerce.users.domain.UserReadRepository;
import com.devsuperior.dscommerce.users.queries.UserResponse;
import com.devsuperior.dscommerce.users.queries.UserResponseMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FindAllUsersHandler {

    private final UserReadRepository userReadRepository;

    public FindAllUsersHandler(UserReadRepository userReadRepository) {
        this.userReadRepository = userReadRepository;
    }

    @Transactional(readOnly = true)
    public List<UserResponse> execute() {
        return userReadRepository.findAll()
                .stream()
                .map(UserResponseMapper::toResponse)
                .toList();
    }
}
