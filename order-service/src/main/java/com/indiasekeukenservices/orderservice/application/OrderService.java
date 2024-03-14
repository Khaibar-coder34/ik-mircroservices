package com.indiasekeukenservices.orderservice.application;

import com.indiasekeukenservices.orderservice.data.OrderRepository;
import com.indiasekeukenservices.orderservice.domain.Order;
import com.indiasekeukenservices.orderservice.domain.OrderLineItems;
import com.indiasekeukenservices.orderservice.event.OrderPlacedEvent;
import com.indiasekeukenservices.orderservice.presentation.dto.OrderLineItemsDto;
import com.indiasekeukenservices.orderservice.presentation.dto.request.InventoryResponse;
import com.indiasekeukenservices.orderservice.presentation.dto.request.OrderRequest;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
//    private final WebClient.Builder webClientBuilder;
    private final Tracer tracer;
    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    public String placeOrder(OrderRequest orderRequest) {
        Order order = buildOrderFromRequest(orderRequest);

        //todo: Zorg ervoor dat alle producten die worden besteld op voorraad zijn.
        orderRepository.save(order);

        // Publiceer een event dat de bestelling is geplaatst
        publishOrderPlacedEvent(order.getOrderNumber());

        return "Order placed successfully!";
    }

    private Order buildOrderFromRequest(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsList().stream()
                .map(this::mapToDto)
                .toList();

        order.setOrderLineItemsList(orderLineItems);
        return order;
    }

    private void publishOrderPlacedEvent(String orderNumber) {
        kafkaTemplate.send("notificationTopic", new OrderPlacedEvent(orderNumber));
    }


//    Deze methode communiceert synchronishc met de Inventory service en IK WIL HET NU NIET GEBRUIKEN

//    public String placeOrder(OrderRequest orderRequest) {
//        Order order = new Order();
//        order.setOrderNumber(UUID.randomUUID().toString());
//
//        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsList()
//                .stream()
//                .map(this::mapToDto)
//                .toList();
//
//        order.setOrderLineItemsList(orderLineItems);
//
//        List<String> productIds = order.getOrderLineItemsList().stream()
//                .map(OrderLineItems::getProductId)
//                .toList();
//
//        Span inventoryServiceLookup = tracer.nextSpan().name("InventoryServiceLookup");
//
//        try (Tracer.SpanInScope spanInScope = tracer.withSpan(inventoryServiceLookup.start())){
//
//            // Call Inventory Service, and place order if product is in stock
//            InventoryResponse[] inventoryResponsArray = webClientBuilder.build().get()
//                    .uri("http://inventory-service/api/inventory",
//                            uriBuilder -> uriBuilder.queryParam("productIds", productIds).build())
//                    .retrieve()
//                    .bodyToMono(InventoryResponse[].class)
//                    .block();
//
//            boolean allProductsInStock = Arrays.stream(inventoryResponsArray)
//                    .allMatch(InventoryResponse::isInStock);
//
//            if(allProductsInStock){
//                orderRepository.save(order);
//                kafkaTemplate.send("notificationTopic", new OrderPlacedEvent(order.getOrderNumber()));
//                return "Order placed succesfully!!!";
//            } else {
//                throw new IllegalArgumentException("Product is not in stock, please try again later");
//            }
//
//        } finally {
//            inventoryServiceLookup.end();
//        }
//
//    }

    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItems.setProductId(orderLineItemsDto.getProductId());
        return orderLineItems;
    }
}