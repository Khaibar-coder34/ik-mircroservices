package com.indiasekeukenservices.orderservice.application;

import com.indiasekeukenservices.orderservice.data.OrderRepository;
import com.indiasekeukenservices.orderservice.domain.Order;
import com.indiasekeukenservices.orderservice.domain.OrderLineItems;
import com.indiasekeukenservices.orderservice.domain.ProductType;
import com.indiasekeukenservices.orderservice.event.OrderPlacedEvent;
import com.indiasekeukenservices.orderservice.presentation.dto.OrderLineItemsDto;
import com.indiasekeukenservices.orderservice.presentation.dto.request.InventoryResponse;
import com.indiasekeukenservices.orderservice.presentation.dto.request.OrderRequest;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;
    private final Tracer tracer;
    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    public String placeOrder(OrderRequest orderRequest) {
        Order order = createOrder(orderRequest);
        List<InventoryResponse> foodProducts = new ArrayList<>();
        List<InventoryResponse> breadProducts = new ArrayList<>();

        // Separate food and bread products
        separateProducts(order, foodProducts, breadProducts);

        // Check if all products are in stock
        validateInventoryStatus(foodProducts, breadProducts);

        // Place the order
        placeOrderAndSendToPreparationAreas(order, foodProducts, breadProducts);

        return "Order placed successfully!!!";
    }

    private Order createOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        order.setOrderLineItemsList(orderRequest.getOrderLineItemsList()
                .stream()
                .map(this::mapToDto)
                .toList());
        log.info("Created order with orderNumber: {}", order.getOrderNumber());
        return order;
    }

    private void separateProducts(Order order, List<InventoryResponse> foodProducts, List<InventoryResponse> breadProducts) {
        InventoryResponse[] inventoryResponses = callInventoryService(order);
        for (InventoryResponse response : inventoryResponses) {
            if (response.getProductType() == ProductType.FOOD) {
                foodProducts.add(response);
                log.info("Food products: {} ", response.getProductType());

            } else if (response.getProductType() == ProductType.BREAD) {
                breadProducts.add(response);
                log.info("Bread products: {} ", response.getProductType());
            }
        }
        log.info("Separated products: {} food products and {} bread products", foodProducts.size(), breadProducts.size());
    }

    private InventoryResponse[] callInventoryService(Order order) {
        List<String> productIds = order.getOrderLineItemsList()
                .stream()
                .map(OrderLineItems::getProductId)
                .toList();
        return webClientBuilder.build().get()
                .uri("http://inventory-service/api/inventory",
                        uriBuilder -> uriBuilder.queryParam("productIds", productIds).build())
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .block();
    }

    private void validateInventoryStatus(List<InventoryResponse> foodProducts, List<InventoryResponse> breadProducts) {
        if (!foodProducts.stream().allMatch(InventoryResponse::isInStock) || !breadProducts.stream().allMatch(InventoryResponse::isInStock)) {
            throw new IllegalArgumentException("Product is not in stock, please try again later");
        }
        log.info("All products are in stock");
    }

    private void placeOrderAndSendToPreparationAreas(Order order, List<InventoryResponse> foodProducts, List<InventoryResponse> breadProducts) {
        orderRepository.save(order);

        // Publiceer een event dat de bestelling is geplaatst
        publishOrderPlacedEvent(order.getOrderNumber());
        log.info("Order placed with orderNumber: {}", order.getOrderNumber());

        sendProductsToRespectiveAreas(foodProducts, breadProducts);
    }

    private void publishOrderPlacedEvent(String orderNumber) {
        kafkaTemplate.send("notificationTopic", new OrderPlacedEvent(orderNumber));
    }

    private void sendProductsToRespectiveAreas(List<InventoryResponse> foodProducts, List<InventoryResponse> breadProducts) {
        sendProductsToPreparationArea(foodProducts, "food preparation area");
        sendProductsToPreparationArea(breadProducts, "bread preparation area");
    }

    private void sendProductsToPreparationArea(List<InventoryResponse> products, String preparationArea) {
        for (InventoryResponse product : products) {
            log.info("Sending product {} to {}", product.getProductId(), preparationArea);
            // Implement logic to send product to the preparation area
        }
    }

    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItems.setProductId(orderLineItemsDto.getProductId());
        return orderLineItems;
    }
}