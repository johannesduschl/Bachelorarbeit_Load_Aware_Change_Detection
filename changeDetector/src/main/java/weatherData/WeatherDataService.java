package weatherData;

import io.grpc.stub.StreamObserver;
import weather.grpc.ChangeDetectorServiceGrpc;
import weather.grpc.WeatherDataRequest;
import weather.grpc.WeatherDataResponse;
import CUSUM.CusumChangeDetector;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class WeatherDataService extends ChangeDetectorServiceGrpc.ChangeDetectorServiceImplBase {

    CusumChangeDetector cusumChangeDetector = new CusumChangeDetector();

    @Override
    public StreamObserver<WeatherDataRequest> sendWeatherData(StreamObserver<WeatherDataResponse> responseObserver) {

        return new StreamObserver<>() {
            @Override
            public void onNext(WeatherDataRequest request) {
                LocalDateTime timestamp = LocalDateTime.ofInstant(
                        Instant.ofEpochSecond(request.getTimestamp()),
                        ZoneOffset.UTC
                );
                WeatherData data = new WeatherData(timestamp, request.getTemperature());
                cusumChangeDetector.sendSensorData(data);
            }

            @Override
            public void onError(Throwable throwable) {
            }

            @Override
            public void onCompleted() {
                responseObserver.onNext(WeatherDataResponse.newBuilder()
                                .setReceived(true)
                                .build());

                responseObserver.onCompleted();
            }
        };
    }
}