package com.indiasekeukenservices.orderservice.presentation.dto.request;

import com.indiasekeukenservices.orderservice.domain.ProductType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InventoryResponse {
    private String productId;
    private boolean isInStock;
    private ProductType productType;

}
