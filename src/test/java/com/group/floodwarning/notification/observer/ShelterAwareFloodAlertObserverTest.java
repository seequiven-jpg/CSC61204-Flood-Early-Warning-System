package com.group.floodwarning.notification.observer;

import com.group.floodwarning.notification.model.FloodReading;
import com.group.floodwarning.notification.model.RiskLevel;
import com.group.floodwarning.notification.model.Shelter;
import com.group.floodwarning.notification.repository.ShelterRepository;
import com.group.floodwarning.notification.sender.NotificationSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShelterAwareFloodAlertObserverTest {

    private record SentMessage(String recipient, String subject, String body) {}

    private List<SentMessage> sentMessages;
    private NotificationSender recordingSender;
    private ShelterRepository repository;

    @BeforeEach
    void setUp() {
        sentMessages = new ArrayList<>();
        recordingSender = (recipient, subject, body) -> sentMessages.add(new SentMessage(recipient, subject, body));
        repository = new ShelterRepository(List.of(
            new Shelter("Dewan A", "Kampung Baru", 100, 55),
            new Shelter("Balai B", "Kampung Baru", 120, 30),
            new Shelter("Closed Hall", "Kampung Baru", 50, 50)
        ));
    }

    @Test
    void constructor_nullRepositoryOrSender_throwsNullPointerException() {
        List<String> recipients = List.of("user1@example.com");

        assertAll(
            () -> assertThrows(NullPointerException.class,
                () -> new ShelterAwareFloodAlertObserver(recipients, null, recordingSender)),
            () -> assertThrows(NullPointerException.class,
                () -> new ShelterAwareFloodAlertObserver(recipients, repository, null))
        );
    }

    @ParameterizedTest
    @EnumSource(RiskLevel.class)
    void onRiskChanged_allRiskLevels_sendsSubjectAndActionToAllRecipients(RiskLevel level) {
        List<String> recipients = List.of("alice@example.com", "bob@example.com");
        ShelterAwareFloodAlertObserver observer =
            new ShelterAwareFloodAlertObserver(recipients, repository, recordingSender);
        FloodReading reading = new FloodReading("Kampung Baru", 3.5, 85.0, "2026-11-24T18:00:00");

        observer.onRiskChanged(reading, level);

        assertEquals(2, sentMessages.size());
        assertAll(
            () -> assertEquals("Flood " + level + " alert: Kampung Baru", sentMessages.get(0).subject()),
            () -> assertTrue(sentMessages.get(0).body().contains("Flood risk: " + level)),
            () -> assertEquals("bob@example.com", sentMessages.get(1).recipient())
        );
    }

    @Test
    void onRiskChanged_dangerWithAvailableShelters_recommendsShelterWithMostBeds() {
        ShelterAwareFloodAlertObserver observer =
            new ShelterAwareFloodAlertObserver(List.of("resident@example.com"), repository, recordingSender);
        FloodReading reading = new FloodReading("kampung baru", 4.2, 125.0, "2026-11-24T18:05:00");

        observer.onRiskChanged(reading, RiskLevel.DANGER);

        assertEquals(1, sentMessages.size());
        assertTrue(sentMessages.get(0).body().contains("Recommended shelter: Balai B (Kampung Baru), 90 beds available."));
    }

    @Test
    void onRiskChanged_warningWithNoAvailableShelters_reportsNoAvailableBeds() {
        ShelterRepository emptyRepo = new ShelterRepository(List.of(
            new Shelter("Full Hall", "Taman Sri Muda", 60, 60)
        ));
        ShelterAwareFloodAlertObserver observer =
            new ShelterAwareFloodAlertObserver(List.of("resident@example.com"), emptyRepo, recordingSender);
        FloodReading reading = new FloodReading("Taman Sri Muda", 3.1, 80.0, "2026-11-24T18:10:00");

        observer.onRiskChanged(reading, RiskLevel.WARNING);

        assertTrue(sentMessages.get(0).body().contains(
            "No available shelter beds are listed for Taman Sri Muda. Contact local authorities for assistance."
        ));
    }

    @Test
    void findAvailableByLocation_matchingCaseAndWhitespace_returnsOnlyOpenShelters() {
        List<Shelter> available = repository.findAvailableByLocation("  KAMPUNG BARU  ");

        assertEquals(2, available.size());
    }
}
