package com.devsuperior.dscommerce.orders.infraestructure.persistence;

import com.devsuperior.dscommerce.orders.domain.Order;
import com.devsuperior.dscommerce.orders.domain.OrderSummary;
import com.devsuperior.dscommerce.orders.domain.vo.OrderId;
import com.devsuperior.dscommerce.users.domain.vo.UserId;

import java.util.Objects;

public final class OrderMapper {

    private OrderMapper() {}

    public static OrderEntity toEntity(Order order) {
        Long id = Objects.nonNull(order.getId()) ? order.getId().value() : null;
        return new OrderEntity(
                id,
                order.getClientId().value(),
                order.getStatus(),
                order.getCreatedAt()
        );
    }

    public static Order toDomain(OrderEntity entity) {
        return Order.restore(
                new OrderId(entity.getId()),
                new UserId(entity.getUserId()),
                entity.getStatus(),
                entity.getCreatedAt()
        );
    }

    public static OrderSummary toSummary(OrderSummaryProjection projection) {
        return new OrderSummary(
                new OrderId(projection.getId()),
                new UserId(projection.getUserId()),
                projection.getStatus(),
                projection.getCreatedAt()
        );
    }
}
