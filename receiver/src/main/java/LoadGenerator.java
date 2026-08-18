import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class LoadGenerator {
    private final Random random = new Random();
    private final int minThreads;
    private final int maxThreads;
    private final int durationSeconds;

    public LoadGenerator(int minThreads, int maxThreads, int durationSeconds) {
        this.minThreads = minThreads;
        this.maxThreads = maxThreads;
        this.durationSeconds = durationSeconds;
    }

    public void start() {
        Thread scheduler = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                int threadCount = minThreads + random.nextInt(maxThreads - minThreads + 1);
                ExecutorService executor = Executors.newFixedThreadPool(threadCount);

                System.out.println("[LOAD] Starting " + threadCount + " CPU threads for " + durationSeconds + " seconds");

                for (int i = 0; i < threadCount; i++) {
                    executor.submit(() -> {
                        long end = System.nanoTime() + TimeUnit.SECONDS.toNanos(durationSeconds);
                        double x = 0;
                        while (System.nanoTime() < end) {
                            x += Math.sqrt(x + 1.23456789);
                            if (x > 1_000_000) x = 0;
                        }
                    });
                }

                executor.shutdown();

                try {
                    executor.awaitTermination(durationSeconds + 1, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });

        scheduler.setDaemon(true);
        scheduler.start();
    }
}