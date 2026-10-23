package com.devsuperior.dscommerce.orders.infrastructure.persistence;

import com.devsuperior.dscommerce.orders.application.port.OrderQueryRepository;
import com.devsuperior.dscommerce.orders.application.view.OrderView;
import com.devsuperior.dscommerce.orders.domain.vo.ClientId;
import com.devsuperior.dscommerce.orders.domain.vo.OrderId;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Adapter do port de leitura {@link OrderQueryRepository}. Sem mapper: a query já devolve {@link OrderView}.
 */
@Repository
class JpaOrderQueryRepositoryAdapter implements OrderQueryRepository {

    private final SpringDataOrderQueryRepository springDataRepository;

    JpaOrderQueryRepositoryAdapter(SpringDataOrderQueryRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    public List<OrderView> findAll() {
        return springDataRepository.findAllViews();
    }

    @Override
    public Optional<OrderView> findById(OrderId id) {
        return springDataRepository.findViewById(id.value());
    }

    @Override
    public List<OrderView> findAllByClientId(ClientId clientId) {
        return springDataRepository.findAllViewsByClientId(clientId.value());
    }
}
