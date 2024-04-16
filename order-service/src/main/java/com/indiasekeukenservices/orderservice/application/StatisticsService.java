package com.indiasekeukenservices.orderservice.application;

import com.indiasekeukenservices.orderservice.data.OrderRepository;
import com.indiasekeukenservices.orderservice.domain.Order;
import com.indiasekeukenservices.orderservice.domain.OrderLineItems;
import com.indiasekeukenservices.orderservice.domain.ProductStatistics;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.AbstractMap;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class StatisticsService {
    private final OrderRepository orderRepository;

    public StatisticsService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public BigDecimal getRevenue(LocalDate start, LocalDate end) {
        LocalDateTime startTime = start.atStartOfDay();
        LocalDateTime endTime = end.plusDays(1).atStartOfDay();
        List<Order> orders = orderRepository.findByOrderTimeBetween(startTime, endTime);
        return orders.stream()
                .flatMap(order -> order.getOrderLineItemsList().stream())
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public long getOrderCount(LocalDate start, LocalDate end) {
        LocalDateTime startTime = start.atStartOfDay();
        LocalDateTime endTime = end.plusDays(1).atStartOfDay();
        return orderRepository.countByOrderTimeBetween(startTime, endTime);
    }


    public BigDecimal getAverageOrderValue(LocalDate start, LocalDate end) {
        LocalDateTime startTime = start.atStartOfDay();
        LocalDateTime endTime = end.plusDays(1).atStartOfDay();
        List<Order> orders = orderRepository.findByOrderTimeBetween(startTime, endTime);
        double average = orders.stream()
                .map(order -> order.getOrderLineItemsList().stream()
                        .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                        .reduce(BigDecimal.ZERO, BigDecimal::add))
                .collect(Collectors.averagingDouble(BigDecimal::doubleValue));
        return BigDecimal.valueOf(average).setScale(2, RoundingMode.HALF_UP);
    }


    public List<ProductStatistics> getTopSellingProducts(LocalDate start, LocalDate end) {
        LocalDateTime startTime = start.atStartOfDay();
        LocalDateTime endTime = end.plusDays(1).atStartOfDay();
        List<Order> orders = orderRepository.findByOrderTimeBetween(startTime, endTime);

        return orders.stream()
                .flatMap(order -> order.getOrderLineItemsList().stream())
                .collect(Collectors.groupingBy(
                        OrderLineItems::getProductId,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                items -> new ProductStatistics(
                                        items.get(0).getName(),
                                        items.stream()
                                                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                                                .reduce(BigDecimal.ZERO, BigDecimal::add),
                                        items.stream()
                                                .mapToLong(OrderLineItems::getQuantity)
                                                .sum()
                                )
                        )
                ))
                .values().stream()
                .sorted((a, b) -> Long.compare(b.getCount(), a.getCount()))
                .collect(Collectors.toList());
    }
}
