import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;

public class OilDataCleaner {

    public void createCleanedData(int size) throws IOException {

        InputStream input = getClass()
                .getClassLoader()
                .getResourceAsStream("MetroPT3(AirCompressor).csv");

        if (input == null) {
            System.out.println("Input was null");
            return;
        }

        Path output = Path.of("src", "main", "resources", "oil_data_cleaned.csv");
        Files.createDirectories(output.getParent());

        try (BufferedReader br = new BufferedReader(new InputStreamReader(input));
             BufferedWriter bw = Files.newBufferedWriter(output)) {

            br.readLine();

            bw.write("index,timestamp,Oil_temperature");
            bw.newLine();

            String line;
            int count = 0;

            while ((line = br.readLine()) != null && count < size) {
                String[] split = line.split(",");

                if (split.length < 8) {
                    continue;
                }

                bw.write(split[0] + "," + split[1] + "," + split[7]);
                bw.newLine();

                count++;
            }

            System.out.println("size: " + count);
        }
    }

    public static void main(String[] args) {
        try {
            new OilDataCleaner().createCleanedData(1_000_000);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
