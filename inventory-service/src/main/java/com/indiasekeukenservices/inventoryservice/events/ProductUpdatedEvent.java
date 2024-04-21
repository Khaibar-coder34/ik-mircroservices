package com.indiasekeukenservices.inventoryservice.events;

import com.indiasekeukenservices.inventoryservice.domain.ProductType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductUpdatedEvent {
    private String id;
    private String name;
    private String description;
    private BigDecimal price;
    private ProductType productType;
}
