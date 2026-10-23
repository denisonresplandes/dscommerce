package com.devsuperior.dscommerce.users.domain.vo;

import com.devsuperior.dscommerce.shared.domain.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DateOfBirthTest {

    @Test
    @DisplayName("aceita data passada e a data de hoje")
    void shouldAcceptPastAndToday() {
        assertThat(new DateOfBirth(LocalDate.of(1990, 1, 1)).value()).isEqualTo(LocalDate.of(1990, 1, 1));
        assertThat(new DateOfBirth(LocalDate.now()).value()).isEqualTo(LocalDate.now());
    }

    @Test
    @DisplayName("rejeita data futura")
    void shouldRejectFutureDate() {
        assertThatThrownBy(() -> new DateOfBirth(LocalDate.now().plusDays(1)))
                .isInstanceOf(DomainException.class)
                .hasMessage("user date of birth cannot be later than now.");
    }

    @Test
    @DisplayName("rejeita nulo")
    void shouldRejectNull() {
        assertThatThrownBy(() -> new DateOfBirth(null))
                .isInstanceOf(DomainException.class)
                .hasMessage("user date of birth is required");
    }
}
