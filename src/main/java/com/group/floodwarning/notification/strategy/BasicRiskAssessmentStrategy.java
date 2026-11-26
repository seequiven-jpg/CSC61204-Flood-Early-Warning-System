package com.group.floodwarning.notification.strategy;
import com.group.floodwarning.notification.model.FloodReading;
import com.group.floodwarning.notification.model.RiskLevel;
/** Placeholder thresholds: replace with cited/local thresholds agreed by the team. */
public final class BasicRiskAssessmentStrategy implements RiskAssessmentStrategy {
    @Override public RiskLevel assess(FloodReading reading) {
        if (reading.riverLevelMetres() >= 4.0 || reading.rainfallMillimetresPerHour() >= 120) return RiskLevel.DANGER;
        if (reading.riverLevelMetres() >= 3.0 || reading.rainfallMillimetresPerHour() >= 80) return RiskLevel.WARNING;
        if (reading.riverLevelMetres() >= 2.0 || reading.rainfallMillimetresPerHour() >= 50) return RiskLevel.ALERT;
        return RiskLevel.NORMAL;
    }
}
