package com.group.floodwarning.notification.strategy;
import com.group.floodwarning.notification.model.FloodReading;
import com.group.floodwarning.notification.model.RiskLevel;
/** Strategy interface for swappable flood-risk rules. */
public interface RiskAssessmentStrategy { RiskLevel assess(FloodReading reading); }
