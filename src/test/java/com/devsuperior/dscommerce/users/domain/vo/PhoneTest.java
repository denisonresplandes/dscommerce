package com.devsuperior.dscommerce.users.domain.vo;

import com.devsuperior.dscommerce.shared.domain.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PhoneTest {

    @ParameterizedTest
    @ValueSource(strings = {"11999998888", " 11999998888 ", "(11) 99999-8888", "11.99999.8888"})
    @DisplayName("normaliza para somente dígitos")
    void shouldKeepOnlyDigits(String input) {
        assertThat(new Phone(input).value()).isEqualTo("11999998888");
    }

    @ParameterizedTest
    @ValueSource(strings = {"123", "1199999888", "119999988888"})
    @DisplayName("exige exatamente 11 dígitos (regra local do Brasil)")
    void shouldRequireElevenDigits(String input) {
        assertThatThrownBy(() -> new Phone(input))
                .isInstanceOf(DomainException.class)
                .hasMessage("user phone number length must be 11 digits");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "abc", "()-"})
    @DisplayName("rejeita o que não tem nenhum dígito")
    void shouldRejectWhenThereAreNoDigits(String input) {
        assertThatThrownBy(() -> new Phone(input))
                .isInstanceOf(DomainException.class)
                .hasMessage("user phone number cannot be blank");
    }

    @Test
    @DisplayName("rejeita nulo")
    void shouldRejectNull() {
        assertThatThrownBy(() -> new Phone(null))
                .isInstanceOf(DomainException.class)
                .hasMessage("user phone number is required");
    }
}
