package com.devsuperior.dscommerce.orders.presentation;

import com.devsuperior.dscommerce.orders.application.OrderQueryService;
import com.devsuperior.dscommerce.orders.presentation.dto.OrderResponse;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller REST de pedidos (substitui os três controllers por endpoint).
 *
 * <p>Os caminhos são declarados por método, e não num {@code @RequestMapping} de classe, porque um deles
 * ({@code /users/{userId}/orders}) é um <i>sub-recurso</i> do usuário: a URL fica sob {@code /users}, mas quem a
 * atende é o módulo de pedidos, pois os dados são pedidos. A URL pública não mudou.
 */
@RestController
@Validated
public class OrderController {

    private final OrderQueryService queryService;

    public OrderController(OrderQueryService queryService) {
        this.queryService = queryService;
    }

    @GetMapping("/orders")
    public List<OrderResponse> findAll() {
        return queryService.findAll()
                .stream()
                .map(OrderWebMapper::toResponse)
                .toList();
    }

    @GetMapping("/orders/{id}")
    public OrderResponse findById(@PathVariable @NotNull @Positive Long id) {
        return OrderWebMapper.toResponse(queryService.findById(id));
    }

    @GetMapping("/users/{userId}/orders")
    public List<OrderResponse> findAllByUserId(@PathVariable @NotNull @Positive Long userId) {
        return queryService.findAllByClientId(userId)
                .stream()
                .map(OrderWebMapper::toResponse)
                .toList();
    }
}
