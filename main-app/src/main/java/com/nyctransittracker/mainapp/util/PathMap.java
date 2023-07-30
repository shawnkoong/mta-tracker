package com.nyctransittracker.mainapp.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nyctransittracker.mainapp.model.Point;
import com.nyctransittracker.mainapp.model.StationDetail;
import jakarta.annotation.PostConstruct;
import lombok.extern.java.Log;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Log
public class PathMap {

    private Map<String, StationDetail> stationDetailMap;
    private Map<String, List<Point>> coordinateMap;
    private final static String FILENAME = "station_details.json";

    @PostConstruct
    public void build() throws IOException {
        ClassPathResource resource = new ClassPathResource(FILENAME);
        ObjectMapper mapper = new ObjectMapper();
        stationDetailMap = mapper.readValue(resource.getInputStream(),
                new TypeReference<Map<String, StationDetail>>() {});
        coordinateMap = new HashMap<>();
        stationDetailMap.forEach((stopId, stationDetail) -> {
            stationDetail.getNorth().forEach((nextStopId, coordinateList) -> {
                String pathName = stopId + "-" + nextStopId;
                List<Point> points = new ArrayList<>(coordinateList.stream()
                        .map(coordinate -> new Point(coordinate.get(0), coordinate.get(1)))
                        .toList()); // coordinates not including the current or next stops' coordinates
                // add current stop's coordinates
                points.add(0, new Point(stationDetail.getLongitude(), stationDetail.getLatitude()));
                StationDetail nextStationDetail = stationDetailMap.get(nextStopId);
                // add next stop's coordinates
                points.add(new Point(nextStationDetail.getLongitude(), nextStationDetail.getLatitude()));
                coordinateMap.put(pathName, points);
            });
        });
        log.info("finished building coordinates");
    }

    public List<Point> getCoordinates(String pathName) {
        List<Point> coordinates = this.coordinateMap.get(pathName);
        if (coordinates != null) {
            return coordinates;
        }
        String[] nameSplit = pathName.split("-");
        coordinates = getCoordinatesRecursive(nameSplit[0], nameSplit[1], 0);
        if (coordinates.isEmpty()) {
            return new ArrayList<>();
        }
        // save the new coordinates so next time you can grab from map rather than finding it again.
        this.coordinateMap.put(pathName, coordinates);
        return coordinates;
    }

    /**
     * @param start - stopId of the starting stop
     * @param end - stopId of the destination stop
     * @param step - number steps into the recursive call for stops that split into multiple stops
     * @return list of Points from start to end, or empty list if path does not exist
     * <p>
     * finds path from Station x to Station x+n, going from x->x+n, then x+1->x+n, until a path exists,
     * most likely till x+n-1->x+n
     */
    private List<Point> getCoordinatesRecursive(String start, String end, int step) {
        if (step > 15) {
            return new ArrayList<>();
        }
        List<Point> coordinates = this.coordinateMap.get(start + "-" + end);
        if (coordinates != null) {
            return coordinates;
        }
        StationDetail curr = this.stationDetailMap.get(start);
        if (curr.getNorth().isEmpty()) {
            return new ArrayList<>();
        }
        for (var entry : curr.getNorth().entrySet()) {
            String next = entry.getKey();
            List<Point> nextCoordinates = getCoordinatesRecursive(next, end, step + 1);
            if (nextCoordinates.isEmpty()) {
                continue;
            }
            List<Point> coordinatesToNext = this.coordinateMap.get(start + "-" + next);
            // excluding the first coordinate since each list of coordinates has the coordinates for the start and end stops
            coordinatesToNext.addAll(nextCoordinates.subList(1, nextCoordinates.size()));
            return coordinatesToNext;
        }
        // if the for loop terminates, then none of the next stops led to end station, return empty list.
        return new ArrayList<>();
    }
}
