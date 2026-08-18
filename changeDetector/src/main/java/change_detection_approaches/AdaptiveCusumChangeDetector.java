package change_detection_approaches;

import common.ChangeDetector;
import common.SensorData;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class AdaptiveCusumChangeDetector extends ChangeDetector {

    private double positiveCusum = 0;
    private double negativeCusum = 0;
    private final double alpha = 0.05;
    private double runningMean;

    @Override
    public void setGlobalMean(double globalMean){
        this.globalMean = globalMean;
        this.runningMean = globalMean;
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
        System.out.printf("Value = %f; Mean = %f; Deviation = %f; %s}%n", value, runningMean, deviation, changeStatus);

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