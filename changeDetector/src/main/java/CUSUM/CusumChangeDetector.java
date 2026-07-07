package CUSUM;

import weatherData.WeatherData;

public class CusumChangeDetector {

    public void sendSensorData(WeatherData data){
        System.out.println("Received data: " + data.getTemperature() + " at " + data.getTimestamp());
    }
}
