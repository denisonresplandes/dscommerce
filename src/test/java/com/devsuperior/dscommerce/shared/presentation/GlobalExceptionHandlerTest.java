package com.devsuperior.dscommerce.shared.presentation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Teste de integração (contexto completo + banco H2 populado pelo {@code import.sql}) que fixa o contrato de erros
 * da API. Antes do {@link GlobalExceptionHandler}, todos estes cenários respondiam HTTP 500.
 *
 * <p>Este teste também serve de <b>rede de segurança da refatoração</b>: ele não conhece pacotes nem classes
 * internas, só HTTP. Se a estrutura interna mudar e o comportamento externo não, ele continua verde.
 */
@SpringBootTest
@AutoConfigureMockMvc
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("usuário inexistente → 404 em formato ProblemDetail")
    void shouldReturn404WhenUserDoesNotExist() throws Exception {
        mockMvc.perform(get("/users/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.detail").value("User with id 999 not found."));
    }

    @Test
    @DisplayName("pedido inexistente → 404")
    void shouldReturn404WhenOrderDoesNotExist() throws Exception {
        mockMvc.perform(get("/orders/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("pedidos de usuário inexistente → 404")
    void shouldReturn404WhenListingOrdersOfUnknownUser() throws Exception {
        mockMvc.perform(get("/users/999/orders"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("regra de domínio violada (e-mail inválido) → 422")
    void shouldReturn422WhenDomainRuleIsViolated() throws Exception {
        mockMvc.perform(get("/users/email").param("email", "not-an-email"))
                .andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.detail").value("invalid user email address"));
    }

    @Test
    @DisplayName("id não positivo na URL → 400")
    void shouldReturn400WhenPathVariableIsNotPositive() throws Exception {
        mockMvc.perform(get("/users/0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("caminho feliz continua respondendo 200")
    void shouldReturn200WhenUserExists() throws Exception {
        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("maria@gmail.com"));
    }
}
