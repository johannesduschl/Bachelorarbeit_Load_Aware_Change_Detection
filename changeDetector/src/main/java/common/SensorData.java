package common;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@AllArgsConstructor
@Data
public class SensorData {
    LocalDateTime timestamp;
    double value;
}
