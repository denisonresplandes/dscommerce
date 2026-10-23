package com.devsuperior.dscommerce.orders.domain;

import com.devsuperior.dscommerce.orders.domain.vo.ClientId;
import com.devsuperior.dscommerce.orders.domain.vo.OrderId;
import com.devsuperior.dscommerce.shared.domain.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Testes do agregado {@link Order}: o <b>domínio rico</b> em ação. Nenhum Spring, nenhum banco — as regras de
 * negócio são verificadas só com objetos Java, e por isso o teste roda em milissegundos.
 */
class OrderTest {

    private static final ClientId CLIENT = new ClientId(1L);

    private Order newOrder() {
        return Order.create(CLIENT);
    }

    @Test
    @DisplayName("um pedido novo aguarda pagamento, ainda não tem id e guarda o cliente")
    void shouldCreateOrderWaitingPayment() {
        Order order = newOrder();

        assertThat(order.getId()).isNull();
        assertThat(order.getClientId()).isEqualTo(CLIENT);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.WAITING_PAYMENT);
        assertThat(order.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("não existe pedido sem cliente")
    void shouldRejectOrderWithoutClient() {
        assertThatThrownBy(() -> Order.create(null))
                .isInstanceOf(DomainException.class)
                .hasMessage("client id cannot be null");
    }

    @Test
    @DisplayName("restore exige id; create é que gera pedido sem id")
    void shouldRequireIdWhenRestoring() {
        LocalDateTime now = LocalDateTime.now();

        assertThatThrownBy(() -> Order.restore(null, CLIENT, OrderStatus.PAID, now))
                .isInstanceOf(DomainException.class)
                .hasMessage("Order ID cannot be null.");

        Order restored = Order.restore(new OrderId(7L), CLIENT, OrderStatus.PAID, now);
        assertThat(restored.getId()).isEqualTo(new OrderId(7L));
        assertThat(restored.getStatus()).isEqualTo(OrderStatus.PAID);
    }

    @Test
    @DisplayName("ciclo de vida completo: pagar → enviar → entregar")
    void shouldFollowTheHappyPath() {
        Order order = newOrder();

        order.pay();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);

        order.ship();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.SHIPPED);

        order.deliver();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.DELIVERED);
    }

    @Test
    @DisplayName("pode cancelar enquanto aguarda pagamento ou depois de pago")
    void shouldCancelBeforeShipping() {
        Order waiting = newOrder();
        waiting.cancel();
        assertThat(waiting.getStatus()).isEqualTo(OrderStatus.CANCELED);

        Order paid = newOrder();
        paid.pay();
        paid.cancel();
        assertThat(paid.getStatus()).isEqualTo(OrderStatus.CANCELED);
    }

    @Test
    @DisplayName("não pode enviar um pedido que ainda não foi pago")
    void shouldNotShipUnpaidOrder() {
        Order order = newOrder();

        assertThatThrownBy(order::ship)
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("cannot transition from WAITING_PAYMENT to SHIPPED");
        assertThat(order.getStatus()).isEqualTo(OrderStatus.WAITING_PAYMENT);
    }

    @Test
    @DisplayName("não pode cancelar depois de enviado")
    void shouldNotCancelShippedOrder() {
        Order order = newOrder();
        order.pay();
        order.ship();

        assertThatThrownBy(order::cancel).isInstanceOf(DomainException.class);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.SHIPPED);
    }

    @Test
    @DisplayName("estados finais (entregue e cancelado) não aceitam mais nenhuma transição")
    void shouldNotLeaveFinalStates() {
        Order delivered = newOrder();
        delivered.pay();
        delivered.ship();
        delivered.deliver();
        assertThatThrownBy(delivered::cancel).isInstanceOf(DomainException.class);
        assertThatThrownBy(delivered::pay).isInstanceOf(DomainException.class);

        Order canceled = newOrder();
        canceled.cancel();
        assertThatThrownBy(canceled::pay).isInstanceOf(DomainException.class);
    }

    @Test
    @DisplayName("repetir a transição para o estado atual é idempotente: não falha nem altera nada")
    void shouldBeIdempotentWhenRepeatingTransition() {
        Order order = newOrder();
        order.pay();

        order.pay();

        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);
    }

    @Test
    @DisplayName("igualdade é por identidade: pedidos sem id nunca são iguais entre si")
    void shouldCompareByIdentity() {
        LocalDateTime now = LocalDateTime.now();
        Order a = Order.restore(new OrderId(1L), CLIENT, OrderStatus.PAID, now);
        Order sameId = Order.restore(new OrderId(1L), new ClientId(2L), OrderStatus.CANCELED, now);
        Order otherId = Order.restore(new OrderId(2L), CLIENT, OrderStatus.PAID, now);

        assertThat(a).isEqualTo(sameId).isNotEqualTo(otherId);
        assertThat(newOrder()).isNotEqualTo(newOrder());
    }
}
