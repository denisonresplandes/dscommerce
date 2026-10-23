package com.devsuperior.dscommerce.users.presentation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Testes de API das regras de entrada do {@code POST /users} (bugs 2.6, 2.7 e 2.8), de ponta a ponta.
 *
 * <p>Complementam o {@code CreateUserRequestValidationTest} (que testa só as anotações): aqui se confere o
 * resultado completo — o código HTTP e o conteúdo gravado, passando pela validação web, pelo domínio e pelo banco.
 */
@SpringBootTest
@AutoConfigureMockMvc
class CreateUserApiValidationTest {

    @Autowired
    private MockMvc mockMvc;

    private static String uniqueEmail() {
        return "val-" + UUID.randomUUID() + "@example.com";
    }

    /** Envia um POST /users com os valores indicados (e-mail único por padrão, para não colidir com outros). */
    private ResultActions createUser(String name, String phone, String password) throws Exception {
        return createUser(name, uniqueEmail(), phone, password);
    }

    private ResultActions createUser(String name, String email, String phone, String password) throws Exception {
        String body = """
                {
                  "name": "%s",
                  "email": "%s",
                  "phone": "%s",
                  "dateOfBirth": "1990-05-20",
                  "password": "%s"
                }
                """.formatted(name, email, phone, password);
        return mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(body));
    }

    @Test
    @DisplayName("telefone formatado é aceito e gravado só com dígitos (bug 2.6)")
    void shouldNormalizeFormattedPhone() throws Exception {
        createUser("Carlos Silva", "(11) 99999-8888", "secret123")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.phone").value("11999998888"));
    }

    @Test
    @DisplayName("telefone com quantidade errada de dígitos → 422 com a mensagem da regra de domínio (bug 2.6)")
    void shouldReturn422ForPhoneWithWrongDigitCount() throws Exception {
        createUser("Carlos Silva", "123", "secret123")
                .andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.detail").value("user phone number length must be 11 digits"));
    }

    @Test
    @DisplayName("nome longo demais → 400 pela validação web, e não 500 por estouro da coluna (bug 2.7)")
    void shouldReturn400ForOversizedName() throws Exception {
        createUser("a".repeat(101), "11999998888", "secret123")
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("nome com 100 caracteres cabe na coluna e é gravado (bug 2.7)")
    void shouldAcceptNameAtColumnLimit() throws Exception {
        createUser("a".repeat(100), "11999998888", "secret123")
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("e-mail longo demais → 400 (bug 2.7)")
    void shouldReturn400ForOversizedEmail() throws Exception {
        String tooLong = "a".repeat(100 - "@example.com".length() + 1) + "@example.com"; // 101 caracteres
        createUser("Carlos Silva", tooLong, "11999998888", "secret123")
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("senha com 7 caracteres → 400; com 8 → 201 (bug 2.8)")
    void shouldEnforceMinimumPasswordLength() throws Exception {
        createUser("Carlos Silva", "11999998888", "1234567")
                .andExpect(status().isBadRequest());
        createUser("Carlos Silva", "11999998888", "12345678")
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("frase-senha longa (antes recusada, teto de 20) é aceita (bug 2.8)")
    void shouldAcceptLongPassphrase() throws Exception {
        createUser("Carlos Silva", "11999998888", "correct horse battery staple 2026!")
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("senha acima de 128 caracteres → 400 (bug 2.8)")
    void shouldRejectHugePassword() throws Exception {
        createUser("Carlos Silva", "11999998888", "x".repeat(129))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("nome com símbolos → 422 (bug 2.5)")
    void shouldRejectNameWithSymbols() throws Exception {
        createUser("$%&@#", "11999998888", "secret123")
                .andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.detail")
                        .value("user name may contain only letters, spaces, apostrophes, hyphens and periods."));
    }

    @Test
    @DisplayName("espaços repetidos no nome são normalizados na resposta (bug 2.5)")
    void shouldNormalizeNameWhitespace() throws Exception {
        createUser("  Carlos    Silva ", "11999998888", "secret123")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Carlos Silva"));
    }
}
