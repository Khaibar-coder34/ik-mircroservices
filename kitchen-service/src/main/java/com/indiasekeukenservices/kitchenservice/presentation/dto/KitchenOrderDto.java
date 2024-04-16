package com.indiasekeukenservices.kitchenservice.presentation.dto;

import com.indiasekeukenservices.kitchenservice.domain.KitchenOrder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

// Simplified DTO for KitchenOrder
@Data
public class KitchenOrderDto {
    private Long id;
    private String orderNumber;
    private LocalDateTime orderTime;
    private String orderStatus;
    private List<KitchenOrderLineItemDto> orderLineItemsList;

    public static KitchenOrderDto fromEntity(KitchenOrder order) {
        KitchenOrderDto dto = new KitchenOrderDto();
        dto.setId(order.getId());
        dto.setOrderNumber(order.getOrderNumber());
        dto.setOrderTime(order.getOrderTime());
        dto.setOrderStatus(order.getOrderStatus().toString());
        dto.setOrderLineItemsList(order.getOrderLineItemsList().stream()
                .map(KitchenOrderLineItemDto::fromEntity)
                .collect(Collectors.toList()));
        return dto;
    }
}