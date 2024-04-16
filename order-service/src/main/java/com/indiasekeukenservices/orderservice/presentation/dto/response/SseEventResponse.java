package com.indiasekeukenservices.orderservice.presentation.dto.response;


import com.indiasekeukenservices.orderservice.presentation.dto.OrderDto;

public class SseEventResponse {
    private String message;
    private OrderDto orderDto;

    // Constructor
    public SseEventResponse(String message, OrderDto order) {
        this.message = message;
        this.orderDto = order;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public OrderDto getOrderDto() {
        return orderDto;
    }

    public void setOrderDto(OrderDto orderDto) {
        this.orderDto = orderDto;
    }
}
