package com.group.floodwarning.service;

import com.group.floodwarning.observer.ShelterAwareFloodAlertObserver;
import com.group.floodwarning.repository.ShelterRepository;
import com.group.floodwarning.repository.userRepository;

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
