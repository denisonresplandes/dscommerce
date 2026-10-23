package com.devsuperior.dscommerce.orders.application;

import com.devsuperior.dscommerce.orders.application.exception.ClientNotFoundException;
import com.devsuperior.dscommerce.orders.application.exception.OrderNotFoundException;
import com.devsuperior.dscommerce.orders.application.port.ClientLookup;
import com.devsuperior.dscommerce.orders.application.port.OrderQueryRepository;
import com.devsuperior.dscommerce.orders.application.view.OrderView;
import com.devsuperior.dscommerce.orders.domain.vo.ClientId;
import com.devsuperior.dscommerce.orders.domain.vo.OrderId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Application Service do lado de <b>leitura</b> de pedidos (substitui {@code FindAllOrdersHandler},
 * {@code FindOrderByIdHandler} e {@code FindAllOrdersByUserIdHandler}).
 *
 * <p>É aqui que a regra entre módulos acontece, e de forma limpa: para listar os pedidos de um cliente, o service
 * pergunta ao port {@link ClientLookup} se o cliente existe. Ele não sabe que a resposta vem do módulo de
 * usuários — e o módulo {@code orders} deixou de importar qualquer coisa de {@code users} fora do ACL.
 */
@Service
@Transactional(readOnly = true)
public class OrderQueryService {

    private final OrderQueryRepository orderQueryRepository;
    private final ClientLookup clientLookup;

    public OrderQueryService(OrderQueryRepository orderQueryRepository, ClientLookup clientLookup) {
        this.orderQueryRepository = orderQueryRepository;
        this.clientLookup = clientLookup;
    }

    public List<OrderView> findAll() {
        return orderQueryRepository.findAll();
    }

    public OrderView findById(Long id) {
        OrderId orderId = new OrderId(id);
        return orderQueryRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId.value()));
    }

    public List<OrderView> findAllByClientId(Long id) {
        ClientId clientId = new ClientId(id);
        // Distingue "cliente inexistente" (404) de "cliente sem pedidos" (200 com lista vazia).
        if (!clientLookup.exists(clientId)) {
            throw new ClientNotFoundException(clientId.value());
        }
        return orderQueryRepository.findAllByClientId(clientId);
    }
}
