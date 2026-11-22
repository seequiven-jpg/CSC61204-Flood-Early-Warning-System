package org.example;

import java.util.List;

public class EvacuationService {
    private final EvacuationRepository repository;
    public EvacuationService(EvacuationRepository repository) {

        if (repository == null) {
            throw new IllegalArgumentException(
                    "Evacuation repository cannot be null."
            );
        }

        this.repository = repository;
    }

    public List<String> getAvailableDistricts() {

        return repository.getAllDistricts();
    }

    public List<EvacuationCenter> findCentres(String district) {

        if (district == null || district.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "District cannot be empty."
            );
        }

        return repository.getCentersByDistrict(district);
    }
}
