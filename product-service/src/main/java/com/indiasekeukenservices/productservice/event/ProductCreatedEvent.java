package com.indiasekeukenservices.productservice.event;


import com.indiasekeukenservices.productservice.domain.ProductType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductCreatedEvent {
    private String id;
    private String name;
    private String description;
    private BigDecimal price;
    private ProductType productType;
}

