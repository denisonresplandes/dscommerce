package com.devsuperior.dscommerce.orders.domain;

import com.devsuperior.dscommerce.orders.domain.vo.OrderId;
import com.devsuperior.dscommerce.users.domain.vo.UserId;

import java.util.List;
import java.util.Optional;

public interface OrderReadRepository {

    List<OrderSummary> findAll();

    Optional<OrderSummary> findById(OrderId id);

    List<OrderSummary> findAllByUserId(UserId id);
}
