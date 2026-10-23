package com.devsuperior.dscommerce.orders.infrastructure.persistence;

import com.devsuperior.dscommerce.orders.application.view.OrderView;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositório Spring Data do lado de <b>leitura</b>: estende {@link Repository} (só expõe o que está declarado,
 * sem {@code save}/{@code delete}) e projeta direto em {@link OrderView} via {@code select new}.
 */
interface SpringDataOrderQueryRepository extends Repository<OrderEntity, Long> {

    @Query("""
        select new com.devsuperior.dscommerce.orders.application.view.OrderView(
            o.id, o.clientId, o.status, o.createdAt)
        from OrderEntity o
    """)
    List<OrderView> findAllViews();

    @Query("""
        select new com.devsuperior.dscommerce.orders.application.view.OrderView(
            o.id, o.clientId, o.status, o.createdAt)
        from OrderEntity o
        where o.id = :id
    """)
    Optional<OrderView> findViewById(Long id);

    @Query("""
        select new com.devsuperior.dscommerce.orders.application.view.OrderView(
            o.id, o.clientId, o.status, o.createdAt)
        from OrderEntity o
        where o.clientId = :clientId
    """)
    List<OrderView> findAllViewsByClientId(Long clientId);
}
