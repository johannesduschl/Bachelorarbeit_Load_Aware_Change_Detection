import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import weather.grpc.ChangeDetectorServiceGrpc;
import weather.grpc.WeatherDataRequest;
import weather.grpc.WeatherDataResponse;

import java.io.IOException;
import java.time.ZoneOffset;
import java.util.List;

@NoArgsConstructor
public class WeatherSensor {

    private static final WeatherDataLoader weatherDataLoader = new WeatherDataLoader();


    public void start(){

        try {
            List<WeatherData> data = weatherDataLoader.loadWeatherData(10000);
            sendData(data, 10);

        } catch (Exception e) {
            System.err.println("Error in weather sensor: " + e.getMessage());
        }
    }


    private void sendData(List<WeatherData> data, int msInterval) throws InterruptedException {

        ManagedChannel channel = ManagedChannelBuilder.forAddress("changeDetector", 50051)
                .usePlaintext()
                .build();

        ChangeDetectorServiceGrpc.ChangeDetectorServiceStub stub = ChangeDetectorServiceGrpc.newStub(channel);

        StreamObserver<WeatherDataRequest> sender = stub.sendWeatherData(new StreamObserver<>() {

            @Override
            public void onNext(WeatherDataResponse response) {}

            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
            }

            @Override
            public void onCompleted() {
                System.out.println("Server closed stream");
            }
        });

        for (WeatherData entry : data) {

            System.out.println("Sending data: " + entry);

            WeatherDataRequest request = WeatherDataRequest.newBuilder()
                            .setTimestamp(
                                    entry.getTimestamp()
                                            .atZone(ZoneOffset.UTC)
                                            .toEpochSecond())
                            .setTemperature(entry.getTemperature())
                            .build();
            sender.onNext(request);

            Thread.sleep(msInterval);
        }

        sender.onCompleted();
        channel.shutdown();
    }
}