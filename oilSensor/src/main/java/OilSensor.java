import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import lombok.AllArgsConstructor;
import weather.grpc.*;

import java.time.ZoneOffset;
import java.util.List;

@AllArgsConstructor
public class OilSensor {

    private static final OilDataLoader oilDataLoader = new OilDataLoader();
    private static final int DATA_SIZE = 1_000_000;
    private static final int MS_INTERVAL = 10;

    public static void main(String[] args) {
        try {
            System.out.println("OIL SENSOR STARTED");

            List<OilData> data = oilDataLoader.loadOilData(DATA_SIZE);
            double globalMean = oilDataLoader.getGlobalMean();
            double globalSigma = oilDataLoader.getGlobalSigma();

            System.out.println("Global mean calculated: " + globalMean);
            System.out.println("Global sigma calculated: " + globalSigma);

            Thread.sleep(3000);
            sendAllData(data, globalMean, globalSigma);
        } catch (Exception e) {
            System.err.println("Error in oil sensor: " + e.getMessage());
        }
    }

    private static void sendAllData(List<OilData> data, double globalMean, double globalSigma) {
        System.out.println("Sending oil data to change detector...");

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

    private static void sendGlobalMean(ChangeDetectorServiceGrpc.ChangeDetectorServiceBlockingStub stub, double globalMean) {
        GlobalMeanRequest request = GlobalMeanRequest.newBuilder().setGlobalMean(globalMean).build();
        GlobalMeanResponse response = stub.sendGlobalMean(request);
        System.out.println("Global mean acknowledged: " + response.getReceived());
    }

    private static void sendGlobalSigma(ChangeDetectorServiceGrpc.ChangeDetectorServiceBlockingStub stub, double globalSigma) {
        GlobalSigmaRequest request = GlobalSigmaRequest.newBuilder().setGlobalSigma(globalSigma).build();
        GlobalSigmaResponse response = stub.sendGlobalSigma(request);
        System.out.println("Global sigma acknowledged: " + response.getReceived());
    }

    private static void sendData(ChangeDetectorServiceGrpc.ChangeDetectorServiceStub stub, List<OilData> data) throws InterruptedException {
        StreamObserver<WeatherDataRequest> sender = stub.sendWeatherData(new StreamObserver<>() {
            @Override
            public void onNext(WeatherDataResponse response) {
                System.out.println("Oil data acknowledged: " + response.getReceived());
            }

            @Override
            public void onError(Throwable t) {
                System.err.println("Error sending oil data: " + t.getMessage());
            }

            @Override
            public void onCompleted() {
                System.out.println("Server closed stream");
            }
        });

        for (OilData entry : data) {
            WeatherDataRequest request = WeatherDataRequest.newBuilder().setTimestamp(entry.getTimestamp().atZone(ZoneOffset.UTC).toEpochSecond()).setTemperature(entry.getOilTemperature()).build();

            System.out.println("Sending oil data: Timestamp: " + entry.getTimestamp() + ", Oil temperature: " + entry.getOilTemperature());

            sender.onNext(request);
            Thread.sleep(MS_INTERVAL);
        }

        sender.onCompleted();
    }
}