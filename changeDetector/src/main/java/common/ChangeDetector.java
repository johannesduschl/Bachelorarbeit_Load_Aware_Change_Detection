package common;

import lombok.Getter;
import lombok.Setter;

import java.util.Timer;
import java.util.TimerTask;

public abstract class ChangeDetector {
    @Setter @Getter
    protected double mean = 0;
    @Setter @Getter
    protected double std = 0;

    protected ChangeDetectionBenchmark benchmark = new ChangeDetectionBenchmark();

    protected ReceiverClient receiverClient = new ReceiverClient("receiver", 50051);

    private Timer inactivityTimer = new Timer(true);
    @Setter
    private Runnable inactivityCallback;

    public void printBenchmarkResults(){
        System.out.println(benchmark.calculateResults());
    }

    public abstract void sendSensorData(SensorData entry);


    protected void resetInactivityTimer() {
        inactivityTimer.cancel();
        inactivityTimer = new Timer(true);
        inactivityTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (inactivityCallback != null) {
                    inactivityCallback.run();
                }
            }
        }, 10000);
    }

}
