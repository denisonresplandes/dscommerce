package com.devsuperior.dscommerce.orders.presentation.dto;

import com.devsuperior.dscommerce.orders.domain.OrderStatus;

import java.time.LocalDateTime;

/**
 * Representação de pedido devolvida pela API.
 *
 * <p>O campo continua se chamando {@code userId}: renomeá-lo para {@code clientId} <b>quebraria os clientes da
 * API</b>, e esta migração não deve mudar o contrato externo. Internamente o conceito já é {@code clientId};
 * o {@code OrderWebMapper} faz a ponte. Se o nome público precisar mudar, faça-o numa nova versão da API
 * ({@code /v2}).
 */
public record OrderResponse(
        Long id,
        Long userId,
        OrderStatus status,
        LocalDateTime createdAt
) {
}
