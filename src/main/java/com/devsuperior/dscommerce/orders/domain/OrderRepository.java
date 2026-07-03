package com.devsuperior.dscommerce.orders.domain;

import com.devsuperior.dscommerce.orders.domain.vo.OrderId;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {

    List<Order> findAll();

    Optional<Order> findById(OrderId id);
}
