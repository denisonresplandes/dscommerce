package com.devsuperior.dscommerce.orders.domain;

import com.devsuperior.dscommerce.orders.domain.vo.ClientId;
import com.devsuperior.dscommerce.orders.domain.vo.OrderId;
import com.devsuperior.dscommerce.shared.domain.AggregateRoot;
import com.devsuperior.dscommerce.shared.domain.DomainException;

import java.time.LocalDateTime;

/**
 * Agregado raiz de pedido — <b>domínio rico</b>: as regras de transição de status vivem aqui, não num service.
 *
 * <p>Quem quer pagar um pedido chama {@link #pay()}; o próprio agregado decide se isso é permitido a partir do
 * estado atual (veja {@link OrderStatus#canTransitionTo}) e lança {@link DomainException} se não for. Assim é
 * impossível "esquecer" a regra em algum caso de uso novo.
 *
 * <p>Relação com outros agregados: só por identidade. O cliente é um {@link ClientId}, nunca um objeto de usuário
 * (veja o Javadoc de {@code ClientId}).
 */
public class Order extends AggregateRoot {

    private final OrderId id;
    private final ClientId clientId;
    private final LocalDateTime createdAt;
    private OrderStatus status;

    private Order(OrderId id, ClientId clientId, OrderStatus status,
                  LocalDateTime createdAt) {
        DomainException.requireNonNull(clientId, "client id cannot be null");
        DomainException.requireNonNull(status, "order status cannot be null");
        DomainException.requireNonNull(createdAt, "created date cannot be null");

        this.id = id;
        this.clientId = clientId;
        this.status = status;
        this.createdAt = createdAt;
    }

    /**
     * Factory method: cria um pedido <b>novo</b> (ainda sem id; o banco o gera ao salvar), sempre aguardando
     * pagamento.
     */
    public static Order create(ClientId clientId) {
        return new Order(
                null,
                clientId,
                OrderStatus.WAITING_PAYMENT,
                LocalDateTime.now()
        );
    }

    /**
     * Factory method: reconstitui um pedido <b>já existente</b> a partir do que está salvo. Exige id.
     */
    public static Order restore(OrderId id, ClientId clientId, OrderStatus status,
                                LocalDateTime createdAt) {
        DomainException.requireNonNull(id,"Order ID cannot be null.");
        return new Order(
                id,
                clientId,
                status,
                createdAt
        );
    }

    public OrderId getId() {
        return id;
    }

    public ClientId getClientId() {
        return clientId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void pay() {
        transitionTo(OrderStatus.PAID);
    }

    public void ship() {
        transitionTo(OrderStatus.SHIPPED);
    }

    public void deliver() {
        transitionTo(OrderStatus.DELIVERED);
    }

    public void cancel() {
        transitionTo(OrderStatus.CANCELED);
    }

    private void transitionTo(OrderStatus nextStatus) {
        // Idempotente: pedir o estado em que já está não é erro nem muda nada.
        if (status == nextStatus) {
            return;
        }
        checkTransitionTo(nextStatus);
        status = nextStatus;
    }

    private void checkTransitionTo(OrderStatus nextStatus) {
        if (!this.status.canTransitionTo(nextStatus)) {
            throw new DomainException("Order with id %s cannot transition from %s to %s"
                    .formatted(
                            id,
                            status,
                            nextStatus
                    )
            );
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Order other)) {
            return false;
        }
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
