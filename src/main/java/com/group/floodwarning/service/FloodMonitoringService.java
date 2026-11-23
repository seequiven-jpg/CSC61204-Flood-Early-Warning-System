package com.group.floodwarning.service;
import com.group.floodwarning.api.FloodDataClient;
import com.group.floodwarning.model.*;
import com.group.floodwarning.observer.FloodAlertObserver;
import com.group.floodwarning.strategy.RiskAssessmentStrategy;
import java.io.IOException;
import java.util.*;
/** Coordinates API readings, risk assessment, and alert notifications. */
public final class FloodMonitoringService {
    private final FloodDataClient dataClient;
    private final RiskAssessmentStrategy strategy;
    private final List<FloodAlertObserver> observers = new ArrayList<>();
    public FloodMonitoringService(FloodDataClient dataClient, RiskAssessmentStrategy strategy) { this.dataClient = Objects.requireNonNull(dataClient); this.strategy = Objects.requireNonNull(strategy); }
    public void addObserver(FloodAlertObserver observer) { observers.add(Objects.requireNonNull(observer)); }
    public Map<FloodReading, RiskLevel> assessLatestReadings() throws IOException, InterruptedException {
        Map<FloodReading, RiskLevel> results = new LinkedHashMap<>();
        for (FloodReading reading : dataClient.fetchReadings()) {
            RiskLevel level = strategy.assess(reading); results.put(reading, level);
            if (level != RiskLevel.NORMAL) observers.forEach(observer -> observer.onRiskChanged(reading, level));
        }
        return results;
    }
}
