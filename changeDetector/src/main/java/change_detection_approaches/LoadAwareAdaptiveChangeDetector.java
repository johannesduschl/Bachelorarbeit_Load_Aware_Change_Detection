package change_detection_approaches;

import common.ChangeDetector;
import common.SensorData;
import lombok.NoArgsConstructor;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class LoadAwareAdaptiveChangeDetector extends ChangeDetector {

    private double positiveCusum = 0;
    private double negativeCusum = 0;
    private final double alpha = 0.05;
    private double runningMean;

    private volatile double utilization;

    public LoadAwareAdaptiveChangeDetector() {
        ScheduledExecutorService utilizationScheduler = Executors.newSingleThreadScheduledExecutor();
        utilizationScheduler.scheduleAtFixedRate(() -> utilization = receiverClient.getReceiverUtilization(), 0, 1, TimeUnit.SECONDS);
    }

    @Override
    public void setGlobalMean(double globalMean){
        this.globalMean = globalMean;
        this.runningMean = globalMean;
    }

    @Override
    public double getK() {
        return super.getK() * Math.exp(Math.log(2) * utilization);
    }

    @Override
    public double getH() {
        return super.getH() * Math.exp(Math.log(2) * utilization);
    }

    @Override
    public void sendSensorData(SensorData data) {
        resetInactivityTimer();

        double value = data.getValue();
        double deviation = value - runningMean;
        double K = getK();
        double H = getH();

        positiveCusum = Math.max(0, positiveCusum + deviation - K);
        negativeCusum = Math.max(0, negativeCusum - deviation - K);

        boolean changeDetected = positiveCusum > H || negativeCusum > H;
        benchmark.storeData(data, changeDetected);

        String changeStatus = changeDetected ? "<--- Change Detected" : "";
        System.out.printf("Utilization = %f; Value = %f; Mean = %f; Deviation = %f; K = %f; H = %f; %s%n", utilization, value, runningMean, deviation, K, H, changeStatus);

        if (changeDetected) {
            receiverClient.send(data);
            positiveCusum = 0;
            negativeCusum = 0;
            runningMean = value;
        } else {
            this.runningMean = (1 - alpha) * runningMean + alpha * data.getValue();
        }

    }
}