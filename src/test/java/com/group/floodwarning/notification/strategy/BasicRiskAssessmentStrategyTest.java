package com.group.floodwarning.notification.strategy;

import com.group.floodwarning.notification.model.FloodReading;
import com.group.floodwarning.notification.model.RiskLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BasicRiskAssessmentStrategyTest {

    private BasicRiskAssessmentStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new BasicRiskAssessmentStrategy();
    }

    @Test
    void assess_nullReading_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> strategy.assess(null));
    }

    @ParameterizedTest
    @CsvSource({
        "0.5, 5.0, NORMAL",
        "1.9, 49.0, NORMAL",
        "2.0, 10.0, ALERT",
        "1.0, 50.0, ALERT",
        "3.0, 20.0, WARNING",
        "1.2, 80.0, WARNING",
        "4.0, 10.0, DANGER",
        "1.0, 120.0, DANGER"
    })
    void assess_riverAndRainfallThresholds_returnsExpectedRiskLevel(
            double riverLevel, double rainfall, RiskLevel expected) {
        FloodReading reading = new FloodReading("Gombak", riverLevel, rainfall, "2026-11-24T18:00:00");

        RiskLevel actual = strategy.assess(reading);

        assertEquals(expected, actual);
    }

    @Test
    void assess_combinedModerateRiverAndRainfall_returnsConsistentReadingAndLevel() {
        FloodReading reading = new FloodReading("Ampang", 3.1, 75.0, "2026-11-24T18:15:00");
        RiskLevel level = strategy.assess(reading);

        assertAll(
            () -> assertEquals("Ampang", reading.location()),
            () -> assertEquals(3.1, reading.riverLevelMetres()),
            () -> assertEquals(RiskLevel.WARNING, level)
        );
    }

    @Test
    void assess_deliberateFailureExample_reportsExpectedVsActualMismatch() {
        // Deliberate failure required by Section 4.2: riverLevel 4.5m and rainfall 130mm/h evaluates to
        // RiskLevel.DANGER, but we intentionally assert RiskLevel.NORMAL here to demonstrate how JUnit 5
        // and Maven Surefire report an assertion failure alongside passing tests.
        FloodReading reading = new FloodReading("Klang", 4.5, 130.0, "2026-11-24T18:20:00");

        assertEquals(RiskLevel.NORMAL, strategy.assess(reading));
    }
}
