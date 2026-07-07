import lombok.NoArgsConstructor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@NoArgsConstructor
public class WeatherDataLoader {

    private final DateTimeFormatter formatter = new DateTimeFormatterBuilder()
            .appendPattern("yyyy-MM-dd HH:mm:ss")
            .optionalStart()
            .appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true)
            .optionalEnd()
            .toFormatter();

    public List<WeatherData> loadWeatherData(int size) throws IOException {

        InputStream input = getClass()
                .getClassLoader()
                .getResourceAsStream("data.txt");

        if (input == null){
            System.out.println("Input was null");
            return null;
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(input))) {
            List<WeatherData> data = new ArrayList<>();
            String line;
            int count = 0;

            while ((line = br.readLine()) != null && count < size) {
                String[] split = line.split(" ");

                LocalDateTime timestamp = LocalDateTime.parse(split[0] + " " + split[1], formatter);
                double temperature = Double.parseDouble(split[5]);
                WeatherData weatherData = new WeatherData(timestamp, temperature);
                data.add(weatherData);
                count++;
            }

            sortWeatherDataByTimestamp(data);

            return data;
        }
    }

    private void sortWeatherDataByTimestamp(List<WeatherData> data){
        data.sort(Comparator.comparing(w -> w.timestamp));
    }
}
