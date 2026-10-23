package com.devsuperior.dscommerce.orders.domain.vo;

import com.devsuperior.dscommerce.shared.domain.DomainException;

/**
 * Identidade do <b>cliente</b> que fez o pedido, <i>do ponto de vista do módulo {@code orders}</i>.
 *
 * <p><b>Regra de ouro do DDD:</b> um agregado não guarda <i>referência de objeto</i> para outro agregado, só a
 * <i>identidade</i> dele. Por isso {@code Order} tem um {@code ClientId} e não um {@code User}: carregar um pedido
 * nunca carrega um usuário, e cada agregado tem seu próprio ciclo de vida e sua própria transação.
 *
 * <p><b>Por que um tipo próprio em vez de reutilizar o {@code UserId} de {@code users}?</b>
 * <ul>
 *   <li>No contexto de pedidos, quem compra é um <i>cliente</i>; "usuário" é o vocabulário de outro módulo
 *       (Bounded Contexts diferentes podem chamar o mesmo conceito por nomes diferentes).</li>
 *   <li>Importar {@code users.domain.vo.UserId} faria o domínio de pedidos depender do domínio de usuários:
 *       mexer em um quebraria o outro.</li>
 *   <li>O preço é uma pequena duplicação (um record de uma linha), um custo assumido no ADR 0001.</li>
 * </ul>
 *
 * <p>A tradução {@code ClientId} ↔ id de usuário acontece em um único lugar: o Anti-Corruption Layer
 * ({@code UserModuleClientLookup}).
 */
public record ClientId(
        Long value
) {

    public ClientId {
        DomainException.requirePositive(value, "client id must be a positive number.");
    }
}
