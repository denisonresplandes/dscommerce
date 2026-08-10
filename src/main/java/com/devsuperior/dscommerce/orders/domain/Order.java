package com.devsuperior.dscommerce.orders.domain;

import com.devsuperior.dscommerce.orders.domain.vo.OrderId;
import com.devsuperior.dscommerce.shared.domain.AggregateRoot;
import com.devsuperior.dscommerce.shared.domain.DomainException;
import com.devsuperior.dscommerce.users.domain.vo.UserId;

import java.time.LocalDateTime;

public class Order extends AggregateRoot {

    private final OrderId id;
    private final UserId userId;
    private final LocalDateTime createdAt;
    private OrderStatus status;

    private Order(OrderId id, UserId userId, OrderStatus status,
                  LocalDateTime createdAt) {
        DomainException.requireNonNull(userId, "user id cannot be null");
        DomainException.requireNonNull(status, "order status cannot be null");
        DomainException.requireNonNull(createdAt, "created date cannot be null");

        this.id = id;
        this.userId = userId;
        this.status = status;
        this.createdAt = createdAt;
    }

    public static Order create(UserId userId) {
        return new Order(
                null,
                userId,
                OrderStatus.WAITING_PAYMENT,
                LocalDateTime.now()
        );
    }

    public static Order restore(OrderId id, UserId userId, OrderStatus status,
                                LocalDateTime createdAt) {
        DomainException.requireNonNull(id,"Order ID cannot be null.");
        return new Order(
                id,
                userId,
                status,
                createdAt
        );
    }

    public OrderId getId() {
        return id;
    }

    public UserId getUserId() {
        return userId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void pay() {
        transitionTo(OrderStatus.PAID);
    }

    public void ship() {
        transitionTo(OrderStatus.SHIPPED);
    }

    public void deliver() {
        transitionTo(OrderStatus.DELIVERED);
    }

    public void cancel() {
        transitionTo(OrderStatus.CANCELED);
    }

    private void transitionTo(OrderStatus nextStatus) {
        if (status == nextStatus) {
            return;
        }
        checkTransitionTo(nextStatus);
        status = nextStatus;
    }

    private void checkTransitionTo(OrderStatus nextStatus) {
        if (!this.status.canTransitionTo(nextStatus)) {
            throw new DomainException("Order with id %s cannot transition from %s to %s"
                    .formatted(
                            id,
                            status,
                            nextStatus
                    )
            );
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Order other)) {
            return false;
        }
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
