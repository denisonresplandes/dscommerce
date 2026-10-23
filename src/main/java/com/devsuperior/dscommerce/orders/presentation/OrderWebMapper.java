package com.devsuperior.dscommerce.orders.presentation;

import com.devsuperior.dscommerce.orders.application.view.OrderView;
import com.devsuperior.dscommerce.orders.presentation.dto.OrderResponse;

/**
 * Converte {@link OrderView} (modelo da aplicação) em {@link OrderResponse} (contrato público da API).
 *
 * <p>Hoje os campos coincidem, mas manter os dois modelos separados é o que permite a cada lado evoluir sozinho:
 * a aplicação pode renomear ou reorganizar a view sem alterar o JSON, e a API pode formatar ou esconder campos
 * sem afetar a aplicação. Só este mapper precisa acompanhar.
 */
final class OrderWebMapper {

    private OrderWebMapper() {}

    static OrderResponse toResponse(OrderView view) {
        return new OrderResponse(
                view.id(),
                view.clientId(),
                view.status(),
                view.createdAt()
        );
    }
}
