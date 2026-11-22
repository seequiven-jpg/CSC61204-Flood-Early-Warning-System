package floodwarning.app;

import floodwarning.api.FloodDataClient;
import floodwarning.model.FloodReading;
import floodwarning.observer.ShelterAwareFloodAlertObserver;
import floodwarning.repository.ShelterRepository;
import floodwarning.service.ConsoleNotificationSender;
import floodwarning.service.FloodMonitoringService;
import floodwarning.strategy.BasicRiskAssessmentStrategy;

import java.util.List;

/** Runs a small local demonstration of flood-risk and shelter notifications. */
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
