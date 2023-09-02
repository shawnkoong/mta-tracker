package com.nyctransittracker.mainapp.controller;

import com.nyctransittracker.mainapp.dto.SubscriptionRequest;
import com.nyctransittracker.mainapp.dto.SubscriptionResponse;
import com.nyctransittracker.mainapp.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/subscription")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService service;

    @PostMapping("/subscribe")
    public ResponseEntity<SubscriptionResponse> subscribe(@RequestBody SubscriptionRequest request) {
        if (request.getUserId() != null && request.getRouteId() != null) {
            service.subscribe(request.getUserId(), request.getRouteId());
            return ResponseEntity
                    .ok(SubscriptionResponse.builder()
                            .message("Successfully subscribed.")
                            .build());
        } else {
            return ResponseEntity
                    .badRequest().body(SubscriptionResponse.builder()
                            .message("Invalid subscription request.")
                            .build());
        }
    }

    @PutMapping("/unsubscribe")
    public ResponseEntity<SubscriptionResponse> unsubscribe(@RequestBody SubscriptionRequest request) {
        if (request.getUserId() != null && request.getRouteId() != null) {
            service.unsubscribe(request.getUserId(), request.getRouteId());
            return ResponseEntity
                    .ok(SubscriptionResponse.builder()
                            .message("Successfully unsubscribed.")
                            .build());
        } else {
            return ResponseEntity
                    .badRequest().body(SubscriptionResponse.builder()
                            .message("Invalid subscription request.")
                            .build());
        }
    }
}
