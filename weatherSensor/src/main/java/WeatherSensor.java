import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import lombok.AllArgsConstructor;
import weather.grpc.*;

import java.time.ZoneOffset;
import java.util.List;

@AllArgsConstructor
public class WeatherSensor {

    private static final WeatherDataLoader weatherDataLoader = new WeatherDataLoader();
    private final int DATA_SIZE;
    private final int MS_INTERVAL;

    public void start() {
        try {

            System.out.println("WEATHER SENSOR STARTED");

            List<WeatherData> data = weatherDataLoader.loadWeatherData(DATA_SIZE);
            double globalMean = weatherDataLoader.getGlobalMean();
            double globalSigma = weatherDataLoader.getGlobalSigma();
            System.out.println("Global mean calculated: " + globalMean);
            System.out.println("Global sigma calculated: " + globalSigma);

            Thread.sleep(3000);
            sendAllData(data, globalMean, globalSigma);

        } catch (Exception e) {
            System.err.println("Error in weather sensor: " + e.getMessage());
        }
    }

    private void sendAllData(List<WeatherData> data, double globalMean, double globalSigma) {
        System.out.println("Sending data to change detector...");
        ManagedChannel channel = ManagedChannelBuilder.forAddress("changeDetector", 50051).usePlaintext().build();

        ChangeDetectorServiceGrpc.ChangeDetectorServiceBlockingStub blockingStub = ChangeDetectorServiceGrpc.newBlockingStub(channel);
        ChangeDetectorServiceGrpc.ChangeDetectorServiceStub asyncStub = ChangeDetectorServiceGrpc.newStub(channel);

        try {
            sendGlobalMean(blockingStub, globalMean);
            sendGlobalSigma(blockingStub, globalSigma);
            sendData(asyncStub, data);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Sending interrupted: " + e.getMessage());
        } finally {
            channel.shutdown();
        }
    }

    private void sendGlobalMean(ChangeDetectorServiceGrpc.ChangeDetectorServiceBlockingStub stub, double globalMean) {
        GlobalMeanRequest request = GlobalMeanRequest.newBuilder().setGlobalMean(globalMean).build();
        GlobalMeanResponse response = stub.sendGlobalMean(request);
        System.out.println("Global mean acknowledged: " + response.getReceived());
    }


    private void sendGlobalSigma(ChangeDetectorServiceGrpc.ChangeDetectorServiceBlockingStub stub, double globalSigma) {
        GlobalSigmaRequest request = GlobalSigmaRequest.newBuilder().setGlobalSigma(globalSigma).build();
        GlobalSigmaResponse response = stub.sendGlobalSigma(request);
        System.out.println("Global sigma acknowledged: " + response.getReceived());
    }

    private void sendData(ChangeDetectorServiceGrpc.ChangeDetectorServiceStub stub, List<WeatherData> data) throws InterruptedException {
        StreamObserver<WeatherDataRequest> sender = stub.sendWeatherData(new StreamObserver<>() {

            @Override
            public void onNext(WeatherDataResponse response) {
                System.out.println("Weather data acknowledged: " + response.getReceived());
            }

            @Override
            public void onError(Throwable t) {
                System.err.println("Error sending weather data: " + t.getMessage());
            }

            @Override
            public void onCompleted() {
                System.out.println("Server closed stream");
            }
        });

        for (WeatherData entry : data) {
            WeatherDataRequest request = WeatherDataRequest.newBuilder()
                    .setTimestamp(entry.getTimestamp().atZone(ZoneOffset.UTC).toEpochSecond())
                    .setTemperature(entry.getTemperature())
                    .build();

            System.out.println("Sending data: Timestamp: " + entry.getTimestamp() + ", Temperature: " + entry.getTemperature());
            sender.onNext(request);
            Thread.sleep(MS_INTERVAL);
        }

        sender.onCompleted();
    }
}