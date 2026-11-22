package floodwarning.observer;
import floodwarning.model.FloodReading;
import floodwarning.model.RiskLevel;
/** Receives risk updates from the flood monitoring service. */
public interface FloodAlertObserver { void onRiskChanged(FloodReading reading, RiskLevel level); }
