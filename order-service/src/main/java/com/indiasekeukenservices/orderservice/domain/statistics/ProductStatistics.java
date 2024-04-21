package com.indiasekeukenservices.orderservice.domain.statistics;

import lombok.Data;
import lombok.Setter;
import org.springframework.util.Assert;

import java.math.BigDecimal;

@Setter
@Data
public class ProductStatistics {
    private String name;
    private BigDecimal revenue;
    private long count;

    public ProductStatistics(String name, BigDecimal revenue, long count) {
        Assert.notNull(name, "Product name must not be null");
        Assert.notNull(revenue, "Revenue must not be null");
        Assert.isTrue(count >= 0, "Count must not be negative");

        this.name = name;
        this.revenue = revenue;
        this.count = count;
    }

}
