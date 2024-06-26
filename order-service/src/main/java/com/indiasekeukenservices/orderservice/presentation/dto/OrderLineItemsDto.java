package com.indiasekeukenservices.orderservice.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderLineItemsDto {
    private Long id;
    private String productId;
    private Integer quantity;
    private String productType;
    private String name;
    private BigDecimal price;

}
