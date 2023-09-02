package com.nyctransittracker.mainapp.dto;

import com.nyctransittracker.mainapp.model.RouteSubscription;

import java.util.Set;

public record UserDTO(Integer id, String email, Set<String> subscriptions) {
}
