package com.devsuperior.dscommerce.orders.queries.findall;

import com.devsuperior.dscommerce.orders.queries.OrderResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class FindAllOrdersController {

    private final FindAllOrdersHandler handler;

    public FindAllOrdersController(FindAllOrdersHandler handler) {
        this.handler = handler;
    }

    @GetMapping
    public List<OrderResponse> findAll() {
        return handler.execute();
    }
}
