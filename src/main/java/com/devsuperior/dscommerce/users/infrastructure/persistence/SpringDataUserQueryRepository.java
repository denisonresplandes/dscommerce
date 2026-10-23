package com.devsuperior.dscommerce.users.infrastructure.persistence;

import com.devsuperior.dscommerce.users.application.view.UserView;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositório Spring Data do lado de <b>leitura</b>.
 *
 * <p>Detalhes que importam:
 * <ul>
 *   <li>Estende {@link Repository} (a interface vazia), <b>não</b> {@code JpaRepository}: assim ele só expõe os
 *       métodos declarados aqui. Não existe {@code save}/{@code delete} no lado de leitura, nem por engano.</li>
 *   <li>Usa a expressão de construtor do JPQL ({@code select new ...UserView(...)}): o Hibernate já devolve o
 *       record preenchido, sem entidade gerenciada, sem proxy e sem passar pelo domínio. Busca só as 5 colunas
 *       necessárias (a senha nem sai do banco).</li>
 * </ul>
 */
interface SpringDataUserQueryRepository extends Repository<UserEntity, Long> {

    @Query("""
        select new com.devsuperior.dscommerce.users.application.view.UserView(
            u.id, u.name, u.email, u.phone, u.dateOfBirth)
        from UserEntity u
        where u.id = :id
    """)
    Optional<UserView> findViewById(Long id);

    @Query("""
        select new com.devsuperior.dscommerce.users.application.view.UserView(
            u.id, u.name, u.email, u.phone, u.dateOfBirth)
        from UserEntity u
        where u.email = :email
    """)
    Optional<UserView> findViewByEmail(String email);

    @Query("""
        select new com.devsuperior.dscommerce.users.application.view.UserView(
            u.id, u.name, u.email, u.phone, u.dateOfBirth)
        from UserEntity u
    """)
    List<UserView> findAllViews();

    boolean existsById(Long id);
}
