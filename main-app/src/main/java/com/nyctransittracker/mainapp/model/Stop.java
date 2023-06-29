package com.nyctransittracker.mainapp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)

@Entity
@Table(name="stops")
public class Stop {
    @Id
    private String uniqueId; // route-GTFS_id-direction
    @JsonProperty("id")
    private String GTFSStopID;
    @Transient
    private Map<String, List> routes;
    private String route;
    private String direction;
    private String name;
    private Double latitude;
    private Double longitude;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "subscriptions",
            joinColumns = @JoinColumn(name = "stop_unqiue_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> subscribedUsers;
}