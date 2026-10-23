package com.devsuperior.dscommerce.orders.domain;

import com.devsuperior.dscommerce.orders.domain.vo.OrderId;

import java.util.Optional;

/**
 * Port de persistência do agregado {@link Order} (padrão <b>Repository</b>).
 *
 * <p>Mesmo papel do {@code UserRepository}: o domínio vê uma "coleção de pedidos" e ignora JPA e SQL. Serve ao
 * lado de <b>escrita</b> — carregar o agregado, aplicar uma regra ({@code pay()}, {@code ship()}…) e salvar.
 * Listagens para a tela usam {@code OrderQueryRepository}.
 *
 * <p>Substitui {@code OrderWriteRepository}. Ganhou {@code findById}, que faltava: sem ele era impossível carregar
 * um pedido existente, então as transições de status do agregado não tinham como ser usadas por nenhum caso de uso.
 * {@code delete}/{@code deleteById} saíram por não terem chamadores.
 */
public interface OrderRepository {

    Order save(Order order);

    Optional<Order> findById(OrderId id);
}
