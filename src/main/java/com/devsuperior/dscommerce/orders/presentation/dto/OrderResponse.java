package com.devsuperior.dscommerce.orders.presentation.dto;

import com.devsuperior.dscommerce.orders.domain.OrderStatus;

import java.time.LocalDateTime;

/**
 * Representação de pedido devolvida pela API.
 *
 * <p>O cliente do pedido aparece como {@code clientId}, o mesmo vocabulário usado no domínio e na aplicação de
 * pedidos. Inicialmente o campo se chamava {@code userId} (nome emprestado do módulo de usuários); como a
 * API ainda não tinha consumidores, foi renomeado sem versionamento. Com consumidores, uma mudança assim exigiria
 * uma nova versão da API ({@code /v2}).
 *
 * <p>Note que a <i>rota</i> {@code /users/{userId}/orders} não mudou: nela {@code userId} identifica o recurso
 * pai (o usuário) na URL, que é outra coisa.
 */
public record OrderResponse(
        Long id,
        Long clientId,
        OrderStatus status,
        LocalDateTime createdAt
) {
}
