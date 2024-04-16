package com.indiasekeukenservices.kitchenservice.data;

import com.indiasekeukenservices.kitchenservice.domain.KitchenOrderLineItem;
import com.indiasekeukenservices.kitchenservice.domain.ProductType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface KitchenOrderLineItemRepository extends JpaRepository<KitchenOrderLineItem, Long> {
    List<KitchenOrderLineItem> findByProductType(ProductType productType);


    @Query("SELECT koli FROM KitchenOrderLineItem koli WHERE koli.productType = :productType AND koli.kitchenOrder.orderTime BETWEEN :start AND :end")
    List<KitchenOrderLineItem> findByProductTypeAndOrderTimeBetween(ProductType productType, LocalDateTime start, LocalDateTime end);

}
