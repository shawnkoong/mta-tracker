package com.nyctransittracker.mainapp.service;

import com.nyctransittracker.mainapp.dto.MtaResponse;
import com.nyctransittracker.mainapp.dto.TrainPositionResponse;
import com.nyctransittracker.mainapp.model.*;
import com.nyctransittracker.mainapp.util.PathMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.algorithm.Angle;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.linearref.LengthIndexedLine;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

import static com.nyctransittracker.mainapp.util.StopIdUtil.findNextStopId;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainPositionService {

    private final RedisService redisService;
    private final GeometryFactory geometryFactory;
    private final PathMap pathMap;

    public TrainPositionResponse getTrainPositions() {
        return redisService.getTrainPositions();
    }

    public void processTrainPositions() {
        UUID processId = UUID.randomUUID();
        log.info("Starting train position calculation: " + Instant.now().toString() + ", Process " + processId);
        MtaResponse mtaResponse = redisService.getMtaData();
        Map<String, Route> routes = mtaResponse.getRoutes();
        Map<String, Map<String, List<CoordinateBearing>>> allPositions = new HashMap<>();
        routes.entrySet().parallelStream().forEach(routeEntry -> {
            Route route = routeEntry.getValue();
            if (!route.isScheduled() || route.getStatus().equals("No Service")) {
                return;
            }
            String line = routeEntry.getKey();
            Map<String, List<CoordinateBearing>> directionMap = new HashMap<>();
            Map<String, List<Trip>> trips = route.getTrips();
            trips.forEach((direction, tripList) -> {
                List<CoordinateBearing> trainPositions = new ArrayList<>();
                tripList.parallelStream().forEach((trip) -> {
                    String lastStopId = trip.getLastStopMade();
                    if (lastStopId == null) {
                        return;
                    }
                    Map<String, Long> stops = trip.getStops();
                    String nextStopId = findNextStopId(stops, lastStopId);
                    String pathName = (direction.equals("north")) ?
                            (lastStopId + "-" + nextStopId) : (nextStopId + "-" + lastStopId);
                    List<Point> coordinates = pathMap.getCoordinates(pathName);
                    if (coordinates.isEmpty()) {
                        return;
                    }
                    CoordinateBearing coordinateBearing =
                            calculateTrainPosition(stops, coordinates, lastStopId, nextStopId);
                    trainPositions.add(coordinateBearing);
                });
                directionMap.put(direction, trainPositions);
            });
            allPositions.put(line, directionMap);
        });
        redisService.saveTrainPositions(new TrainPositionResponse(Instant.now().getEpochSecond(), allPositions));
        log.info("Done with train position calculation: " + Instant.now().toString() + ", Process " + processId);
    }

    private CoordinateBearing calculateTrainPosition(Map<String, Long> stops, List<Point> points,
                                                     String lastStopId, String nextStopId) {
        Coordinate[] coordinates = points.stream()
                .map(point -> new Coordinate(point.longitude(), point.latitude()))
                .toArray(Coordinate[]::new);
        LineString lineString = geometryFactory.createLineString(coordinates);
        LengthIndexedLine indexedLine = new LengthIndexedLine(lineString);
        long lastTimestamp = stops.get(lastStopId);
        long nextTimeStamp = stops.get(nextStopId);
        long nowTimestamp = Instant.now().getEpochSecond();
        double progress = (double) (nowTimestamp - lastTimestamp) / (nextTimeStamp - lastTimestamp);
        double length = lineString.getLength();
        Coordinate coordinate = indexedLine.extractPoint(progress * length);
        Coordinate heading = indexedLine.extractPoint((progress + 0.01) * length);
        // Returns the angle of the vector from p0 to p1, relative to the positive X-axis.
        // The angle is normalized to be in the range [ -Pi, Pi ].
        double bearing = Angle.angle(coordinate, heading);
        return new CoordinateBearing(coordinate.getX(), coordinate.getY(), bearing);
    }
}
