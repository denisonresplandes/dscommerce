package com.devsuperior.dscommerce.orders.infraestructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataJpaOrderWriteRepository extends JpaRepository<OrderEntity, Long> {
}
