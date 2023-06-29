package com.nyctransittracker.mainapp.service;

import com.nyctransittracker.mainapp.model.RouteSubscription;
import com.nyctransittracker.mainapp.model.User;
import com.nyctransittracker.mainapp.repository.RouteSubscriptionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RouteSubscriptionService {

    private final RouteSubscriptionRepository routeSubscriptionRepository;

    public RouteSubscription getRouteSubscription(String id) {
        return routeSubscriptionRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    public void subscribe(String id, User user) {
        RouteSubscription routeSubscription = getRouteSubscription(id);
        routeSubscription.getSubscribedUsers().add(user);
        routeSubscriptionRepository.save(routeSubscription);
    }

    public void unsubscribe(String id, User user) {
        RouteSubscription routeSubscription = getRouteSubscription(id);
        routeSubscription.getSubscribedUsers().remove(user);
        routeSubscriptionRepository.save(routeSubscription);
    }

    public void createRouteSubscriptions(String[] routes) {
        if (routeSubscriptionRepository.count() != 0) {
            return;
        }
        List<RouteSubscription> routeSubscriptions = new ArrayList<>();
        for (String route : routes) {
            routeSubscriptions.add(new RouteSubscription(route, new HashSet<>()));
        }
        routeSubscriptionRepository.saveAll(routeSubscriptions);
    }

    public List<String> getEmails(String id) {
        RouteSubscription routeSubscription = getRouteSubscription(id);
        return routeSubscription.getSubscribedUsers().stream().map(User::getEmail).toList();
    }
}
