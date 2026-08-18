import change_detection_approaches.AdaptiveCusumChangeDetector;
import common.ChangeDetector;
import change_detection_approaches.StaticCusumChangeDetector;
import weatherData.DataService;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class Main {

    static ChangeDetector changeDetector = new StaticCusumChangeDetector();

    public static void main(String[] args) {
        System.out.println("ChangeDetector started.");
        try {
            createWeatherServer(changeDetector);
        } catch (Exception e) {
            System.out.println("Error starting ChangeDetector server: " + e.getMessage());
        }
    }

    private static void createWeatherServer(ChangeDetector changeDetector) throws IOException, InterruptedException {

        Server server = ServerBuilder
                .forPort(50051)
                .addService(new DataService(changeDetector))
                .build();
        server.start();
        System.out.println("ChangeDetector listening on 50051");

        changeDetector.setInactivityCallback(() -> {
            System.out.println("No data received for 10 seconds.");
            changeDetector.printBenchmarkResults();
            server.shutdown();
        });

        server.awaitTermination();
    }
}
