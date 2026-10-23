package com.devsuperior.dscommerce.orders.infrastructure.persistence;

import com.devsuperior.dscommerce.orders.domain.Order;
import com.devsuperior.dscommerce.orders.domain.OrderRepository;
import com.devsuperior.dscommerce.orders.domain.vo.OrderId;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Adapter do port {@link OrderRepository} usando Spring Data JPA: converte agregado ↔ entidade com o
 * {@link OrderMapper} (veja {@code JpaUserRepositoryAdapter} para a explicação completa do padrão).
 */
@Repository
class JpaOrderRepositoryAdapter implements OrderRepository {

    private final SpringDataOrderRepository springDataRepository;

    JpaOrderRepositoryAdapter(SpringDataOrderRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    public Order save(Order order) {
        OrderEntity entityToSave = OrderMapper.toEntity(order);
        OrderEntity savedEntity = springDataRepository.save(entityToSave);
        return OrderMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Order> findById(OrderId id) {
        return springDataRepository.findById(id.value())
                .map(OrderMapper::toDomain);
    }
}
