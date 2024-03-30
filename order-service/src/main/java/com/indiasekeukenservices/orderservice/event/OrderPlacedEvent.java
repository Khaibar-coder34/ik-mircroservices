package com.indiasekeukenservices.orderservice.event;

import lombok.*;
import org.springframework.context.ApplicationEvent;

import java.time.Clock;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class OrderPlacedEvent {
    private String orderNumber;
}
