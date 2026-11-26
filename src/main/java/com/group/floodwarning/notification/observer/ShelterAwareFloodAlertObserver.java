package com.group.floodwarning.notification.observer;

import com.group.floodwarning.notification.model.FloodReading;
import com.group.floodwarning.notification.model.RiskLevel;
import com.group.floodwarning.notification.model.Shelter;
import com.group.floodwarning.notification.repository.ShelterRepository;
import com.group.floodwarning.notification.sender.NotificationSender;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/** Builds a risk alert and recommends a shelter with available beds. */
public final class ShelterAwareFloodAlertObserver implements FloodAlertObserver {
    private final List<String> recipients;
    private final ShelterRepository shelters;
    private final NotificationSender sender;

    public ShelterAwareFloodAlertObserver(
            List<String> recipients,
            ShelterRepository shelters,
            NotificationSender sender) {
        this.recipients = List.copyOf(recipients);
        this.shelters = Objects.requireNonNull(shelters);
        this.sender = Objects.requireNonNull(sender);
    }

    @Override
    public void onRiskChanged(FloodReading reading, RiskLevel level) {
        Shelter recommended = level == RiskLevel.NORMAL
                ? null
                : shelters.findAvailableByLocation(reading.location()).stream()
                        .max(Comparator.comparingInt(Shelter::availableBeds))
                        .orElse(null);
        String subject = "Flood " + level + " alert: " + reading.location();
        String message = buildMessage(reading, level, recommended);

        for (String recipient : recipients) {
            sender.send(recipient, subject, message);
        }
    }

    private String buildMessage(FloodReading reading, RiskLevel level, Shelter recommended) {
        String action = switch (level) {
            case NORMAL -> "The flood risk has returned to normal. Continue to follow official updates.";
            case ALERT -> "Stay alert and monitor official updates.";
            case WARNING -> "Prepare to evacuate if instructed by local authorities.";
            case DANGER -> "Move to safety and follow local emergency instructions.";
        };
        String shelterInfo = recommended == null
                ? (level == RiskLevel.NORMAL
                    ? "No shelter recommendation is needed while risk is normal."
                    : "No available shelter beds are listed for " + reading.location()
                        + ". Contact local authorities for assistance.")
                : "Recommended shelter: " + recommended.name() + " (" + recommended.location()
                    + "), " + recommended.availableBeds() + " beds available.";

        return "Flood risk: " + level
                + "\nLocation: " + reading.location()
                + "\nRiver level: " + reading.riverLevelMetres() + " m"
                + "\nRainfall: " + reading.rainfallMillimetresPerHour() + " mm/hour"
                + "\n\n" + action
                + "\n" + shelterInfo;
    }
}
