package com.nyctransittracker.mainapp.event;

import java.util.List;
import java.util.Map;

public record NotificationEvent(Map<String, Map<String, List<String>>> alerts) {
}
