package com.devsuperior.dscommerce.users.presentation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Testes de API da validação de parâmetros de URL (bugs 2.4 e 2.9).
 *
 * <p>Distinguem dois tipos de erro que antes se confundiam:
 * <ul>
 *   <li><b>400 Bad Request</b> — a requisição está malformada (e-mail sem "@", id negativo): barrada na borda web;</li>
 *   <li><b>404 Not Found</b> — a requisição é válida, mas o recurso não existe.</li>
 * </ul>
 */
@SpringBootTest
@AutoConfigureMockMvc
class QueryParameterValidationApiTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * Bug 2.4: antes, qualquer texto chegava ao value object {@code Email} e só lá era recusado, com 422 (que é
     * "dado válido na forma mas viola regra de negócio"). Um e-mail sem "@" é erro de FORMA: 400.
     */
    @ParameterizedTest(name = "email=\"{0}\"")
    @ValueSource(strings = {"not-an-email", "foo@", "@bar.com", "foo bar@baz.com"})
    @DisplayName("GET /users/email com e-mail malformado → 400")
    void shouldReturn400ForMalformedEmail(String email) throws Exception {
        mockMvc.perform(get("/users/email").param("email", email))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /users/email com parâmetro em branco → 400")
    void shouldReturn400ForBlankEmail() throws Exception {
        mockMvc.perform(get("/users/email").param("email", "   "))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /users/email sem o parâmetro → 400")
    void shouldReturn400WhenEmailParameterIsMissing() throws Exception {
        mockMvc.perform(get("/users/email"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("e-mail bem formado mas inexistente continua sendo 404 (e não 400)")
    void shouldStillReturn404ForWellFormedUnknownEmail() throws Exception {
        mockMvc.perform(get("/users/email").param("email", "ninguem@example.com"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("e-mail bem formado e existente continua funcionando")
    void shouldStillFindExistingUser() throws Exception {
        mockMvc.perform(get("/users/email").param("email", "maria@gmail.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Maria Brown"));
    }

    /**
     * Bug 2.9: a resposta de erro de validação continha o nome interno do método do controller
     * ("findById", "findAllByUserId"…). Checamos todos os endpoints com parâmetro de URL.
     */
    @ParameterizedTest(name = "GET {0}")
    @ValueSource(strings = {"/users/0", "/users/-5", "/orders/0", "/orders/-1", "/users/0/orders"})
    @DisplayName("id não positivo → 400 sem expor nomes internos de métodos")
    void shouldReturn400WithoutLeakingMethodNames(String path) throws Exception {
        mockMvc.perform(get(path))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(not(containsString("findById"))))
                .andExpect(content().string(not(containsString("findAllByUserId"))))
                .andExpect(content().string(not(containsString("findByEmail"))));
    }

    @Test
    @DisplayName("id que não é número → 400")
    void shouldReturn400ForNonNumericId() throws Exception {
        mockMvc.perform(get("/users/abc"))
                .andExpect(status().isBadRequest());
    }
}
