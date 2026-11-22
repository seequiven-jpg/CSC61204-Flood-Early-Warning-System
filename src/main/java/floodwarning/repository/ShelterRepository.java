package floodwarning.repository;

import floodwarning.model.Shelter;

import java.util.List;
import java.util.Locale;

/** In-memory shelter catalog. Replace the sample data with your chosen data source. */
public final class ShelterRepository {
    private final List<Shelter> shelters;

    public ShelterRepository() {
        this(List.of(
                new Shelter("Community Hall", "Kampung Baru", 100, 35),
                new Shelter("District School", "Taman Jaya", 80, 80),
                new Shelter("Sports Centre", "Kampung Baru", 150, 90)
        ));
    }

    public ShelterRepository(List<Shelter> shelters) {
        this.shelters = List.copyOf(shelters);
    }

    public List<Shelter> findAvailableByLocation(String location) {
        String requestedLocation = location.strip().toLowerCase(Locale.ROOT);
        return shelters.stream()
                .filter(shelter -> shelter.location().strip().toLowerCase(Locale.ROOT).equals(requestedLocation))
                .filter(shelter -> shelter.availableBeds() > 0)
                .toList();
    }
}
