import CUSUM.CusumChangeDetector;
import common.ChangeDetector;
import weatherData.WeatherDataService;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        System.out.println("ChangeDetector started.");
        try {
            createWeatherServer(new CusumChangeDetector());
        } catch (Exception e) {
            System.out.println("Error starting ChangeDetector server: " + e.getMessage());
        }
    }

    private static void createWeatherServer(ChangeDetector changeDetector) throws IOException, InterruptedException {
        Server server = ServerBuilder
                .forPort(50051)
                .addService(new WeatherDataService(changeDetector))
                .build();

        server.start();

        System.out.println("ChangeDetector listening on 50051");

        server.awaitTermination();
    }
}
