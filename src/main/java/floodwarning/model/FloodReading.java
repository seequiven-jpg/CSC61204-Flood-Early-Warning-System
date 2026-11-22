package floodwarning.model;

/** Current observed or simulated flood reading for one neighbourhood. */
public record FloodReading(String location, double riverLevelMetres, double rainfallMillimetresPerHour, String observedAt) {
    public FloodReading {
        if (location == null || location.isBlank()) throw new IllegalArgumentException("Location is required");
        if (riverLevelMetres < 0 || rainfallMillimetresPerHour < 0) throw new IllegalArgumentException("Measurements cannot be negative");
    }
}
