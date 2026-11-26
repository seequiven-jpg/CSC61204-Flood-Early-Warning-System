package com.group.floodwarning.service;

import com.group.floodwarning.api.FloodDataClient;
import com.group.floodwarning.model.FloodReading;
import com.group.floodwarning.model.RiskLevel;
import com.group.floodwarning.observer.FloodAlertObserver;
import com.group.floodwarning.strategy.RiskAssessmentStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ServiceFloodMonitoringServiceTest {

    private RiskAssessmentStrategy thresholdStrategy;
    private List<RiskLevel> notifiedLevels;
    private FloodAlertObserver observer;

    @BeforeEach
    void setUp() {
        thresholdStrategy = reading -> reading.riverLevelMetres() >= 3.0 ? RiskLevel.DANGER : RiskLevel.NORMAL;
        notifiedLevels = new ArrayList<>();
        observer = (reading, level) -> notifiedLevels.add(level);
    }

    @Test
    void constructorAndAddObserver_nullArguments_throwNullPointerException() {
        FloodDataClient client = List::of;
        FloodMonitoringService service = new FloodMonitoringService(client, thresholdStrategy);

        assertAll(
            () -> assertThrows(NullPointerException.class,
                () -> new FloodMonitoringService(null, thresholdStrategy)),
            () -> assertThrows(NullPointerException.class,
                () -> new FloodMonitoringService(client, null)),
            () -> assertThrows(NullPointerException.class,
                () -> service.addObserver(null))
        );
    }

    @Test
    void assessLatestReadings_mixedRiskReadings_notifiesOnlyNonNormal() throws Exception {
        FloodReading normal = new FloodReading("Setapak", 1.2, 15.0, "2026-11-24T18:00:00");
        FloodReading danger = new FloodReading("Gombak", 3.8, 110.0, "2026-11-24T18:05:00");
        FloodMonitoringService service = new FloodMonitoringService(
            () -> List.of(normal, danger),
            thresholdStrategy
        );
        service.addObserver(observer);

        Map<FloodReading, RiskLevel> results = service.assessLatestReadings();

        assertAll(
            () -> assertEquals(2, results.size()),
            () -> assertEquals(RiskLevel.NORMAL, results.get(normal)),
            () -> assertEquals(RiskLevel.DANGER, results.get(danger)),
            () -> assertEquals(List.of(RiskLevel.DANGER), notifiedLevels)
        );
    }
}
