package com.devsuperior.dscommerce.users.domain.vo;

import com.devsuperior.dscommerce.shared.domain.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EmailTest {

    @Test
    @DisplayName("normaliza: remove espaços nas pontas e converte para minúsculas")
    void shouldNormalize() {
        assertThat(new Email("  Foo.Bar@Example.COM ").value()).isEqualTo("foo.bar@example.com");
    }

    @Test
    @DisplayName("dois e-mails que diferem só na formatação são o mesmo valor")
    void shouldBeEqualAfterNormalization() {
        assertThat(new Email("MARIA@gmail.com")).isEqualTo(new Email(" maria@gmail.com"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "foo", "foo@", "@bar.com", "foo@bar", "foo bar@baz.com", "foo@bar.c"})
    @DisplayName("rejeita formatos inválidos")
    void shouldRejectInvalidFormats(String invalid) {
        assertThatThrownBy(() -> new Email(invalid))
                .isInstanceOf(DomainException.class)
                .hasMessage("invalid user email address");
    }

    @Test
    @DisplayName("rejeita nulo")
    void shouldRejectNull() {
        assertThatThrownBy(() -> new Email(null))
                .isInstanceOf(DomainException.class)
                .hasMessage("user mail address is required");
    }
}
