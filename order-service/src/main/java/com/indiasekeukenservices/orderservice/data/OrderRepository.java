package com.indiasekeukenservices.orderservice.data;

import com.indiasekeukenservices.orderservice.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
