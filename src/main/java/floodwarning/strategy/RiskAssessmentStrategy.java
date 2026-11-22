package floodwarning.strategy;
import floodwarning.model.FloodReading;
import floodwarning.model.RiskLevel;
/** Strategy interface for swappable flood-risk rules. */
public interface RiskAssessmentStrategy { RiskLevel assess(FloodReading reading); }
