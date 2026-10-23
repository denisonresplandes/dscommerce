package com.devsuperior.dscommerce.orders.application.exception;

import com.devsuperior.dscommerce.shared.application.NotFoundException;

/**
 * O cliente informado não existe, <i>do ponto de vista de pedidos</i>.
 *
 * <p>Antes, {@code orders} lançava a {@code UserNotFoundException} do módulo de usuários — exceção de outro módulo
 * vazando para dentro deste. Agora cada módulo tem a sua, em seu próprio vocabulário. Também resulta em HTTP 404.
 */
public class ClientNotFoundException extends NotFoundException {

    public ClientNotFoundException(Long id) {
        super("Client with id %d not found.".formatted(id));
    }
}
