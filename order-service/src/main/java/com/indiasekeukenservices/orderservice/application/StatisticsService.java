package com.indiasekeukenservices.orderservice.application;

import com.indiasekeukenservices.orderservice.data.OrderRepository;
import com.indiasekeukenservices.orderservice.domain.Order;
import com.indiasekeukenservices.orderservice.domain.OrderLineItems;
import com.indiasekeukenservices.orderservice.domain.statistics.OrderStatistics;
import com.indiasekeukenservices.orderservice.domain.statistics.ProductStatistics;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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


    public OrderStatistics getOrderStatistics(LocalDate start, LocalDate end) {
        LocalDateTime startTime = start.atStartOfDay();
        LocalDateTime endTime = end.plusDays(1).atStartOfDay();
        List<Order> orders = orderRepository.findByOrderTimeBetween(startTime, endTime);
        BigDecimal totalRevenue = orders.stream()
                .flatMap(order -> order.getOrderLineItemsList().stream())
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long orderCount = orders.size();
        BigDecimal averageOrderValue = orderCount > 0 ? totalRevenue.divide(BigDecimal.valueOf(orderCount), BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO;

        return new OrderStatistics(start, totalRevenue, orderCount, averageOrderValue);
    }

    public List<ProductStatistics> getProductStatistics(LocalDate start, LocalDate end) {
        LocalDateTime startTime = start.atStartOfDay();
        LocalDateTime endTime = end.plusDays(1).atStartOfDay();
        List<Order> orders = orderRepository.findByOrderTimeBetween(startTime, endTime);
        return orders.stream()
                .flatMap(order -> order.getOrderLineItemsList().stream())
                .collect(Collectors.groupingBy(
                        OrderLineItems::getName,
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())),
                                BigDecimal::add)
                )).entrySet().stream()
                .map(entry -> new ProductStatistics(entry.getKey(), entry.getValue(), countProducts(entry.getKey(), orders)))
                .collect(Collectors.toList());
    }

    private long countProducts(String productName, List<Order> orders) {
        return orders.stream()
                .flatMap(order -> order.getOrderLineItemsList().stream())
                .filter(item -> item.getName().equals(productName))
                .mapToLong(OrderLineItems::getQuantity)
                .sum();
    }

    public Map<String, Long> getPeakOrderTimes(LocalDate start, LocalDate end) {
        LocalDateTime startTime = start.atStartOfDay();
        LocalDateTime endTime = end.plusDays(1).atStartOfDay();
        return orderRepository.findByOrderTimeBetween(startTime, endTime)
                .stream()
                .collect(Collectors.groupingBy(
                        order -> order.getOrderTime().getHour() + ":00", // Grouping orders by hour of the day
                        Collectors.counting() // Counting orders per hour
                ));
    }

}
