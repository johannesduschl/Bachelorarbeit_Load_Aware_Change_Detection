package common;

import weather.grpc.Empty;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import weather.grpc.ReceiverServiceGrpc;
import weather.grpc.WeatherDataRequest;
import weather.grpc.WeatherDataResponse;

import java.time.ZoneOffset;

public class ReceiverClient {
    private final ManagedChannel channel;
    private final ReceiverServiceGrpc.ReceiverServiceStub stub;
    private final ReceiverServiceGrpc.ReceiverServiceBlockingStub blockingStub;;
    private StreamObserver<WeatherDataRequest> requestObserver;

    public ReceiverClient(String host, int port) {
        channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
        stub = ReceiverServiceGrpc.newStub(channel);
        blockingStub = ReceiverServiceGrpc.newBlockingStub(channel);
        openStream();
    }

    private void openStream() {
        requestObserver = stub.sendWeatherData(new StreamObserver<>() {
            @Override
            public void onNext(WeatherDataResponse response) {}

            @Override
            public void onError(Throwable throwable) {
                throwable.printStackTrace();
            }

            @Override
            public void onCompleted() {}
        });
    }

    public void send(SensorData data) {
        requestObserver.onNext(
                WeatherDataRequest.newBuilder()
                        .setTimestamp(data.getTimestamp().atZone(ZoneOffset.UTC).toEpochSecond())
                        .setTemperature(data.getValue())
                        .build()
        );
    }

    public double getReceiverUtilization() {
        return blockingStub.getUtilization(Empty.newBuilder().build()).getUtilization();
    }

    public void close() {
        requestObserver.onCompleted();
        channel.shutdown();
    }
}