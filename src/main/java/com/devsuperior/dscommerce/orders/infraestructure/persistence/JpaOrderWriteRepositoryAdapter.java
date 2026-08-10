package com.devsuperior.dscommerce.orders.infraestructure.persistence;

import com.devsuperior.dscommerce.orders.domain.Order;
import com.devsuperior.dscommerce.orders.domain.OrderWriteRepository;
import com.devsuperior.dscommerce.orders.domain.vo.OrderId;
import org.springframework.stereotype.Repository;

@Repository
public class JpaOrderWriteRepositoryAdapter implements OrderWriteRepository {

    private final SpringDataJpaOrderWriteRepository springDataRepository;

    public JpaOrderWriteRepositoryAdapter(SpringDataJpaOrderWriteRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    public Order save(Order order) {
        OrderEntity entityToSave = OrderMapper.toEntity(order);
        OrderEntity savedEntity = springDataRepository.save(entityToSave);
        return OrderMapper.toDomain(savedEntity);
    }

    @Override
    public void delete(Order order) {
        springDataRepository.delete(OrderMapper.toEntity(order));
    }

    @Override
    public void deleteById(OrderId orderId) {
        springDataRepository.deleteById(orderId.value());
    }
}