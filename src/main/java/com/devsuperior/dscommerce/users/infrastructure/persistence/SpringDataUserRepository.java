package com.devsuperior.dscommerce.users.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositório Spring Data do lado de <b>escrita</b>: CRUD completo sobre {@link UserEntity}.
 *
 * <p>É um detalhe de infraestrutura — nenhuma outra camada o enxerga. Quem o usa é o
 * {@link JpaUserRepositoryAdapter}, que o esconde atrás do port {@code UserRepository} do domínio.
 */
interface SpringDataUserRepository extends JpaRepository<UserEntity, Long> {
}
