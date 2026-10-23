package com.devsuperior.dscommerce.orders.domain.vo;

import com.devsuperior.dscommerce.shared.domain.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Identificadores do módulo de pedidos: {@link OrderId} e {@link ClientId}. Ambos devem ser positivos.
 */
class OrderIdentifiersTest {

    @Test
    @DisplayName("aceitam valor positivo")
    void shouldAcceptPositive() {
        assertThat(new OrderId(1L).value()).isEqualTo(1L);
        assertThat(new ClientId(1L).value()).isEqualTo(1L);
    }

    @ParameterizedTest
    @ValueSource(longs = {0L, -5L})
    @DisplayName("rejeitam zero e negativos")
    void shouldRejectNonPositive(long value) {
        assertThatThrownBy(() -> new OrderId(value))
                .isInstanceOf(DomainException.class)
                .hasMessage("order id must be a positive number.");
        assertThatThrownBy(() -> new ClientId(value))
                .isInstanceOf(DomainException.class)
                .hasMessage("client id must be a positive number.");
    }

    @Test
    @DisplayName("rejeitam nulo")
    void shouldRejectNull() {
        assertThatThrownBy(() -> new OrderId(null)).isInstanceOf(DomainException.class);
        assertThatThrownBy(() -> new ClientId(null)).isInstanceOf(DomainException.class);
    }

    @Test
    @DisplayName("são tipos distintos: ClientId(1) não é igual a OrderId(1)")
    void shouldNotMixUpIdentifiers() {
        assertThat((Object) new ClientId(1L)).isNotEqualTo(new OrderId(1L));
    }
}
