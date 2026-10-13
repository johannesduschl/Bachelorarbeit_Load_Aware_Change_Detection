import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class WeatherDataOverviewExporter {

    private static final int SAMPLE_INTERVAL = 1000;

    public static void main(String[] args) throws IOException {
        InputStream input = WeatherDataOverviewExporter.class.getClassLoader().getResourceAsStream("data_cleaned.txt");
        if (input == null) throw new IOException("data_cleaned.txt nicht gefunden");

        int validCount = 0, exportedCount = 0, skippedCount = 0;

        try (BufferedReader br = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8)); BufferedWriter bw = Files.newBufferedWriter(Path.of("data_overview.txt"), StandardCharsets.UTF_8)) {
            bw.write("timestamp temperature");
            bw.newLine();

            String line;
            while ((line = br.readLine()) != null) {
                String[] split = line.trim().split("\\s+");
                if (split.length != 8) {
                    skippedCount++;
                    continue;
                }

                double temperature;
                try {
                    temperature = Double.parseDouble(split[5]);
                } catch (NumberFormatException e) {
                    skippedCount++;
                    continue;
                }

                validCount++;

                if (validCount == 1 || validCount % SAMPLE_INTERVAL == 0) {
                    bw.write(split[0] + " " + split[1] + " " + temperature);
                    bw.newLine();
                    exportedCount++;
                }
            }
        }

        System.out.println("Gültige Datensätze: " + validCount);
        System.out.println("Exportierte Messpunkte: " + exportedCount);
        System.out.println("Übersprungene Zeilen: " + skippedCount);
        System.out.println("Ausgabedatei: " + Path.of("data_overview.txt").toAbsolutePath());
    }
}
