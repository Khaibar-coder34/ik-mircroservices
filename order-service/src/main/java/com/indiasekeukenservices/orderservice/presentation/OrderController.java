package com.indiasekeukenservices.orderservice.presentation;

import com.indiasekeukenservices.orderservice.application.OrderService;
import com.indiasekeukenservices.orderservice.domain.OrderStatus;
import com.indiasekeukenservices.orderservice.presentation.dto.OrderDto;
import com.indiasekeukenservices.orderservice.presentation.dto.OrderLineItemsDto;
import com.indiasekeukenservices.orderservice.presentation.dto.request.OrderRequest;
import com.indiasekeukenservices.orderservice.presentation.dto.request.OrderStatusUpadatRequest;
import com.indiasekeukenservices.orderservice.presentation.dto.response.ApiResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @CircuitBreaker(name="inventory", fallbackMethod = "fallbackMethod")
    @TimeLimiter(name="inventory")
    @Retry(name = "inventory")
    public CompletableFuture<ApiResponse> placeOrder(@RequestBody OrderRequest orderRequest) {
        return CompletableFuture.supplyAsync(() -> new ApiResponse(orderService.placeOrder(orderRequest)));
    }

    public CompletableFuture<ApiResponse> fallbackMethod(RuntimeException runtimeException){
        return CompletableFuture.supplyAsync(() -> new ApiResponse("Oops! Something went wrong, please try again later!"));
    }

    @GetMapping("/all")
    public List<OrderDto> getAllOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/{id}")
    public OrderDto getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id);
    }

    //BELOW CODE SHOULD BE DELETED

    @GetMapping("/{orderId}/food-products")
    public List<OrderLineItemsDto> getFoodProducts(@PathVariable Long orderId) {
        return orderService.getFoodProductsForOrder(orderId);
    }

    @GetMapping("/{orderId}/bread-products")
    public List<OrderLineItemsDto> getBreadProducts(@PathVariable Long orderId) {
        return orderService.getBreadProductsForOrder(orderId);
    }



    @GetMapping("/day")
    public List<OrderDto> getOrdersOfDay() {
        LocalDateTime startOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT);
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        return orderService.getOrdersByTimeFrame(startOfDay, endOfDay);
    }

    @GetMapping("/week")
    public List<OrderDto> getOrdersOfWeek() {
        LocalDateTime startOfWeek = LocalDateTime.now().with(LocalTime.MIN).with(ChronoUnit.DAYS.addTo(LocalDate.now(), -(LocalDate.now().getDayOfWeek().getValue() - 1)));
        LocalDateTime endOfWeek = startOfWeek.plusWeeks(1);
        return orderService.getOrdersByTimeFrame(startOfWeek, endOfWeek);
    }

    @GetMapping("/month")
    public List<OrderDto> getOrdersOfMonth() {
        LocalDateTime startOfMonth = LocalDateTime.now().with(LocalTime.MIN).withDayOfMonth(1);
        LocalDateTime endOfMonth = startOfMonth.plusMonths(1);
        return orderService.getOrdersByTimeFrame(startOfMonth, endOfMonth);
    }


    @GetMapping("/orders/food/day")
    public ResponseEntity<List<OrderLineItemsDto>> getFoodProductsOfOrdersOfDay() {
        LocalDateTime startDateTime = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endDateTime = startDateTime.plusDays(1);
        return ResponseEntity.ok(orderService.getFoodProductsOfOrdersByTimeFrame(startDateTime, endDateTime));
    }

    @GetMapping("/orders/food/week")
    public ResponseEntity<List<OrderLineItemsDto>> getFoodProductsOfOrdersOfWeek() {
        LocalDateTime startDateTime = LocalDateTime.now().with(DayOfWeek.MONDAY).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endDateTime = startDateTime.plusWeeks(1);
        return ResponseEntity.ok(orderService.getFoodProductsOfOrdersByTimeFrame(startDateTime, endDateTime));
    }

    @GetMapping("/orders/food/month")
    public ResponseEntity<List<OrderLineItemsDto>> getFoodProductsOfOrdersOfMonth() {
        LocalDateTime startDateTime = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endDateTime = startDateTime.plusMonths(1);
        return ResponseEntity.ok(orderService.getFoodProductsOfOrdersByTimeFrame(startDateTime, endDateTime));
    }

    @GetMapping("/orders/bread/day")
    public ResponseEntity<List<OrderLineItemsDto>> getBreadProductsOfOrdersOfDay() {
        LocalDateTime startDateTime = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endDateTime = startDateTime.plusDays(1);
        return ResponseEntity.ok(orderService.getBreadProductsOfOrdersByTimeFrame(startDateTime, endDateTime));
    }

    @GetMapping("/orders/bread/week")
    public ResponseEntity<List<OrderLineItemsDto>> getBreadProductsOfOrdersOfWeek() {
        LocalDateTime startDateTime = LocalDateTime.now().with(DayOfWeek.MONDAY).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endDateTime = startDateTime.plusWeeks(1);
        return ResponseEntity.ok(orderService.getBreadProductsOfOrdersByTimeFrame(startDateTime, endDateTime));
    }

    @GetMapping("/orders/bread/month")
    public ResponseEntity<List<OrderLineItemsDto>> getBreadProductsOfOrdersOfMonth() {
        LocalDateTime startDateTime = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endDateTime = startDateTime.plusMonths(1);
        return ResponseEntity.ok(orderService.getBreadProductsOfOrdersByTimeFrame(startDateTime, endDateTime));
    }
    @PatchMapping("/{orderId}/status")
    public OrderDto updateOrderStatus(@PathVariable Long orderId, @RequestBody OrderStatusUpadatRequest updateStatus) {
        return orderService.updateOrderStatus(orderId, updateStatus.getStatus());
    }

    //ABOVE CODE SHOULD BE DELETED

}
