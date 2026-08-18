public class Main {

    public static void main(String[] args) {
        WeatherSensor sensor = new WeatherSensor(10_000, 10);
        sensor.start();
    }
}
