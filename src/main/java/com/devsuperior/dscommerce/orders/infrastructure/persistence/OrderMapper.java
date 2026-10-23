package com.devsuperior.dscommerce.orders.infrastructure.persistence;

import com.devsuperior.dscommerce.orders.domain.Order;
import com.devsuperior.dscommerce.orders.domain.vo.ClientId;
import com.devsuperior.dscommerce.orders.domain.vo.OrderId;

import java.util.Objects;

/**
 * Converte entre o agregado {@link Order} (domínio) e {@link OrderEntity} (persistência).
 *
 * <p>Só existe o caminho de <b>escrita/carga do agregado</b>. A conversão para "summary" foi removida: as consultas
 * recebem {@code OrderView} direto do banco e não passam pelo domínio.
 */
public final class OrderMapper {

    private OrderMapper() {}

    public static OrderEntity toEntity(Order order) {
        Objects.requireNonNull(order, "order is required");
        // Pedido novo ainda não tem id: o banco o gera (IDENTITY).
        Long id = Objects.nonNull(order.getId()) ? order.getId().value() : null;
        return new OrderEntity(
                id,
                order.getClientId().value(),
                order.getStatus(),
                order.getCreatedAt()
        );
    }

    public static Order toDomain(OrderEntity entity) {
        Objects.requireNonNull(entity, "entity is required");
        return Order.restore(
                new OrderId(entity.getId()),
                new ClientId(entity.getClientId()),
                entity.getStatus(),
                entity.getCreatedAt()
        );
    }
}
