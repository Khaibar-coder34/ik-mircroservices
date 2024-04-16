package com.indiasekeukenservices.kitchenservice.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "kitchen_orders")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class KitchenOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String orderNumber;
    private LocalDateTime orderTime;

    @Enumerated(EnumType.STRING)
    private KitchenOrderStatus orderStatus;

    @OneToMany(mappedBy = "kitchenOrder", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<KitchenOrderLineItem> orderLineItemsList;
}
