import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import weather.grpc.ReceiverServiceGrpc;
import weather.grpc.WeatherDataRequest;
import weather.grpc.WeatherDataResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Receiver {
    private static final List<Double> valueHistory = new ArrayList<>();
    private static volatile long lastReceived = System.currentTimeMillis();

    public static void main(String[] args) throws Exception {
        Server server = ServerBuilder.forPort(50051).addService(new ReceiverServiceImpl()).build().start();
        System.out.println("Receiver started on port 50051");

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            if (System.currentTimeMillis() - lastReceived >= 50000) {
                System.out.println("No data received for 50 seconds. Received values: " + valueHistory.size());
                server.shutdown();
                scheduler.shutdown();
            }
        }, 10, 1, TimeUnit.SECONDS);

        server.awaitTermination();
    }

    static class ReceiverServiceImpl extends ReceiverServiceGrpc.ReceiverServiceImplBase {
        @Override
        public StreamObserver<WeatherDataRequest> sendWeatherData(StreamObserver<WeatherDataResponse> responseObserver) {
            return new StreamObserver<>() {
                @Override
                public void onNext(WeatherDataRequest request) {
                    valueHistory.add(request.getTemperature());
                    lastReceived = System.currentTimeMillis();
                    System.out.println("Timestamp: " + request.getTimestamp() + ", Temperature: " + request.getTemperature());
                }

                @Override
                public void onError(Throwable throwable) {
                    throwable.printStackTrace();
                }

                @Override
                public void onCompleted() {
                    responseObserver.onNext(WeatherDataResponse.newBuilder().setReceived(true).build());
                    responseObserver.onCompleted();
                }
            };
        }
    }
}