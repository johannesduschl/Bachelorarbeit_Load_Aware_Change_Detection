package weatherData;

import CUSUM.CusumChangeDetector;
import common.SensorData;
import io.grpc.stub.StreamObserver;
import weather.grpc.ChangeDetectorServiceGrpc;
import weather.grpc.GlobalMeanRequest;
import weather.grpc.GlobalMeanResponse;
import weather.grpc.WeatherDataRequest;
import weather.grpc.WeatherDataResponse;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class WeatherDataService extends ChangeDetectorServiceGrpc.ChangeDetectorServiceImplBase {

    private final CusumChangeDetector cusumChangeDetector = new CusumChangeDetector();

    @Override
    public void sendGlobalMean(GlobalMeanRequest request, StreamObserver<GlobalMeanResponse> responseObserver) {
        cusumChangeDetector.setGlobalMean(request.getGlobalMean());

        responseObserver.onNext(GlobalMeanResponse.newBuilder().setReceived(true).build());
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<WeatherDataRequest> sendWeatherData(StreamObserver<WeatherDataResponse> responseObserver) {
        return new StreamObserver<>() {

            @Override
            public void onNext(WeatherDataRequest request) {
                LocalDateTime timestamp = LocalDateTime.ofInstant(Instant.ofEpochSecond(request.getTimestamp()), ZoneOffset.UTC);
                SensorData data = new SensorData(timestamp, request.getTemperature());
                cusumChangeDetector.sendSensorData(data);
            }

            @Override
            public void onError(Throwable throwable) {
                System.err.println("Error receiving weather data: " + throwable.getMessage());
            }

            @Override
            public void onCompleted() {
                responseObserver.onNext(WeatherDataResponse.newBuilder().setReceived(true).build());
                responseObserver.onCompleted();
            }
        };
    }
}