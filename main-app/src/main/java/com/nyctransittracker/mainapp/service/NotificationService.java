package com.nyctransittracker.mainapp.service;

import com.nyctransittracker.mainapp.dto.MtaResponse;
import com.nyctransittracker.mainapp.event.NotificationEvent;
import com.nyctransittracker.mainapp.model.Route;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final RedisService redisService;
    private final RouteSubscriptionService routeSubscriptionService;

    private final KafkaTemplate<String, NotificationEvent> kafkaTemplate;
    private final static String topic = "emails-topic";

    public void processNotifications() {
        log.info("Starting notification process: " + Instant.now().toString());
        MtaResponse mtaResponse = redisService.getMtaData();
        Map<String, Route> routes = mtaResponse.getRoutes();
        Map<String, List<String>> emailMap = new HashMap<>();
        Map<String, List<String>> alertMap = new HashMap<>();
        routes.forEach((routeId, route) -> {
            Map<String, String> irregularities = route.getServiceIrregularitySummaries();
            Map<String, List<String>> changes = route.getServiceChangeSummaries();
            boolean irregularitiesEmpty = isIrregularitiesEmpty(irregularities);
            boolean changesEmpty = isChangesEmpty(changes);
            if (irregularitiesEmpty && changesEmpty) {
                return;
            }
            List<String> alerts = new ArrayList<>();
            if (!irregularitiesEmpty) {
                alerts.add("North bound irregularity: " + irregularities.get("north"));
                alerts.add("South bound irregularity: " + irregularities.get("south"));
            }
            if (!changesEmpty) {
                for (String str : changes.get("both")) {
                    alerts.add("Both service change: " + str);
                }
                for (String str : changes.get("north")) {
                    alerts.add("North bound service change: " + str);
                }
                for (String str : changes.get("south")) {
                    alerts.add("South bound service change: " + str);
                }
            }
            alertMap.put(routeId, alerts);
            emailMap.put(routeId, routeSubscriptionService.getEmails(routeId));
        });
        // email to routes to alerts
        Map<String, Map<String, List<String>>> emailAlerts = new HashMap<>();
        emailMap.forEach((routeId, emailList) -> {
            for (String email : emailList) {
                var routeAlerts = emailAlerts.getOrDefault(email, new HashMap<>());
                routeAlerts.put(routeId, alertMap.get(routeId));
                emailAlerts.put(email, routeAlerts);
            }
        });
        kafkaTemplate.send(topic, new NotificationEvent(emailAlerts));
        log.info("Done with notification process: " + Instant.now().toString());
    }

    private boolean isIrregularitiesEmpty(Map<String, String> irregularities) {
        return (irregularities.get("north") == null) && (irregularities.get("south") == null);
    }

    private boolean isChangesEmpty(Map<String, List<String>> changes) {
        return changes.get("both").isEmpty() && changes.get("north").isEmpty() && changes.get("south").isEmpty();
    }
}
