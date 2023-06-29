package com.nyctransittracker.mainapp.service;

import com.nyctransittracker.mainapp.dto.SubscriptionRequest;
import com.nyctransittracker.mainapp.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final RouteSubscriptionService routeSubscriptionService;
    private final UserService userService;

    public void subscribe(SubscriptionRequest request) {
        User user = userService.getUser(request.getUserId());
        routeSubscriptionService.subscribe(request.getRouteId(), user);
    }

    public void unsubscribe(SubscriptionRequest request) {
        User user = userService.getUser(request.getUserId());
        routeSubscriptionService.unsubscribe(request.getRouteId(), user);
    }
}