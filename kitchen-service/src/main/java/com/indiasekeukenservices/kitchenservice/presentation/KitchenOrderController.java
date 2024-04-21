package com.indiasekeukenservices.kitchenservice.presentation;

import com.indiasekeukenservices.kitchenservice.application.KitchenOrderService;
import com.indiasekeukenservices.kitchenservice.domain.ProductType;
import com.indiasekeukenservices.kitchenservice.presentation.dto.KitchenOrderDto;
import com.indiasekeukenservices.kitchenservice.presentation.dto.KitchenOrderLineItemDto;
import com.indiasekeukenservices.kitchenservice.presentation.dto.KitchenOrderStatusUpdateDto;
import com.indiasekeukenservices.kitchenservice.presentation.dto.PreparationStatusUpdateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/kitchen")
@RequiredArgsConstructor
public class KitchenOrderController {

    private final KitchenOrderService kitchenOrderService;

    @GetMapping("/all")
    public List<KitchenOrderDto> getAllOrders() {
        return kitchenOrderService.getAllOrders().stream()
                .map(KitchenOrderDto::fromEntity)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public KitchenOrderDto getOrderById(@PathVariable Long id) {
        return KitchenOrderDto.fromEntity(kitchenOrderService.getOrderById(id));
    }

    @GetMapping("/line-items/{productType}")
    public List<KitchenOrderLineItemDto> getOrderLineItemsByProductType(@PathVariable ProductType productType) {
        return kitchenOrderService.getOrderLineItemsByProductType(productType).stream()
                .map(KitchenOrderLineItemDto::fromEntity)
                .collect(Collectors.toList());
    }

    @GetMapping("/line-items/day/{productType}")
    public List<KitchenOrderLineItemDto> getOrderLineItemsByProductTypeOfTheDay(@PathVariable ProductType productType) {
        return kitchenOrderService.getOrderLineItemsByProductTypeOfTheDay(productType).stream()
                .map(KitchenOrderLineItemDto::fromEntity)
                .collect(Collectors.toList());
    }

    @GetMapping("/line-items/week/{productType}")
    public List<KitchenOrderLineItemDto> getOrderLineItemsByProductTypeOfTheWeek(@PathVariable ProductType productType) {
        return kitchenOrderService.getOrderLineItemsByProductTypeOfTheWeek(productType).stream()
                .map(KitchenOrderLineItemDto::fromEntity)
                .collect(Collectors.toList());
    }

    @PatchMapping("/line-item/{id}/status")
    public void updateOrderLineItemsByPreparationStatus(@PathVariable Long id, @RequestBody PreparationStatusUpdateDto status) {
        kitchenOrderService.updateOrderLineItemsByPreparationStatus(id, status.getStatus());
    }

    @PatchMapping("/order/{orderId}/status")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long orderId, @RequestBody KitchenOrderStatusUpdateDto statusUpdateDto) {
        try {
            kitchenOrderService.updateOrderStatus(orderId, statusUpdateDto.getStatus());
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while updating the order status.");
        }
    }


    @GetMapping("/orders/day")
    public List<KitchenOrderDto> getOrdersOfTheDay() {
        return kitchenOrderService.getOrdersOfTheDay().stream()
                .map(KitchenOrderDto::fromEntity)
                .collect(Collectors.toList());
    }

    @GetMapping("/orders/week")
    public List<KitchenOrderDto> getOrdersOfTheWeek() {
        return kitchenOrderService.getOrdersOfTheWeek().stream()
                .map(KitchenOrderDto::fromEntity)
                .collect(Collectors.toList());
    }

    @GetMapping("/line-items/day/bread")
    public List<KitchenOrderLineItemDto> getBreadOrderLineItemsOfTheDay() {
        return kitchenOrderService.getOrderLineItemsByProductTypeOfTheDay(ProductType.BREAD).stream()
                .map(KitchenOrderLineItemDto::fromEntity)
                .collect(Collectors.toList());
    }

    @GetMapping("/line-items/day/food")
    public List<KitchenOrderLineItemDto> getFoodOrderLineItemsOfTheDay() {
        return kitchenOrderService.getOrderLineItemsByProductTypeOfTheDay(ProductType.FOOD).stream()
                .map(KitchenOrderLineItemDto::fromEntity)
                .collect(Collectors.toList());
    }

    @GetMapping("/order/{orderId}/line-items/food")
    public List<KitchenOrderLineItemDto> getFoodOrderLineItemsOfOrder(@PathVariable Long orderId) {
        return kitchenOrderService.getOrderById(orderId).getOrderLineItemsList().stream()
                .filter(item -> item.getProductType() == ProductType.FOOD)
                .map(KitchenOrderLineItemDto::fromEntity)
                .collect(Collectors.toList());
    }

    @GetMapping("/order/{orderId}/line-items/bread")
    public List<KitchenOrderLineItemDto> getBreadOrderLineItemsOfOrder(@PathVariable Long orderId) {
        return kitchenOrderService.getOrderById(orderId).getOrderLineItemsList().stream()
                .filter(item -> item.getProductType() == ProductType.BREAD)
                .map(KitchenOrderLineItemDto::fromEntity)
                .collect(Collectors.toList());
    }
}
