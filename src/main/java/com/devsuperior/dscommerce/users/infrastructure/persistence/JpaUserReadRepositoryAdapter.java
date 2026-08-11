package com.devsuperior.dscommerce.users.infrastructure.persistence;

import com.devsuperior.dscommerce.users.domain.UserReadRepository;
import com.devsuperior.dscommerce.users.domain.UserSummary;
import com.devsuperior.dscommerce.users.domain.vo.Email;
import com.devsuperior.dscommerce.users.domain.vo.UserId;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JpaUserReadRepositoryAdapter implements UserReadRepository {

    private final SpringDataJpaUserReadRepository springDataRepository;

    public JpaUserReadRepositoryAdapter(SpringDataJpaUserReadRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    public Optional<UserSummary> findById(UserId id) {
        return springDataRepository.findByIdSummary(id.value())
                .map(UserMapper::toSummary);
    }

    @Override
    public Optional<UserSummary> findByEmail(Email email) {
        return springDataRepository.findByEmailSummary(email.value())
                .map(UserMapper::toSummary);
    }

    @Override
    public List<UserSummary> findAll() {
        return springDataRepository.findAllSummary()
                .stream()
                .map(UserMapper::toSummary)
                .toList();
    }

    @Override
    public boolean existsById(UserId id) {
        return springDataRepository.existsById(id.value());
    }
}
