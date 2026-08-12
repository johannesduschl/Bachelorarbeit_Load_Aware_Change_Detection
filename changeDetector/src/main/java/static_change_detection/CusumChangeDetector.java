package static_change_detection;

import common.ChangeDetector;
import common.SensorData;

public class CusumChangeDetector extends ChangeDetector {

    private double positiveCusum = 0;
    private double negativeCusum = 0;

    @Override
    public void sendSensorData(SensorData data) {
        double deviation = data.getValue() - mean;

        final double K = 0.5 * std;
        final double H = 5.0 * std;

        positiveCusum = Math.max(0, positiveCusum + deviation - K);
        negativeCusum = Math.max(0, negativeCusum - deviation - K);

        if (positiveCusum > H || negativeCusum > H) {
            System.out.println("Change detected: " + data.getValue());

            benchmark.storeData(data, true);
            receiverClient.send(data);

            positiveCusum = 0;
            negativeCusum = 0;
        } else {
            benchmark.storeData(data, false);
            System.out.println("No change detected: " + data.getValue());
        }
    }
}