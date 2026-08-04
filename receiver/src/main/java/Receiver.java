import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import weather.grpc.ReceiverServiceGrpc;
import weather.grpc.WeatherDataRequest;
import weather.grpc.WeatherDataResponse;

public class Receiver {

    private static int receivedCounter = 0;

    public static void main(String[] args) throws Exception {
        Server server = ServerBuilder.forPort(50051).addService(new ReceiverServiceImpl()).build().start();
        System.out.println("Receiver started on port 50051");
        server.awaitTermination();
    }

    static class ReceiverServiceImpl extends ReceiverServiceGrpc.ReceiverServiceImplBase {
        @Override
        public StreamObserver<WeatherDataRequest> sendWeatherData(StreamObserver<WeatherDataResponse> responseObserver) {
            return new StreamObserver<>() {
                @Override
                public void onNext(WeatherDataRequest request) {
                    receivedCounter++;
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