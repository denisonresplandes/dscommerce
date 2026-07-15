package com.devsuperior.dscommerce.orders.infraestructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpringDataJpaOrderReadRepository extends JpaRepository<OrderEntity, Long> {
    /*
     * Cada consulta pode ficar num repositório separado.
     * */
    @Query("""
        select o from OrderEntity o
    """)
    List<OrderSummaryProjection> findAllSummary();

    @Query("""
        select o from OrderEntity o where o.id = :id
    """)
    Optional<OrderSummaryProjection> findByIdSummary(Long id);

    @Query("""
        select o from OrderEntity o where o.userId = :userId
    """)
    List<OrderSummaryProjection> findAllByUserIdSummary(Long userId);
}
