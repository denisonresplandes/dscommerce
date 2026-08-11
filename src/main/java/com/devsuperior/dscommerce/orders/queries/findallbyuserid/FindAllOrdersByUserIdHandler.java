package com.devsuperior.dscommerce.orders.queries.findallbyuserid;

import com.devsuperior.dscommerce.orders.domain.OrderReadRepository;
import com.devsuperior.dscommerce.orders.queries.OrderResponse;
import com.devsuperior.dscommerce.orders.queries.OrderResponseMapper;
import com.devsuperior.dscommerce.shared.domain.DomainException;
import com.devsuperior.dscommerce.users.domain.UserReadRepository;
import com.devsuperior.dscommerce.users.domain.vo.UserId;
import com.devsuperior.dscommerce.users.exceptions.UserNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FindAllOrdersByUserIdHandler {

    private final OrderReadRepository repository;
    private final UserReadRepository userReadRepository;

    public FindAllOrdersByUserIdHandler(OrderReadRepository repository, UserReadRepository userReadRepository) {
        this.repository = repository;
        this.userReadRepository = userReadRepository;
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> execute(FindAllOrdersByUserIdQuery query) {
        DomainException.requireNonNull(query, "user id query cannot be null");
        UserId userId = new UserId(query.userId());
        if (!userReadRepository.existsById(userId)) {
            throw new UserNotFoundException(userId.value());
        }
        return repository.findAllByUserId(userId)
                .stream()
                .map(OrderResponseMapper::toResponse)
                .toList();
    }
}