package com.indiasekeukenservices.kitchenservice.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "kitchen_order_line_items")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class KitchenOrderLineItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kitchen_order_id")
    private KitchenOrder kitchenOrder;

    private String productId;
    private Integer quantity;
    private String name;
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    private ProductType productType;

    @Enumerated(EnumType.STRING)
    private PreparationStatus preparationStatus;
}
