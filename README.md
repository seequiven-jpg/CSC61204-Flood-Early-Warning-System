# Flood Early Warning System

A small Java 17 demonstration of flood-risk assessment and shelter-aware alerts.

## Run the demo

From the project folder, run:

```powershell
mvn compile
java -cp target/classes Notification.app.Main
```

The demo readings, recipient address, and shelter capacities are sample values. Replace them with your data source and recipient list. The current sender prints alerts to the console; connect an email or SMS provider in `NotificationSender` for external delivery.

Risk thresholds in `BasicRiskAssessmentStrategy` are placeholders and should be replaced with thresholds approved for the intended location.
