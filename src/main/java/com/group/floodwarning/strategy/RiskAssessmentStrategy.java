package com.group.floodwarning.strategy;
import com.group.floodwarning.model.FloodReading;
import com.group.floodwarning.model.RiskLevel;
/** Strategy interface for swappable flood-risk rules. */
public interface RiskAssessmentStrategy { RiskLevel assess(FloodReading reading); }
