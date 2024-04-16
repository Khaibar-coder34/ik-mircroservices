package com.indiasekeukenservices.orderservice.presentation.dto;

import com.indiasekeukenservices.orderservice.domain.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {
    private Long id;
    private String orderNumber;
    private LocalDateTime orderTime;
    private OrderStatus orderStatus;
    private List<OrderLineItemsDto> orderLineItemsList;
}
