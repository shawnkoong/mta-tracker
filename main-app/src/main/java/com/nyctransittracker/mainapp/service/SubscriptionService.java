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

    public void subscribe(Integer userId, String routeId) {
        User user = userService.getUser(userId);
        routeSubscriptionService.subscribe(routeId, user);
    }

    public void unsubscribe(Integer userId, String routeId) {
        User user = userService.getUser(userId);
        routeSubscriptionService.unsubscribe(routeId, user);
    }
}