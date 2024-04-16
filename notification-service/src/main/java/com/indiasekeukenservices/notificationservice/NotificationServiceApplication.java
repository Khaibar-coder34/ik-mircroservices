package com.indiasekeukenservices.notificationservice;


import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.KafkaListener;

@SpringBootApplication
@Slf4j

public class NotificationServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApplication.class, args);
    }

    @KafkaListener(topics = "notificationTopic")
    public void handleNotification(OrderPlacedEvent event) {
        //todo-->  send out an email notificaiton
        // Logging the detailed order event
        log.info("Received order: Order Number: {}, Order Time: {}, Order Status: {}",
                event.getOrderNumber(), event.getOrderTime(), event.getOrderStatus());

        // If orderLineItems is not null or empty, log its details
        if(event.getOrderLineItems() != null && !event.getOrderLineItems().isEmpty()) {
            event.getOrderLineItems().forEach(item -> {
                log.info("Order Line Item: Product ID: {}, Quantity: {}, Product Type: {}, Name: {}, Price: {}",
                        item.getProductId(), item.getQuantity(), item.getProductType(), item.getName(), item.getPrice());
            });
        }


    }

}