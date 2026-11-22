package com.group.floodwarning.observer;

import com.group.floodwarning.model.FloodReading;
import com.group.floodwarning.model.RiskLevel;
import com.group.floodwarning.model.Shelter;
import com.group.floodwarning.model.User;
import com.group.floodwarning.repository.ShelterRepository;
import com.group.floodwarning.repository.userRepository;
import com.group.floodwarning.service.NotificationSender;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/** Sends each registered resident a risk alert with the best available local shelter. */
public final class ShelterAwareFloodAlertObserver implements FloodAlertObserver {
    private final userRepository users;
    private final ShelterRepository shelters;
    private final NotificationSender sender;

    public ShelterAwareFloodAlertObserver(
            userRepository users,
            ShelterRepository shelters,
            NotificationSender sender) {
        this.users = Objects.requireNonNull(users);
        this.shelters = Objects.requireNonNull(shelters);
        this.sender = Objects.requireNonNull(sender);
    }

    @Override
    public void onRiskChanged(FloodReading reading, RiskLevel level) {
        List<Shelter> available = shelters.findAvailableByLocation(reading.location());
        Shelter recommended = available.stream()
                .max(Comparator.comparingInt(Shelter::availableBeds))
                .orElse(null);
        String subject = "Flood " + level + " alert: " + reading.location();
        String message = buildMessage(reading, level, recommended);

        try {
            for (User resident : users.findResidents()) {
                sender.send(resident.email(), subject, message);
            }
        } catch (IOException e) {
            System.err.println("Could not load resident notification recipients: " + e.getMessage());
        }
    }

    private String buildMessage(FloodReading reading, RiskLevel level, Shelter recommended) {
        String action = switch (level) {
            case ALERT -> "Stay alert and monitor official updates.";
            case WARNING -> "Prepare to evacuate if instructed by local authorities.";
            case DANGER -> "Move to safety and follow local emergency instructions.";
            case NORMAL -> "The flood risk has returned to normal. Continue to follow official updates.";
        };

        String shelterInfo = level == RiskLevel.NORMAL
                ? "No shelter recommendation is needed while risk is normal."
                : recommended == null
                ? "No available shelter beds are listed for " + reading.location()
                    + ". Contact local authorities for assistance."
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
