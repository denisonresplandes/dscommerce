package com.devsuperior.dscommerce.orders.queries.findall;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class FindAllOrdersController {

    private final FindAllOrdersQueryHandler handler;

    public FindAllOrdersController(FindAllOrdersQueryHandler handler) {
        this.handler = handler;
    }

    @GetMapping
    public List<FindAllOrdersResponse> findAll() {
        return handler.execute();
    }
}
