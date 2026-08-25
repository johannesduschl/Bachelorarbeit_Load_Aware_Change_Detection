import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class LoadGenerator {
    private static final int MACHINE_ID = 1036;
    private static final long INTERVAL_MS = 10_000;
    private final List<Double> utilizationValues = new ArrayList<>();
    private final long startTime;

    public LoadGenerator() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream("server_usage.csv"), StandardCharsets.UTF_8))) {
            reader.readLine();
            List<String[]> rows = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] columns = line.split(",");
                if (Integer.parseInt(columns[1].trim()) == MACHINE_ID) rows.add(columns);
            }
            rows.sort(Comparator.comparing(row -> row[0]));
            for (String[] row : rows) utilizationValues.add(Double.parseDouble(row[2].trim()) / 100.0);
        } catch (Exception e) {
            throw new RuntimeException("Fehler beim Laden von server_usage.csv", e);
        }
        startTime = System.currentTimeMillis();
    }

    public double getUtilization() {
        long elapsed = System.currentTimeMillis() - startTime;
        int currentIndex = (int) ((elapsed / INTERVAL_MS) % utilizationValues.size());
        return utilizationValues.get(currentIndex);
    }
}