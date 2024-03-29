package com.indiasekeukenservices.inventoryservice.presentation.dto.response;

import com.indiasekeukenservices.inventoryservice.domain.ProductType;
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
