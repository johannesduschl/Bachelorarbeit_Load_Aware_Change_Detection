package change_detection_approaches;

import common.ChangeDetector;
import common.SensorData;

public class LoadAwareAdaptiveChangeDetector extends ChangeDetector {

    double utilizationReceiver = 0.5;

    @Override
    public void sendSensorData(SensorData entry) {

    }
}
