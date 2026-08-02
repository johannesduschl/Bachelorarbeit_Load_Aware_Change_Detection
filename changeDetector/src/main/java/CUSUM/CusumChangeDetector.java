package CUSUM;

import common.SensorData;

public class CusumChangeDetector {

    double globalMean = 0;

    public void setGlobalMean(double globalMean){
        System.out.println("Global mean received: " + globalMean);
        this.globalMean = globalMean;
    }

    public void sendSensorData(SensorData data){
        System.out.println("Received data: " + data.getValue() + " at " + data.getTimestamp());
    }
}
