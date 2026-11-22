package com.group.floodwarning.service;

import com.group.floodwarning.api.FloodDataClient;
import com.group.floodwarning.model.FloodReading;
import com.group.floodwarning.model.RiskLevel;
import com.group.floodwarning.observer.FloodAlertObserver;
import com.group.floodwarning.strategy.RiskAssessmentStrategy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/** Coordinates API readings, risk assessment, and alert notifications. */
public final class FloodMonitoringService {
    private final FloodDataClient dataClient;
    private final RiskAssessmentStrategy strategy;
    private final List<FloodAlertObserver> observers = new ArrayList<>();
    private final Map<String, RiskLevel> lastRiskByLocation = new HashMap<>();

    public FloodMonitoringService(FloodDataClient dataClient, RiskAssessmentStrategy strategy) {
        this.dataClient = Objects.requireNonNull(dataClient);
        this.strategy = Objects.requireNonNull(strategy);
    }

    public void addObserver(FloodAlertObserver observer) {
        observers.add(Objects.requireNonNull(observer));
    }

    public Map<FloodReading, RiskLevel> assessLatestReadings() throws IOException, InterruptedException {
        Map<FloodReading, RiskLevel> results = new LinkedHashMap<>();
        for (FloodReading reading : dataClient.fetchReadings()) {
            RiskLevel level = strategy.assess(reading);
            results.put(reading, level);

            RiskLevel previous = lastRiskByLocation.put(reading.location(), level);
            boolean firstNormalReading = previous == null && level == RiskLevel.NORMAL;
            if (!firstNormalReading && level != previous) {
                observers.forEach(observer -> observer.onRiskChanged(reading, level));
            }
        }
        return results;
    }
}
