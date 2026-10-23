package com.devsuperior.dscommerce.shared.presentation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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

    /**
     * O corpo abaixo passa na validação de formato do Request (todos os campos preenchidos e bem formados), mas o
     * nome tem um número — o que o value object {@code Name} proíbe. É a regra de DOMÍNIO que falha, não a
     * validação web, por isso a resposta é 422 e não 400.
     */
    @Test
    @DisplayName("regra de domínio violada (nome com número) → 422")
    void shouldReturn422WhenDomainRuleIsViolated() throws Exception {
        String body = """
                {
                  "name": "Fulano 2",
                  "email": "fulano.dois@example.com",
                  "phone": "11999998888",
                  "dateOfBirth": "1990-05-20",
                  "password": "secret123"
                }
                """;

        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.detail").value("user name cannot contain numbers."));
    }

    /**
     * Regressão do bug 2.9: a mensagem de erro de validação incluía o nome do método do controller
     * ("findById.id: ..."), expondo um detalhe interno. Aqui só exigimos que ele NÃO apareça; o texto exato varia
     * conforme o mecanismo de validação em uso.
     */
    @Test
    @DisplayName("id não positivo na URL → 400, sem vazar o nome do método do controller")
    void shouldReturn400WhenPathVariableIsNotPositive() throws Exception {
        mockMvc.perform(get("/users/0"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(not(containsString("findById"))));
    }

    @Test
    @DisplayName("caminho feliz continua respondendo 200")
    void shouldReturn200WhenUserExists() throws Exception {
        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("maria@gmail.com"));
    }
}
