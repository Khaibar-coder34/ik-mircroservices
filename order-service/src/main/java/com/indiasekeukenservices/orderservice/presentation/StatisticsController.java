package com.indiasekeukenservices.orderservice.presentation;

import com.indiasekeukenservices.orderservice.application.StatisticsService;
import com.indiasekeukenservices.orderservice.domain.statistics.OrderStatistics;
import com.indiasekeukenservices.orderservice.domain.statistics.ProductStatistics;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class StatisticsController {
    private final StatisticsService statisticsService;

    @GetMapping("/revenue")
    public ResponseEntity<BigDecimal> getRevenue(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return ResponseEntity.ok(statisticsService.getRevenue(start, end));
    }

    @GetMapping("/order-count")
    public ResponseEntity<Long> getOrderCount(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return ResponseEntity.ok(statisticsService.getOrderCount(start, end));
    }

    @GetMapping("/average-order-value")
    public ResponseEntity<BigDecimal> getAverageOrderValue(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return ResponseEntity.ok(statisticsService.getAverageOrderValue(start, end));
    }

    @GetMapping("/top-selling-products")
    public ResponseEntity<List<ProductStatistics>> getTopSellingProducts(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return ResponseEntity.ok(statisticsService.getTopSellingProducts(start, end));
    }


    @GetMapping("/order-statistics")
    public ResponseEntity<OrderStatistics> getOrderStatistics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return ResponseEntity.ok(statisticsService.getOrderStatistics(start, end));
    }

    @GetMapping("/product-statistics")
    public ResponseEntity<List<ProductStatistics>> getProductStatistics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return ResponseEntity.ok(statisticsService.getProductStatistics(start, end));
    }

    @GetMapping("/peak-order-times")
    public ResponseEntity<Map<String, Long>> getPeakOrderTimes(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return ResponseEntity.ok(statisticsService.getPeakOrderTimes(start, end));
    }
}
