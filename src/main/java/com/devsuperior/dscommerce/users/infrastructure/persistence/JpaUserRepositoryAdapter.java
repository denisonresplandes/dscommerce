package com.devsuperior.dscommerce.users.infrastructure.persistence;

import com.devsuperior.dscommerce.users.domain.User;
import com.devsuperior.dscommerce.users.domain.UserRepository;
import com.devsuperior.dscommerce.users.domain.vo.UserId;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * <b>Adapter</b> (Ports &amp; Adapters): implementa o port {@link UserRepository} do domínio usando Spring Data JPA.
 *
 * <p>É aqui que os <b>dois modelos</b> se encontram. O domínio trabalha com {@code User} (regras, value objects);
 * o banco trabalha com {@link UserEntity} (colunas). O {@link UserMapper} converte nos dois sentidos, e por isso
 * mudanças de schema não vazam para as regras de negócio, nem o contrário.
 */
@Repository
class JpaUserRepositoryAdapter implements UserRepository {

    private final SpringDataUserRepository springDataRepository;

    JpaUserRepositoryAdapter(SpringDataUserRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    public User save(User user) {
        UserEntity entityToSave = UserMapper.toEntity(user);
        UserEntity savedEntity = springDataRepository.save(entityToSave);
        return UserMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<User> findById(UserId id) {
        return springDataRepository.findById(id.value())
                .map(UserMapper::toDomain);
    }
}
