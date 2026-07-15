package com.devsuperior.dscommerce.orders.queries.findbyid;

import com.devsuperior.dscommerce.orders.domain.OrderReadRepository;
import com.devsuperior.dscommerce.orders.domain.vo.OrderId;
import com.devsuperior.dscommerce.orders.exceptions.OrderNotFoundException;
import com.devsuperior.dscommerce.orders.queries.OrderResponseMapper;
import com.devsuperior.dscommerce.orders.queries.OrderResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class FindOrderByIdHandler {

    private final OrderReadRepository repository;

    public FindOrderByIdHandler(OrderReadRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public OrderResponse execute(FindOrderByIdQuery query) {
        Objects.requireNonNull(query, "order id query is required");
        OrderId id = new OrderId(query.id());
        return repository.findById(id)
                .map(OrderResponseMapper::toResponse)
                .orElseThrow(() -> new OrderNotFoundException(
                        id.value()
                )
        );
    }
}
