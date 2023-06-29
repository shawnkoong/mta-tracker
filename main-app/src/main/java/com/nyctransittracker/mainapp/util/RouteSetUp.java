package com.nyctransittracker.mainapp.util;

import com.nyctransittracker.mainapp.service.RouteSubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RouteSetUp implements CommandLineRunner {

    private final RouteSubscriptionService service;
    private final String[] routes =
            new String[]{
                    "1", "2", "3", "4", "5", "6", "6X", "7", "7X", "A", "AL", "C", "E", "F", "FX", "D", "B", "M",
                    "J", "Z", "R", "N", "Q", "W", "G", "H", "FS", "GS", "L", "SI"
            };


    @Override
    public void run(String... args) throws Exception {
        setUpRoutes();
    }

    private void setUpRoutes() {
        service.createRouteSubscriptions(routes);
    }
}
