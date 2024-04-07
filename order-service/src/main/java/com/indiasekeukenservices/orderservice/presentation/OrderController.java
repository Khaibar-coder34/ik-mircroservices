package com.indiasekeukenservices.orderservice.presentation;

import com.indiasekeukenservices.orderservice.application.OrderService;
import com.indiasekeukenservices.orderservice.presentation.dto.request.OrderRequest;
import com.indiasekeukenservices.orderservice.presentation.dto.response.ApiResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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
}
