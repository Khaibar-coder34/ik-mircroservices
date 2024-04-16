package com.indiasekeukenservices.kitchenservice.listener;

import com.indiasekeukenservices.kitchenservice.application.KitchenOrderService;
import com.indiasekeukenservices.kitchenservice.domain.KitchenOrder;
import com.indiasekeukenservices.kitchenservice.domain.KitchenOrderLineItem;
import com.indiasekeukenservices.kitchenservice.domain.KitchenOrderStatus;
import com.indiasekeukenservices.kitchenservice.domain.ProductType;
import com.indiasekeukenservices.kitchenservice.events.OrderPlacedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.KafkaListenerErrorHandler;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class OrderEventListener {

    private final KitchenOrderService kitchenOrderService;

    public OrderEventListener(KitchenOrderService kitchenOrderService) {
        this.kitchenOrderService = kitchenOrderService;
    }


    @KafkaListener(topics = "notificationTopic", groupId = "kitchen-service")
    public void listenOrderPlaced(OrderPlacedEvent event) {
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

        KitchenOrder kitchenOrder = convertToKitchenOrder(event);
        kitchenOrderService.saveOrder(kitchenOrder);
        log.info("Whole recieved order in Kitchen " + event);
    }


    private KitchenOrder convertToKitchenOrder(OrderPlacedEvent event) {
        KitchenOrder kitchenOrder = new KitchenOrder();
        kitchenOrder.setOrderNumber(event.getOrderNumber());
        kitchenOrder.setOrderTime(LocalDateTime.parse(event.getOrderTime(), DateTimeFormatter.ISO_DATE_TIME));
        kitchenOrder.setOrderStatus(KitchenOrderStatus.IN_KITCHEN);
        kitchenOrder.setOrderLineItemsList(convertLineItems(event.getOrderLineItems(), kitchenOrder));

        return kitchenOrder;
    }

    private List<KitchenOrderLineItem> convertLineItems(List<OrderPlacedEvent.OrderLineItemEvent> items, KitchenOrder kitchenOrder) {
        return items.stream().map(item -> {
            KitchenOrderLineItem lineItem = new KitchenOrderLineItem();
            lineItem.setKitchenOrder(kitchenOrder);
            lineItem.setProductId(item.getProductId());
            lineItem.setQuantity(item.getQuantity());
            lineItem.setName(item.getName());
            lineItem.setPrice(new BigDecimal(item.getPrice()));
            lineItem.setProductType(ProductType.valueOf(item.getProductType()));
            return lineItem;
        }).collect(Collectors.toList());
    }

    public KafkaListenerErrorHandler kitchenListenerErrorHandler() {
        return (message, exception) -> {
            log.error("Error handling message in kitchen-service: {}", message, exception);
            // Handle recovery or notification logic
            return null;
        };
    }
}