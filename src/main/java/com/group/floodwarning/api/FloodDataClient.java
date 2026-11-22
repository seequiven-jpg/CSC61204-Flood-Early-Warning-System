package com.group.floodwarning.api;
import com.group.floodwarning.model.FloodReading;
import java.io.IOException;
import java.util.List;
/** Boundary for the selected public flood-data API. */
public interface FloodDataClient { List<FloodReading> fetchReadings() throws IOException, InterruptedException; }
