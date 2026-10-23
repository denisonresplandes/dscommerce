package com.devsuperior.dscommerce.orders.application.port;

import com.devsuperior.dscommerce.orders.application.view.OrderView;
import com.devsuperior.dscommerce.orders.domain.vo.ClientId;
import com.devsuperior.dscommerce.orders.domain.vo.OrderId;

import java.util.List;
import java.util.Optional;

/**
 * Port de <b>leitura</b> de pedidos, definido pela camada de aplicação (mesmo raciocínio do
 * {@code UserQueryRepository}): devolve {@link OrderView}, não o agregado.
 *
 * <p>É também o lugar natural para consultas que <b>cruzam agregados</b> no futuro (ex.: pedidos com o nome do
 * cliente). Essa junção é feita por uma query de leitura com DTO próprio, sem carregar dois agregados.
 */
public interface OrderQueryRepository {

    List<OrderView> findAll();

    Optional<OrderView> findById(OrderId id);

    List<OrderView> findAllByClientId(ClientId clientId);
}
