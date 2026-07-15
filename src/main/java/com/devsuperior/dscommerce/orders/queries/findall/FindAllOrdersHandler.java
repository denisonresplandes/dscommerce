package com.devsuperior.dscommerce.orders.queries.findall;

import com.devsuperior.dscommerce.orders.domain.OrderReadRepository;
import com.devsuperior.dscommerce.orders.queries.OrderResponseMapper;
import com.devsuperior.dscommerce.orders.queries.OrderResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FindAllOrdersHandler {

    private final OrderReadRepository repository;

    public FindAllOrdersHandler(OrderReadRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> execute() {
        return repository.findAll()
                .stream()
                .map(OrderResponseMapper::toResponse)
                .toList();
    }

}
