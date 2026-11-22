package floodwarning.api;
import floodwarning.model.FloodReading;
import java.io.IOException;
import java.util.List;
/** Boundary for the selected public flood-data API. */
public interface FloodDataClient { List<FloodReading> fetchReadings() throws IOException, InterruptedException; }
