package com.devsuperior.dscommerce.users.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/*
 * Implementação JPA.
 * */
@Repository
public interface SpringDataJpaUserReadRepository extends JpaRepository<UserEntity, Long> {
    /*
     * Cada consulta pode ficar num repositório separado.
     * */
    @Query("""
        select u.id as id,
               u.name as name,
               u.dateOfBirth as dateOfBirth,
               u.email as email,
               u.phone as phone
        from UserEntity u
        where u.id = :id
    """)
    Optional<UserSummaryProjection> findByIdSummary(Long id);

    @Query("""
        select u.id as id,
               u.name as name,
               u.dateOfBirth as dateOfBirth,
               u.email as email,
               u.phone as phone
        from UserEntity u
    """)
    List<UserSummaryProjection> findAllSummary();

    @Query("""
        select u.id as id,
               u.name as name,
               u.dateOfBirth as dateOfBirth,
               u.email as email,
               u.phone as phone
        from UserEntity u
        where u.email = :email
    """)
    Optional<UserSummaryProjection> findByEmailSummary(String email);
}
