package common;

import lombok.Getter;
import lombok.Setter;

public abstract class ChangeDetector {
    @Setter @Getter
    protected double mean = 0;
    @Setter @Getter
    protected double std = 0;

    protected ChangeDetectionBenchmark benchmark = new ChangeDetectionBenchmark();

    protected ReceiverClient receiverClient = new ReceiverClient("receiver", 50051);

    public void printBenchmarkResults(){
        System.out.println(benchmark.calculateResults());
    }

    public abstract void sendSensorData(SensorData entry);
}
