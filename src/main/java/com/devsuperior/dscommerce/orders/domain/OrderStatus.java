package com.devsuperior.dscommerce.orders.domain;

import java.util.EnumSet;

public enum OrderStatus {

    WAITING_PAYMENT {
        @Override
        public boolean canTransitionTo(OrderStatus next) {
            return EnumSet.of(PAID, CANCELED).contains(next);
        }
    },
    PAID {
        @Override
        public boolean canTransitionTo(OrderStatus next) {
            return EnumSet.of(SHIPPED, CANCELED).contains(next);
        }
    },
    SHIPPED {
        @Override
        public boolean canTransitionTo(OrderStatus next) {
            return next == DELIVERED;
        }
    },
    DELIVERED {
        @Override
        public boolean canTransitionTo(OrderStatus next) {
            return false;
        }
    },
    CANCELED {
        @Override
        public boolean canTransitionTo(OrderStatus next) {
            return false;
        }
    };

    public abstract boolean canTransitionTo(OrderStatus next);
}
