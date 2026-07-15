package com.devsuperior.dscommerce.orders.queries.findbyid;

import com.devsuperior.dscommerce.orders.queries.OrderResponse;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
@Validated
public class FindOrderByIdController {

    private final FindOrderByIdHandler handler;

    public FindOrderByIdController(FindOrderByIdHandler handler) {
        this.handler = handler;
    }

    @GetMapping("/{id}")
    public OrderResponse findById(@PathVariable @NotNull @Positive Long id) {
        return handler.execute(new FindOrderByIdQuery(id));
    }
}
