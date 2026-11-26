package com.group.floodwarning.ShowEvacuationCenters.src.main.java.org.example;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EvacuationControllerTest {

    private InputStream originalIn;
    private PrintStream originalOut;
    private EvacuationService service;

    @BeforeEach
    void setUp() {
        originalIn = System.in;
        originalOut = System.out;
        Map<String, List<EvacuationCenter>> byDistrict = Map.of(
            "Gombak", List.of(new EvacuationCenter("Dewan Orang Ramai")),
            "Petaling", List.of()
        );
        EvacuationRepository stubRepo = new EvacuationRepository() {
            @Override
            public List<EvacuationCenter> getCentersByDistrict(String district) {
                return byDistrict.getOrDefault(district, List.of());
            }

            @Override
            public List<String> getAllDistricts() {
                return List.of("Gombak", "Petaling");
            }
        };
        service = new EvacuationService(stubRepo);
    }

    @AfterEach
    void tearDown() {
        System.setIn(originalIn);
        System.setOut(originalOut);
    }

    @Test
    void constructor_nullService_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new EvacuationController(null));
    }

    @Test
    void start_validDistrictEmptyDistrictOutOfRangeThenExit_printsExpectedOutput() {
        String simulatedInput = "1\n2\n99\n0\n";
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes(StandardCharsets.UTF_8)));
        ByteArrayOutputStream capturedOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(capturedOut, true, StandardCharsets.UTF_8));

        EvacuationController controller = new EvacuationController(service);
        controller.start();

        String output = capturedOut.toString(StandardCharsets.UTF_8);
        assertAll(
            () -> assertTrue(output.contains("Dewan Orang Ramai")),
            () -> assertTrue(output.contains("No centers found")),
            () -> assertTrue(output.contains("Invalid input")),
            () -> assertTrue(output.contains("Exit"))
        );
    }
}
