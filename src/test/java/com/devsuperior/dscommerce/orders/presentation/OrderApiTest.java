package com.devsuperior.dscommerce.orders.presentation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Testes de API dos pedidos, a partir dos dados do {@code import.sql} (3 pedidos: #1 e #3 do usuário 1, #2 do
 * usuário 2). Verificam só o contrato HTTP — em particular, que o cliente do pedido é exposto como
 * {@code clientId} (e não mais {@code userId}).
 */
@SpringBootTest
@AutoConfigureMockMvc
class OrderApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /orders lista os pedidos")
    void shouldListOrders() throws Exception {
        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", greaterThanOrEqualTo(3)));
    }

    @Test
    @DisplayName("GET /orders/{id} devolve o pedido com o campo clientId")
    void shouldFindOrderById() throws Exception {
        mockMvc.perform(get("/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.clientId").value(1))
                .andExpect(jsonPath("$.userId").doesNotExist())
                .andExpect(jsonPath("$.status").value("WAITING_PAYMENT"))
                .andExpect(jsonPath("$.createdAt").value("2020-07-25T13:00:00"));
    }

    @Test
    @DisplayName("GET /orders/{id} inexistente → 404")
    void shouldReturn404WhenOrderDoesNotExist() throws Exception {
        mockMvc.perform(get("/orders/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Order with id 999 not found."));
    }

    @Test
    @DisplayName("GET /users/{id}/orders devolve só os pedidos daquele cliente")
    void shouldListOrdersOfUser() throws Exception {
        mockMvc.perform(get("/users/1/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[*].clientId", everyItem(equalTo(1))));

        mockMvc.perform(get("/users/2/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(2));
    }

    @Test
    @DisplayName("GET /users/{id}/orders de cliente inexistente → 404 (via ACL para o módulo users)")
    void shouldReturn404WhenClientDoesNotExist() throws Exception {
        mockMvc.perform(get("/users/999/orders"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Client with id 999 not found."));
    }

    @Test
    @DisplayName("id não positivo → 400")
    void shouldReturn400WhenIdIsNotPositive() throws Exception {
        mockMvc.perform(get("/orders/0"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(get("/users/0/orders"))
                .andExpect(status().isBadRequest());
    }
}
