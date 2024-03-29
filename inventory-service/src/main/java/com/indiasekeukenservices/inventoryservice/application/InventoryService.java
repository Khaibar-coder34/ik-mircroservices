package com.indiasekeukenservices.inventoryservice.application;

import com.indiasekeukenservices.inventoryservice.data.InventoryRepository;
import com.indiasekeukenservices.inventoryservice.presentation.dto.response.InventoryResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    @Transactional(readOnly = true)
    public List<InventoryResponse> isInStock(List<String> skuCode) {
        return inventoryRepository.findByProductIdIn(skuCode).stream()
                .map(inventory ->
                        InventoryResponse.builder()
                                .productId(inventory.getProductId())
                                .isInStock(inventory.getInStock())
                                .productType(inventory.getProductType())
                                .build()
                ).toList();
    }
}