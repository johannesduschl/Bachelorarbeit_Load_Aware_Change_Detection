public class Main {

    public static void main(String[] args) {
        WeatherSensor sensor = new WeatherSensor(1_000_000, 10);
        sensor.start();
    }
}
