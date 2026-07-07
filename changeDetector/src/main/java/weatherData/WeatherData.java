package weatherData;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@AllArgsConstructor
@Data
public class WeatherData {
    LocalDateTime timestamp;
    double temperature;
}
