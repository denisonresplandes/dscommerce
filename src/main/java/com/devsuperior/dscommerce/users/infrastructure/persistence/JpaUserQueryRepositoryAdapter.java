package com.devsuperior.dscommerce.users.infrastructure.persistence;

import com.devsuperior.dscommerce.users.application.port.UserQueryRepository;
import com.devsuperior.dscommerce.users.application.view.UserView;
import com.devsuperior.dscommerce.users.domain.vo.Email;
import com.devsuperior.dscommerce.users.domain.vo.UserId;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Adapter do port de leitura {@link UserQueryRepository}.
 *
 * <p>Repare que aqui <b>não há mapper</b>: a query já devolve {@link UserView}. É a economia da leitura direta —
 * antes eram três conversões (projeção → summary de domínio → response); agora é nenhuma.
 */
@Repository
class JpaUserQueryRepositoryAdapter implements UserQueryRepository {

    private final SpringDataUserQueryRepository springDataRepository;

    JpaUserQueryRepositoryAdapter(SpringDataUserQueryRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    public Optional<UserView> findById(UserId id) {
        return springDataRepository.findViewById(id.value());
    }

    @Override
    public Optional<UserView> findByEmail(Email email) {
        return springDataRepository.findViewByEmail(email.value());
    }

    @Override
    public List<UserView> findAll() {
        return springDataRepository.findAllViews();
    }

    @Override
    public boolean existsById(UserId id) {
        return springDataRepository.existsById(id.value());
    }
}
