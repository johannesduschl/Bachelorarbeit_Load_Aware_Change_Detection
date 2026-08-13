package adaptive_change_detection;

import common.ChangeDetector;
import common.SensorData;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class AdaptiveCusumChangeDetector extends ChangeDetector {
    private double positiveCusum = 0;
    private double negativeCusum = 0;
    private final double alpha = 0.01;

    @Override
    public void sendSensorData(SensorData data) {
        resetInactivityTimer();

        double currentMean = mean;
        double currentStd = Math.max(std, 1e-9);
        double deviation = data.getValue() - currentMean;
        double K = 0.5 * currentStd;
        double H = 2.5 * currentStd;

        System.out.println("Mean = " + currentMean + ", Std = " + currentStd + ", Deviation = " + deviation);

        positiveCusum = Math.max(0, positiveCusum + deviation - K);
        negativeCusum = Math.max(0, negativeCusum - deviation - K);

        boolean changeDetected = positiveCusum > H || negativeCusum > H;
        benchmark.storeData(data, changeDetected);

        if (changeDetected) {
            receiverClient.send(data);
            positiveCusum = 0;
            negativeCusum = 0;
        }

        double newMean = (1 - alpha) * currentMean + alpha * data.getValue();
        double newVariance = (1 - alpha) * currentStd * currentStd + alpha * Math.pow(data.getValue() - currentMean, 2);
        mean = newMean;
        std = Math.max(Math.sqrt(newVariance), 1e-9);
    }
}