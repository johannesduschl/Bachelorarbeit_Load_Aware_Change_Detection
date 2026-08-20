package weatherData;

import change_detection_approaches.ChangeDetector;
import common.SensorData;
import io.grpc.stub.StreamObserver;
import lombok.AllArgsConstructor;
import weather.grpc.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@AllArgsConstructor
public class DataService extends ChangeDetectorServiceGrpc.ChangeDetectorServiceImplBase {

    private final ChangeDetector changeDetector;

    @Override
    public void sendGlobalMean(GlobalMeanRequest request, StreamObserver<GlobalMeanResponse> responseObserver) {
        System.out.println("Received global mean: " + request.getGlobalMean());
        changeDetector.setGlobalMean(request.getGlobalMean());
        responseObserver.onNext(GlobalMeanResponse.newBuilder().setReceived(true).build());
        responseObserver.onCompleted();
    }


    @Override
    public void sendGlobalSigma(GlobalSigmaRequest request, StreamObserver<GlobalSigmaResponse> responseObserver) {
        System.out.println("Received global sigma: " + request.getGlobalSigma());
        changeDetector.setGlobalStd(request.getGlobalSigma());
        responseObserver.onNext(GlobalSigmaResponse.newBuilder().setReceived(true).build());
        responseObserver.onCompleted();
    }


    @Override
    public StreamObserver<WeatherDataRequest> sendWeatherData(StreamObserver<WeatherDataResponse> responseObserver) {
        return new StreamObserver<>() {

            @Override
            public void onNext(WeatherDataRequest request) {
                LocalDateTime timestamp = LocalDateTime.ofInstant(Instant.ofEpochSecond(request.getTimestamp()), ZoneOffset.UTC);
                SensorData data = new SensorData(timestamp, request.getTemperature());
                changeDetector.sendSensorData(data);
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