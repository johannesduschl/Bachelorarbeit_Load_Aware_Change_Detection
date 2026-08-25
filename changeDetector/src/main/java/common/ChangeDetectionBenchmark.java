package common;

import java.util.ArrayList;
import java.util.List;

public class ChangeDetectionBenchmark {
    private final List<SensorData> allData = new ArrayList<>();
    private final List<Boolean> sentFlags = new ArrayList<>();
    private final List<Long> latencies = new ArrayList<>();

    public void storeData(SensorData data, boolean wasSent, long latency) {
        allData.add(data);
        sentFlags.add(wasSent);
        latencies.add(latency);
    }

    public BenchmarkResult calculateResults() {
        if (allData.isEmpty()) {
            throw new RuntimeException("allData was empty in Benchmark.");
        }

        int sentCount = 0;
        int discardedCount = 0;
        double absoluteErrorSum = 0;
        double squaredErrorSum = 0;
        double maxError = 0;
        double lastSentValue = allData.getFirst().getValue();

        for (int i = 0; i < allData.size(); i++) {
            SensorData data = allData.get(i);
            double actualValue = data.getValue();
            boolean wasSent = sentFlags.get(i);

            if (wasSent) {
                sentCount++;
                lastSentValue = actualValue;
            } else {
                discardedCount++;

                double error = actualValue - lastSentValue;
                double absoluteError = Math.abs(error);

                absoluteErrorSum += absoluteError;
                squaredErrorSum += error * error;
                maxError = Math.max(maxError, absoluteError);
            }
        }

        int totalCount = allData.size();
        double transmissionRate = (double) sentCount / totalCount;
        double mae = discardedCount > 0 ? absoluteErrorSum / discardedCount : 0;
        double rmse = discardedCount > 0 ? Math.sqrt(squaredErrorSum / discardedCount) : 0;
        double maeTRMetric = 0.5 * transmissionRate + 0.5 * (mae / 24.688743236106145);

        List<Long> sortedLatencies = new ArrayList<>(latencies);
        sortedLatencies.sort(Long::compareTo);
        int percentileIndex = (int) Math.ceil(0.99 * sortedLatencies.size()) - 1;
        long latency = sortedLatencies.get(percentileIndex);

        return new BenchmarkResult(sentCount, discardedCount, transmissionRate, mae, rmse, maxError, maeTRMetric, latency);
    }
}