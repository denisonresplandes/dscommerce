package com.devsuperior.dscommerce.orders.queries.findall;

import com.devsuperior.dscommerce.orders.domain.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FindAllOrdersHandler {

    private final OrderRepository repository;

    public FindAllOrdersHandler(OrderRepository repository) {
        this.repository = repository;
    }

    public List<FindAllOrdersResponse> execute() {
        return repository.findAll()
                .stream()
                .map(FindAllOrdersResponseMapper::toResponse)
                .toList();
    }

}
