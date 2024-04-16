package com.indiasekeukenservices.orderservice.presentation.dto.request;

import com.indiasekeukenservices.orderservice.domain.OrderStatus;

public class OrderStatusUpadatRequest {
    private OrderStatus status;

    // Getter and setter
    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}
