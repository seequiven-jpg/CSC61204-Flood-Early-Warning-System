package Notification.app;

import Notification.api.FloodDataClient;
import Notification.model.FloodReading;
import Notification.observer.ShelterAwareFloodAlertObserver;
import Notification.repository.ShelterRepository;
import Notification.sender.ConsoleNotificationSender;
import Notification.sender.FloodMonitoringService;
import Notification.strategy.BasicRiskAssessmentStrategy;

import java.util.List;

public final class Main {
    private Main() {}

    public static void main(String[] args) throws Exception {
        FloodDataClient demoData = () -> List.of(
                new FloodReading("Kampung Baru", 4.2, 75, "demo reading"),
                new FloodReading("Taman Jaya", 3.3, 82, "demo reading")
        );

        List<String> recipients = List.of("resident@example.com");
        FloodMonitoringService monitoring = new FloodMonitoringService(
                demoData,
                new BasicRiskAssessmentStrategy());
        monitoring.addObserver(new ShelterAwareFloodAlertObserver(
                recipients,
                new ShelterRepository(),
                new ConsoleNotificationSender()));

        monitoring.assessLatestReadings();
    }
}
