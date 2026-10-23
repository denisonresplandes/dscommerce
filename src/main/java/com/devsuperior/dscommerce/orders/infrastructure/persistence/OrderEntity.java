package com.devsuperior.dscommerce.orders.infrastructure.persistence;

import com.devsuperior.dscommerce.orders.domain.OrderStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Modelo de <b>persistência</b> do pedido: um espelho da tabela {@code tb_order}, sem regras de negócio.
 *
 * <p>É deliberadamente diferente do agregado {@code Order}: aqui só há colunas e anotações JPA. O domínio nunca
 * enxerga esta classe, e o {@link OrderMapper} faz a tradução.
 *
 * <p>O atributo chama-se {@code clientId} (vocabulário de pedidos) mas continua mapeado para a coluna
 * {@code user_id}, então o schema e o {@code import.sql} não mudaram.
 */
@Entity
@Table(name = "tb_order")
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long clientId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    OrderEntity() {}

    public OrderEntity(Long id, Long clientId, OrderStatus status,
                       LocalDateTime createdAt) {
        this.id = id;
        this.clientId = clientId;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public Long getClientId() {
        return clientId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OrderEntity other)) {
            return false;
        }
        return id != null && id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
