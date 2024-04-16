package com.indiasekeukenservices.inventoryservice.events;

import com.indiasekeukenservices.inventoryservice.data.InventoryRepository;
import com.indiasekeukenservices.inventoryservice.domain.Inventory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ProductEventListener {

    // Eventueel andere benodigde afhankelijkheden injecteren
    private final InventoryRepository inventoryRepository;

    public ProductEventListener(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @KafkaListener(topics = "productCreatedTopic", groupId = "inventory-group")
    public void onProductCreated(ProductCreatedEvent event) {
        log.info("Received product created event for product ID: " + event.getId());

        // Maak een nieuw voorraadrecord voor het product
        Inventory inventory = new Inventory();
        inventory.setProductId(event.getId());
        inventory.setInStock(true);
        inventory.setProductType(event.getProductType());
        inventory.setName(event.getName());
        inventory.setPrice(event.getPrice());

        inventoryRepository.save(inventory);
        log.info("PRODUCT SAVED in Inventory record for product ID: " + event.getId());
    }
}
