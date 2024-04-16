package com.indiasekeukenservices.orderservice.application;

import com.indiasekeukenservices.orderservice.data.OrderRepository;
import com.indiasekeukenservices.orderservice.domain.Order;
import com.indiasekeukenservices.orderservice.domain.OrderLineItems;
import com.indiasekeukenservices.orderservice.domain.OrderStatus;
import com.indiasekeukenservices.orderservice.event.OrderPlacedEvent;
import com.indiasekeukenservices.orderservice.presentation.SseController;
import com.indiasekeukenservices.orderservice.presentation.dto.OrderDto;
import com.indiasekeukenservices.orderservice.presentation.dto.OrderLineItemsDto;
import com.indiasekeukenservices.orderservice.presentation.dto.request.InventoryResponse;
import com.indiasekeukenservices.orderservice.presentation.dto.request.OrderRequest;
import com.indiasekeukenservices.orderservice.presentation.dto.response.SseEventResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;
    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;
    private final SseController sseController;

    public OrderService(OrderRepository orderRepository, WebClient.Builder webClientBuilder, KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate, SseController sseController) {
        this.orderRepository = orderRepository;
        this.webClientBuilder = webClientBuilder;
        this.kafkaTemplate = kafkaTemplate;
        this.sseController = sseController;
    }

    public String placeOrder(OrderRequest orderRequest) {
        Order order = createOrder(orderRequest);
        List<InventoryResponse> inventoryResponses = checkInventoryAndCategorizeProducts(order);

        // Check if all products are in stock
        if (!inventoryResponses.stream().allMatch(InventoryResponse::isInStock)) {
            throw new IllegalArgumentException("Product is not in stock, please try again later");
        }

        orderRepository.save(order);
        publishOrderPlacedEvent(order);
        dispatchOrderPlacedSse(order);

        return "Order placed successfully!!!";
    }

    private Order createOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        order.setOrderLineItemsList(orderRequest.getOrderLineItemsList()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList()));
        order.setOrderTime(LocalDateTime.now());
        order.setOrderStatus(OrderStatus.IN_KITCHEN);
        return order;
    }

    private List<InventoryResponse> checkInventoryAndCategorizeProducts(Order order) {
        List<InventoryResponse> inventoryResponses = List.of(callInventoryService(order));

        inventoryResponses.forEach(response -> {
            OrderLineItems orderLineItem = findOrderLineItem(order, response.getProductId());
            updateOrderLineItem(orderLineItem, response);
        });

        return inventoryResponses;
    }

    private InventoryResponse[] callInventoryService(Order order) {
        List<String> productIds = extractProductIds(order);
        return webClientBuilder.build().get()
                .uri("http://inventory-service/api/inventory", uriBuilder -> uriBuilder.queryParam("productIds", productIds).build())
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .block();
    }

    private List<String> extractProductIds(Order order) {
        return order.getOrderLineItemsList()
                .stream()
                .map(OrderLineItems::getProductId)
                .collect(Collectors.toList());
    }

    private OrderLineItems findOrderLineItem(Order order, String productId) {
        return order.getOrderLineItemsList().stream()
                .filter(oli -> oli.getProductId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Matching product not found in order"));
    }

    private void updateOrderLineItem(OrderLineItems orderLineItem, InventoryResponse response) {
        orderLineItem.setName(response.getName());
        orderLineItem.setPrice(response.getPrice());
        orderLineItem.setProductType(response.getProductType().name());
    }

    public void publishOrderPlacedEvent(Order order) {
        OrderPlacedEvent event = new OrderPlacedEvent();
        event.setOrderNumber(order.getOrderNumber());
        event.setOrderTime(order.getOrderTime().toString()); // Convert LocalDateTime to String
        event.setOrderStatus(order.getOrderStatus().toString());

        List<OrderPlacedEvent.OrderLineItemEvent> lineItems = order.getOrderLineItemsList().stream()
                .map(item -> new OrderPlacedEvent.OrderLineItemEvent(
                        item.getProductId(),
                        item.getQuantity(),
                        item.getProductType(),
                        item.getName(),
                        item.getPrice().toString() // Convert BigDecimal to String
                )).collect(Collectors.toList());

        event.setOrderLineItems(lineItems);

        // Use KafkaTemplate to send the event
        kafkaTemplate.send("notificationTopic", event);
        log.info("Order sended to NotifcationService and KitchenService " + event);
    }


    private void dispatchOrderPlacedSse(Order order) {
        sseController.dispatch(new SseEventResponse("New Order placed successfully in SSE!", convertToDto(order)));
        log.info("SSE event data: " + order);
    }

    public List<OrderDto> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public OrderDto getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with id: " + id));
        return convertToDto(order);
    }

    //BELOW CODE SHOULD BE DELETED
    public List<OrderLineItemsDto> getFoodProductsForOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order  not found"));
        return order.getOrderLineItemsList().stream()
                .filter(item -> "FOOD".equals(item.getProductType()))
                .map(this::convertToOrderLineItemsDto)
                .collect(Collectors.toList());
    }

    public List<OrderLineItemsDto> getBreadProductsForOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order  not found"));
        return order.getOrderLineItemsList().stream()
                .filter(item -> "BREAD".equals(item.getProductType()))
                .map(this::convertToOrderLineItemsDto)
                .collect(Collectors.toList());
    }

    public List<OrderDto> getOrdersByTimeFrame(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return orderRepository.findByOrderTimeBetween(startDateTime, endDateTime).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }


    public List<OrderLineItemsDto> getFoodProductsOfOrdersByTimeFrame(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return orderRepository.findByOrderTimeBetween(startDateTime, endDateTime).stream()
                .flatMap(order -> order.getOrderLineItemsList().stream())
                .filter(item -> "FOOD".equals(item.getProductType()))
                .map(this::convertToOrderLineItemsDto)
                .collect(Collectors.toList());
    }

    public List<OrderLineItemsDto> getBreadProductsOfOrdersByTimeFrame(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return orderRepository.findByOrderTimeBetween(startDateTime, endDateTime).stream()
                .flatMap(order -> order.getOrderLineItemsList().stream())
                .filter(item -> "BREAD".equals(item.getProductType()))
                .map(this::convertToOrderLineItemsDto)
                .collect(Collectors.toList());
    }


    public OrderDto updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with id: " + orderId));
        order.setOrderStatus(status);
        Order updatedOrder = orderRepository.save(order);
        return convertToDto(updatedOrder);
    }

    //ABOVE CODE SHOULD BE DELETED

    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItems.setProductId(orderLineItemsDto.getProductId());
        orderLineItems.setProductType(orderLineItemsDto.getProductType());
        return orderLineItems;
    }

    private OrderLineItemsDto convertToOrderLineItemsDto(OrderLineItems item) {
        return new OrderLineItemsDto(item.getId(), item.getProductId(), item.getQuantity(), item.getProductType(), item.getName(), item.getPrice());
    }


    private OrderDto convertToDto(Order order) {
        List<OrderLineItemsDto> lineItemsDtos = order.getOrderLineItemsList().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return new OrderDto(order.getId(),
                            order.getOrderNumber(),
                            order.getOrderTime(),
                            order.getOrderStatus(),
                            lineItemsDtos);
    }

    private OrderLineItemsDto convertToDto(OrderLineItems item) {
        return new OrderLineItemsDto(item.getId(), item.getProductId(), item.getQuantity(), item.getProductType(), item.getName(), item.getPrice());
    }

}