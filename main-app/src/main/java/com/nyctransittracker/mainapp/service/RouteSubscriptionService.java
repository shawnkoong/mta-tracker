package com.nyctransittracker.mainapp.service;

import com.nyctransittracker.mainapp.model.RouteSubscription;
import com.nyctransittracker.mainapp.model.User;
import com.nyctransittracker.mainapp.repository.RouteSubscriptionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RouteSubscriptionService {

    private final RouteSubscriptionRepository routeSubscriptionRepository;

    public RouteSubscription getRouteSubscription(String id) {
        return routeSubscriptionRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    public void subscribeTo(String id, User user) {
        RouteSubscription routeSubscription = getRouteSubscription(id);
        routeSubscription.getSubscribedUsers().add(user);
        routeSubscriptionRepository.save(routeSubscription);
    }
}
