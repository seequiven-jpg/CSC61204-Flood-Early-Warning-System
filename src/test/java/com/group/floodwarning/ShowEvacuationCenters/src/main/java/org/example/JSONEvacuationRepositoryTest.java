package com.group.floodwarning.ShowEvacuationCenters.src.main.java.org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JSONEvacuationRepositoryTest {

    private JSONEvacuationRepository repository;

    @BeforeEach
    void setUp() {
        repository = new JSONEvacuationRepository();
    }

    @Test
    void getAllDistricts_loadedFromJson_returnsDistricts() {
        List<String> districts = repository.getAllDistricts();

        assertFalse(districts.isEmpty());
    }

    @Test
    void getCentersByDistrict_knownDistrictWithWhitespaceAndCase_returnsCenters() {
        String firstDistrict = repository.getAllDistricts().get(0);
        List<EvacuationCenter> centers = repository.getCentersByDistrict("  " + firstDistrict.toUpperCase() + "  ");

        assertAll(
            () -> assertFalse(centers.isEmpty()),
            () -> assertFalse(centers.get(0).getName().isBlank())
        );
    }

    @Test
    void getCentersByDistrict_unknownDistrict_returnsEmptyList() {
        List<EvacuationCenter> centers = repository.getCentersByDistrict("NonExistentDistrictXYZ");

        assertTrue(centers.isEmpty());
    }
}
