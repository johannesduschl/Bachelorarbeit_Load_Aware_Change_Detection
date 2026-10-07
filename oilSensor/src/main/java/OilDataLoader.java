import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@NoArgsConstructor
public class OilDataLoader {

    @Getter
    private double globalMean;

    @Getter
    private double globalSigma;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public List<OilData> loadOilData(int size) throws IOException {
        InputStream input = getClass().getClassLoader().getResourceAsStream("oil_data_cleaned.csv");

        if (input == null) {
            System.err.println("Oil data input was null");
            return null;
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {
            List<OilData> data = new ArrayList<>();
            String line;
            int count = 0;
            double oilTemperatureSum = 0;

            br.readLine();

            while ((line = br.readLine()) != null && count < size) {
                try {
                    String[] split = line.trim().split(",");

                    LocalDateTime timestamp = LocalDateTime.parse(split[1].trim(), formatter);
                    double oilTemperature = Double.parseDouble(split[2].trim());

                    data.add(new OilData(timestamp, oilTemperature));
                    oilTemperatureSum += oilTemperature;
                    count++;
                } catch (Exception e) {
                    System.err.println("Error parsing line: " + line);
                    System.err.println(e.getMessage());
                }
            }

            System.out.println("Oil data size: " + count);

            sortOilDataByTimestamp(data);

            if (count > 0) {
                this.globalMean = oilTemperatureSum / count;
                this.globalSigma = computeStdDev(data, this.globalMean);
            }

            return data;
        }
    }

    private double computeStdDev(List<OilData> data, double mean) {
        int n = data.size();

        if (n <= 1) {
            return 0;
        }

        double sumSquaredDiffs = 0;

        for (OilData oil : data) {
            double diff = oil.getOilTemperature() - mean;
            sumSquaredDiffs += diff * diff;
        }

        return Math.sqrt(sumSquaredDiffs / (n - 1));
    }

    private void sortOilDataByTimestamp(List<OilData> data) {
        data.sort(Comparator.comparing(OilData::getTimestamp));
    }
}