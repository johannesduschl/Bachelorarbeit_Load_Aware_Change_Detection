package change_detection_approaches;

import common.SensorData;

public class StaticCusumChangeDetector extends ChangeDetector {
    private double positiveCusum = 0;
    private double negativeCusum = 0;

    @Override
    public void sendSensorData(SensorData data) {
        long startTime = System.nanoTime();
        resetInactivityTimer();

        double value = data.getValue();
        double deviation = value - globalMean;
        final double K = 0.25 * globalStd;
        final double H = 2.5 * globalStd;

        positiveCusum = Math.max(0, positiveCusum + deviation - K);
        negativeCusum = Math.max(0, negativeCusum - deviation - K);

        boolean positiveChange = positiveCusum > H;
        boolean negativeChange = negativeCusum > H;
        boolean changeDetected = positiveChange || negativeChange;

        if (changeDetected) {
            receiverClient.send(data);
            positiveCusum = 0;
            negativeCusum = 0;
        }

        long latency = System.nanoTime() - startTime;
        benchmark.storeData(data, changeDetected, latency);
    }
}