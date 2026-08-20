package change_detection_approaches;

import common.ChangeDetectionBenchmark;
import common.ReceiverClient;
import common.SensorData;
import lombok.Getter;
import lombok.Setter;

import java.util.Timer;
import java.util.TimerTask;

public abstract class ChangeDetector {
    @Getter
    protected double globalMean = 0;
    @Setter @Getter
    protected double globalStd = 0;

    protected ChangeDetectionBenchmark benchmark = new ChangeDetectionBenchmark();

    protected ReceiverClient receiverClient = new ReceiverClient("receiver", 50051);

    private Timer inactivityTimer = new Timer(true);
    @Setter
    private Runnable inactivityCallback;

    public double getK(){
        return 0.25 * globalStd;
    }

    public double getH(){
        return 2.5 * globalStd;
    }

    public void setGlobalMean(double globalMean){
        this.globalMean = globalMean;
    }

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
