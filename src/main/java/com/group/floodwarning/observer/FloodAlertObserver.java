package com.group.floodwarning.observer;
import com.group.floodwarning.model.FloodReading;
import com.group.floodwarning.model.RiskLevel;
/** Receives risk updates from the flood monitoring service. */
public interface FloodAlertObserver { void onRiskChanged(FloodReading reading, RiskLevel level); }
