package com.indiasekeukenservices.orderservice.event;

import lombok.*;
import org.springframework.context.ApplicationEvent;

import java.time.Clock;

//@Data
//@AllArgsConstructor
//@NoArgsConstructor
@Getter
@Setter
public class OrderPlacedEvent extends ApplicationEvent {
    private String orderNumber;

    public OrderPlacedEvent(Object source, String orderNumber) {
        super(source);
        this.orderNumber = orderNumber;
    }

    public OrderPlacedEvent(String orderNumber) {
        super(orderNumber);
        this.orderNumber = orderNumber;
    }
}
