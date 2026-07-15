package com.devsuperior.dscommerce.users.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataJpaUserWriteRepository extends JpaRepository<UserEntity, Long> {
}
