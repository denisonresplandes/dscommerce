package com.devsuperior.dscommerce.users.infrastructure.persistence;

import com.devsuperior.dscommerce.users.domain.User;
import com.devsuperior.dscommerce.users.domain.UserWriteRepository;
import com.devsuperior.dscommerce.users.domain.vo.UserId;
import org.springframework.stereotype.Repository;

import java.util.Objects;

@Repository
public class JpaUserWriteRepositoryAdapter implements UserWriteRepository {

    private final SpringDataJpaUserWriteRepository springDataRepository;

    public JpaUserWriteRepositoryAdapter(SpringDataJpaUserWriteRepository springDataRepository) {
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
    public void delete(User user) {
        Objects.requireNonNull(user, "user is required");
        springDataRepository.delete(UserMapper.toEntity(user));
    }

    @Override
    public void deleteById(UserId id) {
        Objects.requireNonNull(id, "user id is required");
        springDataRepository.deleteById(id.value());
    }
}
