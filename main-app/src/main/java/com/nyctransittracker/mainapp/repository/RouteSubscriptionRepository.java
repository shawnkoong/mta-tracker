package com.nyctransittracker.mainapp.repository;

import com.nyctransittracker.mainapp.model.RouteSubscription;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RouteSubscriptionRepository extends JpaRepository<RouteSubscription, String> {
}
