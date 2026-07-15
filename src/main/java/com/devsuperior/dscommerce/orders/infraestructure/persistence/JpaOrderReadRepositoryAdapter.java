package com.devsuperior.dscommerce.orders.infraestructure.persistence;

import com.devsuperior.dscommerce.orders.domain.OrderReadRepository;
import com.devsuperior.dscommerce.orders.domain.OrderSummary;
import com.devsuperior.dscommerce.orders.domain.vo.OrderId;
import com.devsuperior.dscommerce.users.domain.vo.UserId;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class JpaOrderReadRepositoryAdapter implements OrderReadRepository {

    private final SpringDataJpaOrderReadRepository springDataRepository;

    public JpaOrderReadRepositoryAdapter(SpringDataJpaOrderReadRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    public List<OrderSummary> findAll() {
        return springDataRepository.findAllSummary()
                .stream()
                .map(OrderMapper::toSummary)
                .toList();
    }

    @Override
    public Optional<OrderSummary> findById(OrderId id) {
        Objects.requireNonNull(id, "order id is required");
        return springDataRepository.findByIdSummary(id.value())
                .map(OrderMapper::toSummary);
    }

    @Override
    public List<OrderSummary> findAllByUserId(UserId id) {
        Objects.requireNonNull(id, "user id is required");
        return springDataRepository.findAllByUserIdSummary(id.value())
                .stream()
                .map(OrderMapper::toSummary)
                .toList();
    }
}
