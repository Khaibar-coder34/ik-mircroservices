package com.indiasekeukenservices.orderservice.presentation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
@RequestMapping("/api/order")
@Slf4j // This annotation is for logging
public class SseController {

    private final CopyOnWriteArrayList<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    @GetMapping("/subscribe")
    public SseEmitter subscribe() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        this.emitters.add(emitter);

        emitter.onCompletion(() -> {
            this.emitters.remove(emitter);
            log.info("SSE connection completed");
        });
        emitter.onTimeout(() -> {
            this.emitters.remove(emitter);
            log.info("SSE connection timed out");
        });
        emitter.onError((e) -> {
            this.emitters.remove(emitter);
            log.error("SSE connection error", e);
        });

        return emitter;
    }

    // Method to dispatch messages including heartbeats
    public void dispatch(Object message) {
        List<SseEmitter> deadEmitters = new ArrayList<>();
        this.emitters.forEach(emitter -> {
            try {
                emitter.send(SseEmitter.event().name("orderUpdate").data(message, MediaType.APPLICATION_JSON));
                log.info("Successfully dispatched message: {}", message);
            } catch (Exception e) {
                deadEmitters.add(emitter);
                log.error("Error dispatching message: {}", e.getMessage(), e);
            }
        });
        this.emitters.removeAll(deadEmitters);
    }

    // Scheduled task for sending heartbeat
    @Scheduled(fixedDelay = 25000) // Every 25 seconds
    public void sendHeartbeat() {
        this.emitters.forEach(emitter -> {
            try {
                emitter.send(SseEmitter.event().name("heartbeat").data("heartbeat", MediaType.TEXT_PLAIN));
                log.info("Heartbeat sent");
            } catch (IOException e) {
                log.error("Error sending heartbeat", e);
            }
        });
    }
}
