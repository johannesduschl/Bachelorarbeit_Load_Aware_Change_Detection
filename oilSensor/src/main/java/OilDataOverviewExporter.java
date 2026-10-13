
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class OilDataOverviewExporter {

    private static final int SAMPLE_INTERVAL = 1000;

    public static void main(String[] args) throws IOException {

        InputStream input = OilDataOverviewExporter.class
                .getClassLoader()
                .getResourceAsStream("oil_data_cleaned.csv");

        if (input == null) {
            throw new IOException("oil_data_cleaned.csv nicht gefunden");
        }

        int validCount = 0;
        int exportedCount = 0;
        int skippedCount = 0;

        Path output = Path.of("oil_data_overview.txt");

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(input, StandardCharsets.UTF_8));
             BufferedWriter bw = Files.newBufferedWriter(
                     output, StandardCharsets.UTF_8)) {

            // CSV header überspringen
            br.readLine();

            // Header der Overview-Datei
            bw.write("timestamp oil_temperature");
            bw.newLine();

            String line;

            while ((line = br.readLine()) != null) {

                String[] split = line.trim().split(",");

                // CSV: index,timestamp,Oil_temperature
                if (split.length != 3) {
                    skippedCount++;
                    continue;
                }

                String timestamp = split[1].trim();
                double oilTemperature;

                try {
                    oilTemperature = Double.parseDouble(split[2].trim());
                } catch (NumberFormatException e) {
                    skippedCount++;
                    continue;
                }

                validCount++;

                // Ersten und jeden 1000. gültigen Wert exportieren
                if (validCount == 1 ||
                        validCount % SAMPLE_INTERVAL == 0) {

                    bw.write(timestamp + " " + oilTemperature);
                    bw.newLine();

                    exportedCount++;
                }
            }
        }

        System.out.println("Gültige Datensätze: " + validCount);
        System.out.println("Exportierte Messpunkte: " + exportedCount);
        System.out.println("Übersprungene Zeilen: " + skippedCount);
        System.out.println("Ausgabedatei: " + output.toAbsolutePath());
    }
}
