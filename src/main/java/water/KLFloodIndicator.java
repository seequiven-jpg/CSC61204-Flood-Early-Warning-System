package water;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Locale;
import java.util.StringJoiner;
import javax.net.ssl.HttpsURLConnection;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;

/** Reads selected water level in Kuala Lumpur and Klang Valley. */
public final class KlFloodIndicator {
    private static final String URL = "https://publicinfobanjir.water.gov.my/index.php/aras-air-data-cognos/";
    // Each entry contains station ID and location shown in the output.
    private static final List<Map.Entry<String, String>> KUALA_LUMPUR = List.of(
            Map.entry("3116435", "Kuala Lumpur (Sg. Klang di Jambatan Tun Perak)"),
            Map.entry("0232161WL", "Jalan Tun Razak (Sg. Klang)"),
            Map.entry("0232051WL", "Sri Rampai (Sg. Bunus)"));
    private static final List<Map.Entry<String, String>> SELANGOR = List.of(
            Map.entry("3216467", "Batu Caves (Sg. Batu)"),
            Map.entry("3117482", "Ampang (Sg. Ampang di Kg. Lembah Jaya Utara)"),
            Map.entry("3115403", "Petaling Jaya (Sg. Kayu Ara di Taman Mayang)"),
            Map.entry("3015436", "Puchong (Sg. Klang di Puchong Drop)"),
            Map.entry("3015490", "Shah Alam (Sg. Damansara di TTDI Jaya)"),
            Map.entry("3014401", "Bandar Klang (Sg. Klang)"),
            Map.entry("2917402", "Kajang (Sg. Langat di Taman West Country)"));
    private static final ZoneId MALAYSIA = ZoneId.of("Asia/Kuala_Lumpur");
    private static final DateTimeFormatter TIMESTAMP = DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm");

    private KlFloodIndicator() {}

    /** Returns one line w/location, water level, category, and danger threshold */
    public static String check() {
        var lines = new StringJoiner(System.lineSeparator());
        addReadings(lines, "WLH", KUALA_LUMPUR);
        addReadings(lines, "SEL", SELANGOR);
        return lines.toString();
    }

    // Fetch one page per state.
    private static void addReadings(StringJoiner lines, String state, List<Map.Entry<String, String>> stations) {
        Map<String, List<String>> rows;
        try {
            String url = URL + "?state=" + state + "&district=ALL&station=ALL";
            var connection = (HttpsURLConnection) URI.create(url).toURL().openConnection();
            connection.setConnectTimeout(8_000);
            connection.setReadTimeout(12_000);
            connection.setRequestProperty("User-Agent", "KlFloodIndicator/1.0");
            connection.setRequestProperty("Accept", "text/html");
            try (var body = new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8)) {
                rows = readStations(body);
            } finally {
                connection.disconnect();
            }
        } catch (IOException failure) {
            rows = Map.of();
        }

        LocalDateTime now = LocalDateTime.now(MALAYSIA);
        for (var station : stations) {
            String location = station.getValue();
            List<String> row = rows.get(station.getKey());
            lines.add(row == null ? unavailable(location, null) : format(location, row, now));
        }
    }

    // Read gauge rows from the public HTML table and index them by station ID.
    private static Map<String, List<String>> readStations(Reader html) throws IOException {
        Map<String, List<String>> stationRows = new HashMap<>();
        new ParserDelegator().parse(html, new HTMLEditorKit.ParserCallback() {
            private final List<String> row = new ArrayList<>();
            private final StringBuilder cell = new StringBuilder();
            private boolean inCell;

            @Override
            public void handleStartTag(HTML.Tag tag, MutableAttributeSet attrs, int position) {
                if (tag == HTML.Tag.TR) {
                    row.clear();
                } else if (tag == HTML.Tag.TD) {
                    cell.setLength(0);
                    inCell = true;
                }
            }

            @Override
            public void handleText(char[] data, int position) {
                if (inCell) {
                    cell.append(data);
                }
            }

            @Override
            public void handleEndTag(HTML.Tag tag, int position) {
                if (tag == HTML.Tag.TD) {
                    row.add(cell.toString().strip());
                    inCell = false;
                } else if (tag == HTML.Tag.TR && row.size() == 18) {
                    String stationId = "-".equals(row.get(1)) ? row.get(2) : row.get(1);
                    stationRows.putIfAbsent(stationId, List.copyOf(row));
                }
            }
        }, true);
        return stationRows;
    }

    private static String format(String location, List<String> row, LocalDateTime now) {
        Double danger = null;
        try {
            // Official alert, warning, and danger thresholds
            double alert = Double.parseDouble(row.get(14));
            double warning = Double.parseDouble(row.get(15));
            double dangerValue = Double.parseDouble(row.get(16));
            if (!(alert > 0 && alert < warning && warning < dangerValue && Double.isFinite(dangerValue))) {
                return unavailable(location, null);
            }
            danger = dangerValue;
            if (!"ON".equals(row.get(17))) {
                return unavailable(location, danger);
            }
            var age = Duration.between(LocalDateTime.parse(row.get(8), TIMESTAMP), now);
            double level = Double.parseDouble(row.get(9));
            // Avoid classifying old readings or missing value
            if (age.isNegative() || age.compareTo(Duration.ofHours(2)) > 0
                    || !Double.isFinite(level) || level <= 0) {
                return unavailable(location, danger);
            }
            String category = level >= danger ? "DANGEROUS"
                    : level >= warning ? "HIGH" : level >= alert ? "MED" : "LOW";
            return String.format(Locale.ROOT, "%s : %.2f m : %s : %.2f m", location, level, category, danger);
        } catch (IllegalArgumentException | java.time.DateTimeException failure) {
            return unavailable(location, danger);
        }
    }

    private static String unavailable(String location, Double danger) {
        String threshold = danger == null ? "UNAVAILABLE" : String.format(Locale.ROOT, "%.2f m", danger);
        return location + " : UNAVAILABLE : UNAVAILABLE : " + threshold;
    }
}
