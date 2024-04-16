package com.indiasekeukenservices.kitchenservice.application;

import com.indiasekeukenservices.kitchenservice.data.KitchenOrderLineItemRepository;
import com.indiasekeukenservices.kitchenservice.data.KitchenOrderRepository;
import com.indiasekeukenservices.kitchenservice.domain.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
//@RequiredArgsConstructor
@Transactional
@Slf4j
public class KitchenOrderService {
    private final KitchenOrderRepository kitchenOrderRepository;
    private final KitchenOrderLineItemRepository kitchenOrderLineItemRepository;

    public KitchenOrderService(KitchenOrderRepository kitchenOrderRepository, KitchenOrderLineItemRepository kitchenOrderLineItemRepository) {
        this.kitchenOrderRepository = kitchenOrderRepository;
        this.kitchenOrderLineItemRepository = kitchenOrderLineItemRepository;
    }

    public KitchenOrder saveOrder(KitchenOrder order) {
        order.setOrderStatus(KitchenOrderStatus.IN_KITCHEN);
        processOrderItems(order);
        return kitchenOrderRepository.save(order);
    }

    private void processOrderItems(KitchenOrder order) {
        order.getOrderLineItemsList().forEach(item -> item.setPreparationStatus(PreparationStatus.PENDING));  // Set initial status to PENDING
        List<KitchenOrderLineItem> foodItems = filterItemsByType(order, ProductType.FOOD);
        List<KitchenOrderLineItem> breadItems = filterItemsByType(order, ProductType.BREAD);
        sendToPreparationArea(foodItems, KitchenArea.FOOD_PREPARATION_AREA);
        sendToPreparationArea(breadItems, KitchenArea.BREAD_PREPARATION_AREA);
    }

    private List<KitchenOrderLineItem> filterItemsByType(KitchenOrder order, ProductType type) {
        return order.getOrderLineItemsList().stream()
                .filter(item -> item.getProductType() == type)
                .collect(Collectors.toList());
    }

    private void sendToPreparationArea(List<KitchenOrderLineItem> items, KitchenArea kitchenArea) {
        // Implement logic to handle order items in their respective preparation areas
        // This might involve updating the order status or notifying kitchen staff
        items.forEach(item -> {
            // You can add more detailed logic here depending on the preparation steps
            log.info("Sending item " + item.getName() + " to " + kitchenArea);
        });

        updateOrderStatus(items, KitchenOrderStatus.IN_KITCHEN);

    }

    private void updateOrderStatus(List<KitchenOrderLineItem> items, KitchenOrderStatus status) {
        items.forEach(item -> item.getKitchenOrder().setOrderStatus(status));
    }

    public KitchenOrder getOrderById(Long id) {
        return kitchenOrderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with id: " + id));
    }


    public List<KitchenOrderLineItem> getOrderLineItemsByProductType(ProductType productType) {
        return kitchenOrderLineItemRepository.findByProductType(productType);
    }

    public List<KitchenOrderLineItem> getOrderLineItemsByProductTypeOfTheDay(ProductType productType) {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        return kitchenOrderLineItemRepository.findByProductTypeAndOrderTimeBetween(productType, startOfDay, endOfDay);
    }

    public List<KitchenOrderLineItem> getOrderLineItemsByProductTypeOfTheWeek(ProductType productType) {
        LocalDateTime startOfWeek = LocalDate.now().atStartOfDay().with(LocalTime.MIN).minusDays(LocalDate.now().getDayOfWeek().getValue() - 1);
        LocalDateTime endOfWeek = startOfWeek.plusWeeks(1);
        return kitchenOrderLineItemRepository.findByProductTypeAndOrderTimeBetween(productType, startOfWeek, endOfWeek);
    }

    public void updateOrderLineItemsByPreparationStatus(Long lineItemId, PreparationStatus status) {
        KitchenOrderLineItem lineItem = kitchenOrderLineItemRepository.findById(lineItemId)
                .orElseThrow(() -> new IllegalStateException("Line item not found"));
        lineItem.setPreparationStatus(status);
        kitchenOrderLineItemRepository.save(lineItem);
    }

    public List<KitchenOrder> getOrdersOfTheDay() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        return kitchenOrderRepository.findByOrderTimeBetween(startOfDay, endOfDay);
    }

    public List<KitchenOrder> getOrdersOfTheWeek() {
        LocalDateTime startOfWeek = LocalDate.now().atStartOfDay().minusDays(LocalDate.now().getDayOfWeek().getValue() - 1);
        LocalDateTime endOfWeek = startOfWeek.plusWeeks(1);
        return kitchenOrderRepository.findByOrderTimeBetween(startOfWeek, endOfWeek);
    }

    public List<KitchenOrder> getAllOrders() {
        return kitchenOrderRepository.findAll();
    }

}
