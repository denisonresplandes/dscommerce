package com.devsuperior.dscommerce.users.domain.vo;

import com.devsuperior.dscommerce.shared.domain.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class NameTest {

    private static final String INVALID_CHARS =
            "user name may contain only letters, spaces, apostrophes, hyphens and periods.";

    @ParameterizedTest
    @ValueSource(strings = {"Maria", "Maria Brown", "Jo", "José da Silva", "Maria D'Ávila", "João-Pedro", "Jr."})
    @DisplayName("aceita nomes válidos (acentos, apóstrofo, hífen e ponto incluídos)")
    void shouldAcceptValidNames(String input) {
        assertThat(new Name(input).value()).isEqualTo(input);
    }

    /**
     * Cada linha do CsvSource é "entrada ; resultado esperado". Usamos aspas simples para delimitar os valores
     * porque algumas entradas têm espaços nas pontas, que o CSV removeria sem elas.
     */
    @ParameterizedTest(name = "\"{0}\" vira \"{1}\"")
    @CsvSource(delimiter = ';', quoteCharacter = '\'', value = {
            "'  Maria Brown  ' ; Maria Brown",
            "'Maria    Brown'  ; Maria Brown",
            "'  Maria   da   Silva ' ; Maria da Silva"
    })
    @DisplayName("normaliza: remove espaços nas pontas e reduz espaços repetidos a um só")
    void shouldNormalizeWhitespace(String input, String expected) {
        assertThat(new Name(input).value()).isEqualTo(expected);
    }

    @Test
    @DisplayName("dois nomes que diferem só nos espaços são o mesmo valor")
    void shouldBeEqualAfterNormalization() {
        assertThat(new Name(" Maria  Brown")).isEqualTo(new Name("Maria Brown "));
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
    @DisplayName("rejeita nome com menos de 2 caracteres, mesmo com espaços em volta")
    void shouldRejectTooShort() {
        assertThatThrownBy(() -> new Name("A"))
                .isInstanceOf(DomainException.class)
                .hasMessage("user name is too short.");
        // Após o trim sobra 1 letra: o tamanho é medido DEPOIS de normalizar.
        assertThatThrownBy(() -> new Name("   A   "))
                .isInstanceOf(DomainException.class)
                .hasMessage("user name is too short.");
    }

    @Test
    @DisplayName("aceita 100 caracteres (tamanho da coluna) e rejeita 101")
    void shouldEnforceMaxLength() {
        assertThat(new Name("a".repeat(Name.MAX_LENGTH)).value()).hasSize(100);

        assertThatThrownBy(() -> new Name("a".repeat(Name.MAX_LENGTH + 1)))
                .isInstanceOf(DomainException.class)
                .hasMessage("user name is too long.");
    }

    @ParameterizedTest
    @ValueSource(strings = {"Fulano 2", "R2D2", "123"})
    @DisplayName("rejeita nome com número (mensagem específica preservada)")
    void shouldRejectDigits(String input) {
        assertThatThrownBy(() -> new Name(input))
                .isInstanceOf(DomainException.class)
                .hasMessage("user name cannot contain numbers.");
    }

    /**
     * Regressão do bug 2.5: antes só os dígitos eram barrados, e símbolos passavam. "-Maria" e ".Maria" testam
     * também a regra de que o nome precisa COMEÇAR por letra.
     */
    @ParameterizedTest
    @ValueSource(strings = {"$%&@#", "Maria_Brown", "Maria<script>", "-Maria", ".Maria", "Maria!"})
    @DisplayName("rejeita símbolos e nomes que não começam por letra")
    void shouldRejectInvalidCharacters(String input) {
        assertThatThrownBy(() -> new Name(input))
                .isInstanceOf(DomainException.class)
                .hasMessage(INVALID_CHARS);
    }

    @Test
    @DisplayName("rejeita nulo")
    void shouldRejectNull() {
        assertThatThrownBy(() -> new Name(null))
                .isInstanceOf(DomainException.class)
                .hasMessage("user name is required");
    }
}
