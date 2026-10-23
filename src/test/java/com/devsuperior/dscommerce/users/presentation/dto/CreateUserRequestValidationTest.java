package com.devsuperior.dscommerce.users.presentation.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testa as anotações de validação do {@link CreateUserRequest} <b>sem subir o Spring</b>.
 *
 * <p>As anotações ({@code @NotBlank}, {@code @Size}…) são apenas "marcas". Quem as executa é o <i>Bean
 * Validation</i>; aqui chamamos o {@link Validator} dele diretamente, exatamente como o Spring MVC faz quando vê
 * {@code @Valid} no controller. O teste roda em milissegundos e prova, para cada campo, o que é aceito e o que é
 * recusado.
 */
class CreateUserRequestValidationTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void createValidator() {
        // Cria o validador padrão (Hibernate Validator) uma única vez para todos os testes da classe.
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void closeFactory() {
        factory.close();
    }

    /** Um request totalmente válido; cada teste troca só o campo que quer exercitar. */
    private CreateUserRequest request(String name, String email, String phone, String password) {
        return new CreateUserRequest(name, email, phone, LocalDate.of(1990, 5, 20), password);
    }

    private CreateUserRequest validRequest() {
        return request("Maria Brown", "maria@example.com", "11999998888", "secret123");
    }

    /** Devolve os NOMES dos campos que violaram alguma restrição, para asserções curtas. */
    private Set<String> invalidFields(CreateUserRequest request) {
        return validator.validate(request).stream()
                .map(ConstraintViolation::getPropertyPath)
                .map(Object::toString)
                .collect(Collectors.toSet());
    }

    @Test
    @DisplayName("um request completo e bem formado não tem violações")
    void shouldAcceptValidRequest() {
        assertThat(invalidFields(validRequest())).isEmpty();
    }

    @Test
    @DisplayName("telefone formatado é aceito pela borda web; quem valida os 11 dígitos é o domínio (bug 2.6)")
    void shouldAcceptFormattedPhone() {
        // Antes um @Pattern exigia "somente 11 dígitos" aqui e recusava estes valores.
        assertThat(invalidFields(request("Maria Brown", "maria@example.com", "(11) 99999-8888", "secret123")))
                .isEmpty();
        assertThat(invalidFields(request("Maria Brown", "maria@example.com", "11 99999-8888", "secret123")))
                .isEmpty();
    }

    @Test
    @DisplayName("telefone em branco ou maior que a coluna (20) é recusado")
    void shouldRejectBlankOrOversizedPhone() {
        assertThat(invalidFields(request("Maria Brown", "maria@example.com", "  ", "secret123")))
                .containsExactly("phone");
        assertThat(invalidFields(request("Maria Brown", "maria@example.com", "1".repeat(21), "secret123")))
                .containsExactly("phone");
        assertThat(invalidFields(request("Maria Brown", "maria@example.com", "1".repeat(20), "secret123")))
                .isEmpty();
    }

    @Test
    @DisplayName("nome: em branco é recusado; 100 caracteres passam e 101 não (bug 2.7)")
    void shouldValidateNameSize() {
        assertThat(invalidFields(request("", "maria@example.com", "11999998888", "secret123")))
                .containsExactly("name");
        assertThat(invalidFields(request("a".repeat(100), "maria@example.com", "11999998888", "secret123")))
                .isEmpty();
        assertThat(invalidFields(request("a".repeat(101), "maria@example.com", "11999998888", "secret123")))
                .containsExactly("name");
    }

    @Test
    @DisplayName("e-mail: formato inválido e mais de 100 caracteres são recusados (bug 2.7)")
    void shouldValidateEmail() {
        assertThat(invalidFields(request("Maria Brown", "not-an-email", "11999998888", "secret123")))
                .containsExactly("email");

        String tooLong = "a".repeat(100 - "@example.com".length() + 1) + "@example.com"; // 101 caracteres
        assertThat(invalidFields(request("Maria Brown", tooLong, "11999998888", "secret123")))
                .containsExactly("email");
    }

    @Test
    @DisplayName("senha: mínimo de 8 caracteres (bug 2.8)")
    void shouldRequireMinimumPasswordLength() {
        assertThat(invalidFields(request("Maria Brown", "maria@example.com", "11999998888", "1234567")))
                .containsExactly("password");
        assertThat(invalidFields(request("Maria Brown", "maria@example.com", "11999998888", "12345678")))
                .isEmpty();
    }

    @Test
    @DisplayName("senha: frases longas são aceitas (antes o teto era 20); o teto agora é 128 (bug 2.8)")
    void shouldAcceptLongPassphrasesUpTo128() {
        String passphrase = "correct horse battery staple 2026!"; // 34 caracteres: o teto antigo (20) a recusava
        assertThat(invalidFields(request("Maria Brown", "maria@example.com", "11999998888", passphrase)))
                .isEmpty();

        assertThat(invalidFields(request("Maria Brown", "maria@example.com", "11999998888", "x".repeat(128))))
                .isEmpty();
        assertThat(invalidFields(request("Maria Brown", "maria@example.com", "11999998888", "x".repeat(129))))
                .containsExactly("password");
    }

    @Test
    @DisplayName("data de nascimento: obrigatória e não pode ser futura")
    void shouldValidateDateOfBirth() {
        CreateUserRequest missing = new CreateUserRequest(
                "Maria Brown", "maria@example.com", "11999998888", null, "secret123");
        CreateUserRequest future = new CreateUserRequest(
                "Maria Brown", "maria@example.com", "11999998888", LocalDate.now().plusDays(1), "secret123");

        assertThat(invalidFields(missing)).containsExactly("dateOfBirth");
        assertThat(invalidFields(future)).containsExactly("dateOfBirth");
    }

    @Test
    @DisplayName("toCommand copia todos os campos para o comando da aplicação")
    void shouldConvertToCommand() {
        var command = validRequest().toCommand();

        assertThat(command.name()).isEqualTo("Maria Brown");
        assertThat(command.email()).isEqualTo("maria@example.com");
        assertThat(command.phone()).isEqualTo("11999998888");
        assertThat(command.dateOfBirth()).isEqualTo(LocalDate.of(1990, 5, 20));
        assertThat(command.password()).isEqualTo("secret123");
    }
}
