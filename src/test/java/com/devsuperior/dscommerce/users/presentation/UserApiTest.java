package com.devsuperior.dscommerce.users.presentation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Testes de API do recurso {@code /users}, de ponta a ponta (HTTP → service → banco H2 com {@code import.sql}).
 *
 * <p>Verificam apenas o contrato externo (status, headers, JSON) e não conhecem pacotes nem classes internas.
 * Por isso servem de rede de segurança para a migração em camadas: se a estrutura interna mudar e estes testes
 * continuarem verdes, o comportamento da API foi preservado.
 *
 * <p>O banco é compartilhado entre os testes da mesma execução, então nenhum teste assume a quantidade exata de
 * usuários nem reutiliza e-mails.
 */
@SpringBootTest
@AutoConfigureMockMvc
class UserApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("POST /users cria o usuário: 201, Location e corpo sem senha")
    void shouldCreateUser() throws Exception {
        String body = """
                {
                  "name": "Carlos Silva",
                  "email": "Carlos.Silva@Example.com",
                  "phone": "11999998888",
                  "dateOfBirth": "1990-05-20",
                  "password": "secret123"
                }
                """;

        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", org.hamcrest.Matchers.containsString("/users/")))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Carlos Silva"))
                // o e-mail é normalizado (minúsculas) pelo value object Email
                .andExpect(jsonPath("$.email").value("carlos.silva@example.com"))
                .andExpect(jsonPath("$.phone").value("11999998888"))
                .andExpect(jsonPath("$.dateOfBirth").value("1990-05-20"))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.passwordHash").doesNotExist());
    }

    @Test
    @DisplayName("POST /users com corpo inválido → 400")
    void shouldReturn400WhenRequestBodyIsInvalid() throws Exception {
        String body = """
                {
                  "name": "",
                  "email": "not-an-email",
                  "phone": "123",
                  "dateOfBirth": "1990-05-20",
                  "password": "123"
                }
                """;

        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /users com regra de domínio violada (nome com número) → 422")
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

    @Test
    @DisplayName("GET /users lista os usuários")
    void shouldListUsers() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", greaterThanOrEqualTo(2)))
                .andExpect(jsonPath("$[0].password").doesNotExist());
    }

    @Test
    @DisplayName("GET /users/{id} devolve o usuário")
    void shouldFindUserById() throws Exception {
        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Maria Brown"))
                .andExpect(jsonPath("$.email").value("maria@gmail.com"))
                .andExpect(jsonPath("$.phone").value("98888888888"))
                .andExpect(jsonPath("$.dateOfBirth").value("2001-07-25"));
    }

    @Test
    @DisplayName("GET /users/email é insensível a maiúsculas")
    void shouldFindUserByEmailIgnoringCase() throws Exception {
        // Sem espaços nas pontas: desde o bug 2.4 o @Email do parâmetro recusa e-mail com espaço (400).
        mockMvc.perform(get("/users/email").param("email", "ALEX@gmail.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Alex Green"));
    }

    @Test
    @DisplayName("GET /users/email de usuário inexistente → 404")
    void shouldReturn404WhenEmailDoesNotExist() throws Exception {
        mockMvc.perform(get("/users/email").param("email", "ninguem@example.com"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value(endsWith("not found.")));
    }
}
