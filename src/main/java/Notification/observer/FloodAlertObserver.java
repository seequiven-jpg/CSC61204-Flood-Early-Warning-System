package Notification.observer;
import Notification.model.FloodReading;
import Notification.model.RiskLevel;
/** Receives risk updates from the flood monitoring service. */
public interface FloodAlertObserver { void onRiskChanged(FloodReading reading, RiskLevel level); }
