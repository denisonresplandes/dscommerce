package com.devsuperior.dscommerce.orders.queries.findallbyuserid;

import com.devsuperior.dscommerce.orders.queries.OrderResponse;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users")
@Validated
public class FindAllOrdersByUserIdController {

    private final FindAllOrdersByUserIdHandler handler;

    public FindAllOrdersByUserIdController(FindAllOrdersByUserIdHandler handler) {
        this.handler = handler;
    }

    @GetMapping("/{userId}/orders")
    public List<OrderResponse> findAllByUserId(@PathVariable @NotNull @Positive Long userId) {
        return handler.execute(new FindAllOrdersByUserIdQuery(userId));
    }
}
