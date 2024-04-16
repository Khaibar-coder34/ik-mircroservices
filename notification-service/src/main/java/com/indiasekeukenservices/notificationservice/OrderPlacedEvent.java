package com.indiasekeukenservices.notificationservice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class OrderPlacedEvent {
    private String orderNumber;
    private String orderTime; // Consider using ISO-8601 string representation
    private String orderStatus;
    private List<OrderLineItemEvent> orderLineItems;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OrderLineItemEvent {
        private String productId;
        private Integer quantity;
        private String productType;
        private String name;
        private String price; // To simplify serialization, consider using String representation
    }
}

