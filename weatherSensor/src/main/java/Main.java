import java.io.IOException;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        System.out.println("Weather sensor started.");
        WeatherSensor sensor = new WeatherSensor();
        sensor.start();

    }
}
