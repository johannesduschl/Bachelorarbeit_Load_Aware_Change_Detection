import weatherData.WeatherDataService;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        System.out.println("ChangeDetector started.");
        try {
            createWeatherServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void createWeatherServer() throws IOException, InterruptedException {
        Server server = ServerBuilder
                .forPort(50051)
                .addService(new WeatherDataService())
                .build();

        server.start();

        System.out.println("ChangeDetector listening on 50051");

        server.awaitTermination();
    }
}
