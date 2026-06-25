package com.devsuperior.dscommerce.users.queries.findbyid;

import com.devsuperior.dscommerce.users.domain.UserRepository;
import com.devsuperior.dscommerce.users.domain.vo.UserId;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class FindUserByIdHandler {

    private final UserRepository repository;

    public FindUserByIdHandler(UserRepository repository) {
        this.repository = repository;
    }

    public FindUserByIdResponse execute(FindUserByIdQuery query) {
        return repository.findById(new UserId(query.userId()))
                .map(FindUserByIdResponseMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException(
                    String.format("user with id %d not found.", query.userId())
                )
        );
    }
}
