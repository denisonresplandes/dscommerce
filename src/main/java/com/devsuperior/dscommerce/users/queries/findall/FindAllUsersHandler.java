package com.devsuperior.dscommerce.users.queries.findall;

import com.devsuperior.dscommerce.users.domain.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FindAllUsersHandler {

    private final UserRepository repository;

    public FindAllUsersHandler(UserRepository repository) {
        this.repository = repository;
    }

    public List<FindAllUserResponse> execute() {
        return repository.findAll()
                .stream()
                .map(FindAllUserResponseMapper::toResponse)
                .toList();
    }
}
