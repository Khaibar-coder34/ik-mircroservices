package com.indiasekeukenservices.kitchenservice.presentation.dto;

import com.indiasekeukenservices.kitchenservice.domain.KitchenOrderLineItem;
import com.indiasekeukenservices.kitchenservice.domain.PreparationStatus;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class KitchenOrderLineItemDto {
    private String orderNumber;
    private Long id;
    private String productId;
    private Integer quantity;
    private String productType;
    private String name;
    private BigDecimal price;
    private String preparationStatus;



    public static KitchenOrderLineItemDto fromEntity(KitchenOrderLineItem lineItem) {
        KitchenOrderLineItemDto dto = new KitchenOrderLineItemDto();
        dto.setOrderNumber(lineItem.getKitchenOrder().getOrderNumber()); // Set the orderId from the KitchenOrder
        dto.setId(lineItem.getId());
        dto.setProductId(lineItem.getProductId());
        dto.setQuantity(lineItem.getQuantity());
        dto.setName(lineItem.getName());
        dto.setPrice(lineItem.getPrice());
        dto.setProductType(lineItem.getProductType().name());
        dto.setPreparationStatus(lineItem.getPreparationStatus().name());
        return dto;
    }
}
