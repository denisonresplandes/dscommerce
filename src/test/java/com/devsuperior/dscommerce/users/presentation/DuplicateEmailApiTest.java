package com.devsuperior.dscommerce.users.presentation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Testes de API do bug 2.3 (e-mail duplicado), de ponta a ponta: HTTP → controller → service → banco H2 real.
 * Antes da correção, o segundo cadastro era aceito e gerava dois usuários com o mesmo e-mail.
 *
 * <p>Cada teste usa um e-mail aleatório (UUID) para não colidir com os outros, já que o banco é compartilhado
 * entre os testes da mesma execução.
 */
@SpringBootTest
@AutoConfigureMockMvc
class DuplicateEmailApiTest {

    @Autowired
    private MockMvc mockMvc;

    private static String uniqueEmail() {
        return "dup-" + UUID.randomUUID() + "@example.com";
    }

    private static String bodyWithEmail(String email) {
        return """
                {
                  "name": "Carlos Silva",
                  "email": "%s",
                  "phone": "11999998888",
                  "dateOfBirth": "1990-05-20",
                  "password": "secret123"
                }
                """.formatted(email);
    }

    private int countUsers() throws Exception {
        MvcResult result = mockMvc.perform(get("/users")).andExpect(status().isOk()).andReturn();
        // O corpo é um array JSON; contamos quantas vezes aparece a chave "id".
        return result.getResponse().getContentAsString().split("\"id\"", -1).length - 1;
    }

    @Test
    @DisplayName("o segundo cadastro com o mesmo e-mail → 409 e nenhum usuário novo é criado")
    void shouldReturn409WhenEmailIsAlreadyRegistered() throws Exception {
        String email = uniqueEmail();

        // 1º cadastro: normal.
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(bodyWithEmail(email)))
                .andExpect(status().isCreated());
        int usersAfterFirst = countUsers();

        // 2º cadastro, idêntico: recusado como conflito.
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(bodyWithEmail(email)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.detail").value("A user with e-mail %s already exists.".formatted(email)));

        assertThat(countUsers()).as("o cadastro recusado não pode criar usuário").isEqualTo(usersAfterFirst);
    }

    /**
     * Só maiúsculas são testadas aqui: o {@code @Email} do Request já recusa espaços (400) antes de o dado chegar ao
     * value object. A normalização de espaços é coberta em {@code UserCommandServiceTest}, onde o service é chamado
     * diretamente, sem a camada web na frente.
     */
    @Test
    @DisplayName("maiúsculas não burlam a verificação: o e-mail é normalizado antes de comparar")
    void shouldTreatEmailsAsEqualIgnoringCase() throws Exception {
        String email = uniqueEmail();
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(bodyWithEmail(email)))
                .andExpect(status().isCreated());

        String disguised = email.toUpperCase();
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(bodyWithEmail(disguised)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("e-mail de um usuário que veio do import.sql também conta como já cadastrado")
    void shouldReturn409ForSeedUserEmail() throws Exception {
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(bodyWithEmail("maria@gmail.com")))
                .andExpect(status().isConflict());

        // O usuário original continua intacto.
        mockMvc.perform(get("/users/email").param("email", "maria@gmail.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Maria Brown"));
    }
}
