package com.indiasekeukenservices.inventoryservice.events;

import com.indiasekeukenservices.inventoryservice.data.InventoryRepository;
import com.indiasekeukenservices.inventoryservice.domain.Inventory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ProductEventListener {

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

    @KafkaListener(topics = "productUpdatedTopic", groupId = "inventory-group")
    public void onProductUpdated(ProductUpdatedEvent event) {
        log.info("Received product updated event for product ID: " + event.getId());

        Inventory inventory = inventoryRepository.findByProductId(event.getId())
                .orElseThrow(() -> new RuntimeException("Inventory record not found for product ID: " + event.getId()));

        inventory.setName(event.getName());
        inventory.setProductType(event.getProductType());
        inventory.setPrice(event.getPrice());

        inventoryRepository.save(inventory);
        log.info("Inventory record updated for product ID: " + event.getId());
    }

    @KafkaListener(topics = "productDeletedTopic", groupId = "inventory-group")
    public void onProductDeleted(ProductDeletedEvent event) {
        log.info("Received product deleted event for product ID: " + event.getId());

        Inventory inventory = inventoryRepository.findByProductId(event.getId())
                .orElseThrow(() -> new RuntimeException("Inventory record not found for product ID: " + event.getId()));

        inventoryRepository.delete(inventory);
        log.info("Inventory record deleted for product ID: " + event.getId());
    }
}
