package com.devsuperior.dscommerce.orders.domain;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import static com.devsuperior.dscommerce.orders.domain.OrderStatus.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Teste da máquina de estados do pedido. Em vez de listar alguns casos, percorre <b>todos os pares</b>
 * (origem, destino) e compara com a tabela de transições esperada — assim qualquer transição nova ou removida
 * por engano faz o teste falhar.
 *
 * <pre>
 *   WAITING_PAYMENT ──▶ PAID ──▶ SHIPPED ──▶ DELIVERED
 *          │              │
 *          └──────────────┴──▶ CANCELED
 * </pre>
 */
class OrderStatusTest {

    private static final Map<OrderStatus, Set<OrderStatus>> ALLOWED = Map.of(
            WAITING_PAYMENT, EnumSet.of(PAID, CANCELED),
            PAID, EnumSet.of(SHIPPED, CANCELED),
            SHIPPED, EnumSet.of(DELIVERED),
            DELIVERED, EnumSet.noneOf(OrderStatus.class),
            CANCELED, EnumSet.noneOf(OrderStatus.class)
    );

    @ParameterizedTest(name = "a partir de {0}")
    @EnumSource(OrderStatus.class)
    void shouldAllowOnlyTheDocumentedTransitions(OrderStatus from) {
        for (OrderStatus to : OrderStatus.values()) {
            assertThat(from.canTransitionTo(to))
                    .as("%s -> %s", from, to)
                    .isEqualTo(ALLOWED.get(from).contains(to));
        }
    }
}
