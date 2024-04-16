package com.indiasekeukenservices.orderservice.data;

import com.indiasekeukenservices.orderservice.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByOrderTimeBetween(LocalDateTime startDateTime,LocalDateTime endDateTime);
}
