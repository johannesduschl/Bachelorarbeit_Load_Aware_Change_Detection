import java.io.IOException;
import java.util.List;

public class WeatherSensor {

    private static final WeatherDataLoader weatherDataLoader = new WeatherDataLoader();


    public static void main(String[] args) {

        System.out.println("Weather sensor started.");

        try {

            List<WeatherData> data = weatherDataLoader.loadWeatherData(1000);
            for (WeatherData entry : data){
                System.out.println(entry);
            }

        } catch (IOException e) {
            System.err.println("Error in weather sensor: " + e.getMessage());
        }

    }
}