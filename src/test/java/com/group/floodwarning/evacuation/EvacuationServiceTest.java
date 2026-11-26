package com.group.floodwarning.evacuation;

import com.group.floodwarning.ShowEvacuationCenters.src.main.java.org.example.EvacuationCenter;
import com.group.floodwarning.ShowEvacuationCenters.src.main.java.org.example.EvacuationRepository;
import com.group.floodwarning.ShowEvacuationCenters.src.main.java.org.example.EvacuationService;
import com.group.floodwarning.ShowEvacuationCenters.src.main.java.org.example.JSONEvacuationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EvacuationServiceTest {

    private EvacuationService service;

    @BeforeEach
    void setUp() {
        EvacuationRepository stubRepo = new EvacuationRepository() {
            private final Map<String, List<String>> data = Map.of(
                "Petaling", List.of("Dewan Orang Ramai Sri Muda", "SK Seksyen 13"),
                "Gombak", List.of("Balai Raya Batu Caves")
            );

            @Override
            public List<EvacuationCenter> getCentersByDistrict(String district) {
                List<String> names = data.getOrDefault(district.trim(), List.of());
                return names.stream().map(EvacuationCenter::new).toList();
            }

            @Override
            public List<String> getAllDistricts() {
                return List.copyOf(data.keySet());
            }
        };
        service = new EvacuationService(stubRepo);
    }

    @Test
    void constructor_nullRepository_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new EvacuationService(null));
    }

    @Test
    void getAvailableDistricts_populatedRepository_returnsAllDistricts() {
        List<String> districts = service.getAvailableDistricts();

        assertAll(
            () -> assertEquals(2, districts.size()),
            () -> assertTrue(districts.contains("Petaling")),
            () -> assertTrue(districts.contains("Gombak"))
        );
    }

    @Test
    void findCentres_matchingDistrict_returnsCentersForDistrict() {
        List<EvacuationCenter> centers = service.findCentres("Petaling");

        assertEquals(2, centers.size());
    }

    @Test
    void findCentres_unknownDistrict_returnsEmptyList() {
        List<EvacuationCenter> centers = service.findCentres("Sepang");

        assertTrue(centers.isEmpty());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t"})
    void findCentres_nullOrBlankDistrict_throwsIllegalArgumentException(String invalidDistrict) {
        assertThrows(IllegalArgumentException.class, () -> service.findCentres(invalidDistrict));
    }

    @Test
    void jsonRepositoryIntegration_loadsResourceFile_returnsNonEmptyDistricts() {
        EvacuationService jsonService = new EvacuationService(new JSONEvacuationRepository());

        List<String> districts = jsonService.getAvailableDistricts();

        assertFalse(districts.isEmpty());
    }
}
