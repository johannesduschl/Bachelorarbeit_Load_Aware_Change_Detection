public class Main {

    private static final boolean IS_ACTIVE = false;
    private static final int DATA_SIZE = 1_000_000;

    public static void main(String[] args) {

        if (IS_ACTIVE){
            WeatherSensor sensor = new WeatherSensor(DATA_SIZE, 10);
            sensor.start();
        }

    }
}
