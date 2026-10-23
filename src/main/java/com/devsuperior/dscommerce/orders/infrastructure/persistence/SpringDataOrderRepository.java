package com.devsuperior.dscommerce.orders.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositório Spring Data do lado de <b>escrita</b> (CRUD sobre {@link OrderEntity}). Detalhe de infraestrutura,
 * visível só neste pacote; o domínio o enxerga através do port {@code OrderRepository}.
 */
interface SpringDataOrderRepository extends JpaRepository<OrderEntity, Long> {
}
