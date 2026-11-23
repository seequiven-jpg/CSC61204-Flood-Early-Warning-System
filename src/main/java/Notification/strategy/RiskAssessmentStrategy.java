package Notification.strategy;
import Notification.model.FloodReading;
import Notification.model.RiskLevel;
/** Strategy interface for swappable flood-risk rules. */
public interface RiskAssessmentStrategy { RiskLevel assess(FloodReading reading); }
