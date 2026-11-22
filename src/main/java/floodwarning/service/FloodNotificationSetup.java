package floodwarning.service;

import floodwarning.observer.ShelterAwareFloodAlertObserver;
import floodwarning.repository.ShelterRepository;
import floodwarning.repository.userRepository;

import java.nio.file.Path;
import java.util.Objects;

/** Wires resident notifications into an existing flood monitoring service. */
public final class FloodNotificationSetup {
    private FloodNotificationSetup() {}

    public static void addResidentNotifications(
            FloodMonitoringService monitoringService,
            Path usersFile,
            ShelterRepository shelterRepository,
            NotificationSender sender) {
        Objects.requireNonNull(monitoringService).addObserver(
                new ShelterAwareFloodAlertObserver(
                        new userRepository(usersFile),
                        shelterRepository,
                        sender));
    }
}
