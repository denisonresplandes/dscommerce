package com.devsuperior.dscommerce.users.domain.vo;

import com.devsuperior.dscommerce.shared.domain.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class NameTest {

    @ParameterizedTest
    @ValueSource(strings = {"Maria", "Maria Brown", "Jo", "José da Silva"})
    @DisplayName("aceita nomes válidos")
    void shouldAcceptValidNames(String input) {
        assertThat(new Name(input).value()).isEqualTo(input);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   "})
    @DisplayName("rejeita vazio ou em branco")
    void shouldRejectBlank(String input) {
        assertThatThrownBy(() -> new Name(input))
                .isInstanceOf(DomainException.class)
                .hasMessage("user name is required");
    }

    @Test
    @DisplayName("rejeita nome com menos de 2 caracteres")
    void shouldRejectTooShort() {
        assertThatThrownBy(() -> new Name("A"))
                .isInstanceOf(DomainException.class)
                .hasMessage("user name is too short.");
    }

    @Test
    @DisplayName("rejeita nome com número")
    void shouldRejectDigits() {
        assertThatThrownBy(() -> new Name("Fulano 2"))
                .isInstanceOf(DomainException.class)
                .hasMessage("user name cannot contain numbers.");
    }

    @Test
    @DisplayName("rejeita nulo")
    void shouldRejectNull() {
        assertThatThrownBy(() -> new Name(null))
                .isInstanceOf(DomainException.class)
                .hasMessage("user name is required");
    }
}
