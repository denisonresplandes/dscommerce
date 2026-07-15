package com.devsuperior.dscommerce.orders.infraestructure.persistence;

import com.devsuperior.dscommerce.orders.domain.Order;
import com.devsuperior.dscommerce.orders.domain.OrderWriteRepository;
import com.devsuperior.dscommerce.orders.domain.vo.OrderId;
import org.springframework.stereotype.Repository;

import java.util.Objects;

@Repository
public class JpaOrderWriteRepositoryAdapter implements OrderWriteRepository {

    private final SpringDataJpaOrderWriteRepository springDataRepository;

    public JpaOrderWriteRepositoryAdapter(SpringDataJpaOrderWriteRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    public Order save(Order order) {
        Objects.requireNonNull(order, "order is required");
        OrderEntity entityToSave = OrderMapper.toEntity(order);
        OrderEntity savedEntity = springDataRepository.save(entityToSave);
        return OrderMapper.toDomain(savedEntity);
    }

    @Override
    public void delete(Order order) {
        Objects.requireNonNull(order, "order is required");
        springDataRepository.delete(OrderMapper.toEntity(order));
    }

    @Override
    public void deleteById(OrderId orderId) {
        Objects.requireNonNull(orderId, "order id is required");
        springDataRepository.deleteById(orderId.value());
    }
}