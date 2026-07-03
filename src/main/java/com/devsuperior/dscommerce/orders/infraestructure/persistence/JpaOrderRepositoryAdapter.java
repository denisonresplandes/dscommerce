package com.devsuperior.dscommerce.orders.infraestructure.persistence;

import com.devsuperior.dscommerce.orders.domain.Order;
import com.devsuperior.dscommerce.orders.domain.OrderRepository;
import com.devsuperior.dscommerce.orders.domain.vo.OrderId;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JpaOrderRepositoryAdapter implements OrderRepository {

    private final SpringDataJpaOrderRepository springDataRepository;

    public JpaOrderRepositoryAdapter(SpringDataJpaOrderRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    public List<Order> findAll() {
        return springDataRepository.findAll()
                .stream()
                .map(OrderMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Order> findById(OrderId id) {
        return springDataRepository.findById(id.value())
                .map(OrderMapper::toDomain);
    }
}
