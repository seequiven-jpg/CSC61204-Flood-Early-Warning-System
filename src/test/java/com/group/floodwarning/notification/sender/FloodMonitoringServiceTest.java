package com.group.floodwarning.notification.sender;

import com.group.floodwarning.notification.api.FloodDataClient;
import com.group.floodwarning.notification.model.FloodReading;
import com.group.floodwarning.notification.model.RiskLevel;
import com.group.floodwarning.notification.observer.FloodAlertObserver;
import com.group.floodwarning.notification.strategy.BasicRiskAssessmentStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FloodMonitoringServiceTest {

    private BasicRiskAssessmentStrategy strategy;
    private List<RiskLevel> observedLevels;
    private FloodAlertObserver recordingObserver;

    @BeforeEach
    void setUp() {
        strategy = new BasicRiskAssessmentStrategy();
        observedLevels = new ArrayList<>();
        recordingObserver = (reading, level) -> observedLevels.add(level);
    }

    @Test
    void constructorAndAddObserver_nullArguments_throwNullPointerException() {
        FloodDataClient dummyClient = List::of;
        FloodMonitoringService service = new FloodMonitoringService(dummyClient, strategy);

        assertAll(
            () -> assertThrows(NullPointerException.class,
                () -> new FloodMonitoringService(null, strategy)),
            () -> assertThrows(NullPointerException.class,
                () -> new FloodMonitoringService(dummyClient, null)),
            () -> assertThrows(NullPointerException.class,
                () -> service.addObserver(null))
        );
    }

    @Test
    void assessLatestReadings_firstReadingIsNormal_doesNotNotifyObserver() throws Exception {
        FloodReading normalReading = new FloodReading("Kampung Baru", 1.1, 10.0, "2026-11-24T18:00:00");
        FloodMonitoringService service = new FloodMonitoringService(() -> List.of(normalReading), strategy);
        service.addObserver(recordingObserver);

        Map<FloodReading, RiskLevel> results = service.assessLatestReadings();

        assertAll(
            () -> assertEquals(RiskLevel.NORMAL, results.get(normalReading)),
            () -> assertEquals(0, observedLevels.size())
        );
    }

    @Test
    void assessLatestReadings_riskEscalatesThenStaysSame_notifiesOnlyOnChange() throws Exception {
        AtomicReference<List<FloodReading>> batch = new AtomicReference<>(List.of(
            new FloodReading("Taman Sri Muda", 3.2, 85.0, "2026-11-24T18:00:00")
        ));
        FloodMonitoringService service = new FloodMonitoringService(batch::get, strategy);
        service.addObserver(recordingObserver);

        Map<FloodReading, RiskLevel> firstRun = service.assessLatestReadings();
        Map<FloodReading, RiskLevel> secondRun = service.assessLatestReadings();

        assertAll(
            () -> assertEquals(RiskLevel.WARNING, firstRun.values().iterator().next()),
            () -> assertEquals(RiskLevel.WARNING, secondRun.values().iterator().next()),
            () -> assertEquals(1, observedLevels.size()),
            () -> assertEquals(RiskLevel.WARNING, observedLevels.get(0))
        );
    }

    @Test
    void assessLatestReadings_emptyFeed_returnsEmptyMap() throws Exception {
        FloodMonitoringService service = new FloodMonitoringService(List::of, strategy);
        service.addObserver(recordingObserver);

        Map<FloodReading, RiskLevel> results = service.assessLatestReadings();

        assertAll(
            () -> assertEquals(0, results.size()),
            () -> assertNull(results.get(null))
        );
    }
}
