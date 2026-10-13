
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;

public class WeatherDataCleaner {

    public void createCleanedData() throws IOException {

        InputStream input = getClass()
                .getClassLoader()
                .getResourceAsStream("data_cleaned.txt");

        if (input == null) {
            System.out.println("Input was null");
            return;
        }

        Path output = Path.of("src", "main", "resources", "data_cleaned.txt");
        Files.createDirectories(output.getParent());

        try (BufferedReader br = new BufferedReader(new InputStreamReader(input));
             BufferedWriter bw = Files.newBufferedWriter(output)) {

            String line;
            int count = 0;

            while ((line = br.readLine()) != null) {

                String[] split = line.trim().split("\\s+");

                if (split.length < 6) {
                    continue;
                }

                double temperature;

                try {
                    temperature = Double.parseDouble(split[5]);
                } catch (NumberFormatException e) {
                    continue;
                }

                if (temperature < 0 || temperature > 80) {
                    continue;
                }

                bw.write(line);
                bw.newLine();
                count++;
            }

            System.out.println("size: " + count);
            System.out.println("Saved to: " + output.toAbsolutePath());
        }
    }

    public static void main(String[] args) {
        try {
            new WeatherDataCleaner().createCleanedData();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
