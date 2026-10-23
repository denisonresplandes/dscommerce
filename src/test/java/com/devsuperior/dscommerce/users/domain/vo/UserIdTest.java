package com.devsuperior.dscommerce.users.domain.vo;

import com.devsuperior.dscommerce.shared.domain.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserIdTest {

    @Test
    @DisplayName("aceita id positivo")
    void shouldAcceptPositive() {
        assertThat(new UserId(1L).value()).isEqualTo(1L);
    }

    @ParameterizedTest
    @ValueSource(longs = {0L, -1L, Long.MIN_VALUE})
    @DisplayName("rejeita zero e negativos")
    void shouldRejectNonPositive(long value) {
        assertThatThrownBy(() -> new UserId(value))
                .isInstanceOf(DomainException.class)
                .hasMessage("user id must be a positive number.");
    }

    @Test
    @DisplayName("rejeita nulo")
    void shouldRejectNull() {
        assertThatThrownBy(() -> new UserId(null)).isInstanceOf(DomainException.class);
    }
}
