package com.indiasekeukenservices.orderservice.domain.statistics;

import lombok.Data;
import lombok.Setter;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.time.LocalDate;

@Setter
@Data
public class OrderStatistics {
    private LocalDate date;
    private BigDecimal totalRevenue;
    private long orderCount;
    private BigDecimal averageOrderValue;

    public OrderStatistics(LocalDate date, BigDecimal totalRevenue, long orderCount, BigDecimal averageOrderValue) {
        Assert.notNull(date, "Date must not be null");
        Assert.notNull(totalRevenue, "Total revenue must not be null");
        Assert.isTrue(orderCount >= 0, "Order count must not be negative");
        Assert.notNull(averageOrderValue, "Average order value must not be null");

        this.date = date;
        this.totalRevenue = totalRevenue;
        this.orderCount = orderCount;
        this.averageOrderValue = averageOrderValue;
    }
}

