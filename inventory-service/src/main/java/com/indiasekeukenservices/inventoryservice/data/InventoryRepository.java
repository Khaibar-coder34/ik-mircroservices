package com.indiasekeukenservices.inventoryservice.data;

import com.indiasekeukenservices.inventoryservice.domain.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    List<Inventory> findByProductIdIn(List<String> skuCode);
}
