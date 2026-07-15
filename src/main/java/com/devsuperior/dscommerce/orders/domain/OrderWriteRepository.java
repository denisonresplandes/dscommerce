package com.devsuperior.dscommerce.orders.domain;

import com.devsuperior.dscommerce.orders.domain.vo.OrderId;

public interface OrderWriteRepository {

    Order save(Order order);

    void delete(Order order);

    void deleteById(OrderId orderId);
}