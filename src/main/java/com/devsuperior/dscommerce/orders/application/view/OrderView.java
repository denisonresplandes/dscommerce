package com.devsuperior.dscommerce.orders.application.view;

import com.devsuperior.dscommerce.orders.domain.OrderStatus;

import java.time.LocalDateTime;

/**
 * <i>Read model</i> de pedido: o que as consultas devolvem, preenchido direto pelo banco (sem montar o agregado
 * {@code Order}). Mesma ideia da {@code UserView}.
 *
 * <p>O campo se chama {@code clientId}, o vocabulário do módulo de pedidos.
 */
public record OrderView(
        Long id,
        Long clientId,
        OrderStatus status,
        LocalDateTime createdAt
) {
}
