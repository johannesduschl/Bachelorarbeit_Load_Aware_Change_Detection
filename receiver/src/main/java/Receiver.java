import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import weather.grpc.Empty;
import weather.grpc.ReceiverServiceGrpc;
import weather.grpc.WeatherDataRequest;
import weather.grpc.WeatherDataResponse;
import weather.grpc.UtilizationResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;

public class Receiver {
    private static final List<Double> valueHistory = new ArrayList<>();
    private static volatile long lastReceived = System.currentTimeMillis();
    private static final OperatingSystemMXBean osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

    public static void main(String[] args) throws Exception {
        LoadGenerator loadGenerator = new LoadGenerator(0, 10, 10);
        loadGenerator.start();

        Server server = ServerBuilder.forPort(50051).addService(new ReceiverServiceImpl()).build().start();
        System.out.println("Receiver started on port 50051");

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            if (System.currentTimeMillis() - lastReceived >= 60000) {
                System.out.println("No data received for 60 seconds. Received values: " + valueHistory.size());
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

        @Override
        public void getUtilization(Empty request, StreamObserver<UtilizationResponse> responseObserver) {
            double utilization = osBean.getCpuLoad();
            responseObserver.onNext(UtilizationResponse.newBuilder().setUtilization(utilization).build());
            responseObserver.onCompleted();
        }
    }
}