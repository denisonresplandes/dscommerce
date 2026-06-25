package com.devsuperior.dscommerce.users.infrastructure.persistence;

import com.devsuperior.dscommerce.users.domain.User;
import com.devsuperior.dscommerce.users.domain.UserRepository;
import com.devsuperior.dscommerce.users.domain.vo.Email;
import com.devsuperior.dscommerce.users.domain.vo.UserId;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class JpaUserRepositoryAdapter implements UserRepository {

    private final SpringDataJpaUserRepository springDataRepository;

    public JpaUserRepositoryAdapter(SpringDataJpaUserRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    public User save(User user) {
        Objects.requireNonNull(user, "user is required");
        UserEntity entityToSave = UserMapper.toEntity(user);
        UserEntity savedEntity = springDataRepository.save(entityToSave);
        return UserMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<User> findById(UserId id) {
        Objects.requireNonNull(id, "user id is required");
        return springDataRepository.findById(id.value())
                .map(UserMapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(Email email) {
        Objects.requireNonNull(email, "user email is required");
        return springDataRepository.findByEmail(email.value())
                .map(UserMapper::toDomain);
    }

    @Override
    public List<User> findAll() {
        return springDataRepository.findAll()
                .stream()
                .map(UserMapper::toDomain)
                .toList();
    }
}
