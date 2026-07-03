package com.devsuperior.dscommerce.orders.queries.findall;

import com.devsuperior.dscommerce.orders.domain.Order;

public final class FindAllOrdersResponseMapper {

    private  FindAllOrdersResponseMapper() {}

    public static FindAllOrdersResponse toResponse(Order order) {
        return new FindAllOrdersResponse(
                order.getId().value(),
                order.getUserId().value(),
                order.getStatus(),
                order.getCreatedAt()
        );
    }
}
