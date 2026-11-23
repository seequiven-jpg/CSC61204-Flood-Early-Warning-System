package Notification.api;
import Notification.model.FloodReading;
import java.io.IOException;
import java.util.List;
/** Boundary for the selected public flood-data API. */
public interface FloodDataClient { List<FloodReading> fetchReadings() throws IOException, InterruptedException; }
