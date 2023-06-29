package com.nyctransittracker.mainapp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class RouteSubscription {
    @Id
    private String id;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "subscriptions", joinColumns = @JoinColumn(name = "route_subscription_id"),
    inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> subscribedUsers;
}
