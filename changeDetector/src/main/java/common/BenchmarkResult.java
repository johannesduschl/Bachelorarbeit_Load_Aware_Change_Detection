package common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class BenchmarkResult {

    /**
     * Number of events that were sent from the change detection to the receiver.
     */
    private final int sentCount;

    /**
     * Number of events that were discarded during the change detection process and not sent to the receiver.
     */
    private final int discardedCount;

    /**
     * Percentage of all events that were sent to the receiver.
     */
    private final double transmissionRate;

    /**
     * Mean absolute reconstruction error of every original value with the last sent value.
     */
    private final double mae;

    /**
     * Root mean squared reconstruction error of every original value with the last sent value.
     * Larger reconstruction errors have a greater influence.
     */
    private final double rmse;

    /**
     * Maximum absolute reconstruction error observed among discarded events.
     */
    private final double maxError;

    /**
     * MAE multiplied with the transmissionRate
     */
    private final double maeTimesTransmissionRate;
}