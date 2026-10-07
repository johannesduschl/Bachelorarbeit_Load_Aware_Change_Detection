import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@AllArgsConstructor
@Data
public class OilData {
    LocalDateTime timestamp;
    double oilTemperature;
}