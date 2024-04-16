package com.indiasekeukenservices.kitchenservice.data;

import com.indiasekeukenservices.kitchenservice.domain.KitchenOrder;
import com.indiasekeukenservices.kitchenservice.domain.KitchenOrderLineItem;
import com.indiasekeukenservices.kitchenservice.domain.KitchenOrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

//@Repository
public interface KitchenOrderRepository extends JpaRepository<KitchenOrder, Long> {
//    List<KitchenOrder> findByStatus(KitchenOrderStatus status);
    List<KitchenOrder> findByOrderTimeBetween(LocalDateTime startOfDay, LocalDateTime now);
}
