package cherkasov.com;

import java.text.MessageFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static cherkasov.com.ProjectLogger.LOG;

/**
 * Debug some functionality
 */
public class DebugThreads {

    private final boolean debugState;
    private final Downloader downloader;

    public DebugThreads(boolean debugState, Downloader downloader) {
        this.debugState = debugState;
        this.downloader = downloader;
    }

    /**
     * Thread for monitoring speed of downloading and bucket filling
     * Prints info on console every second
     */
    public void threadSpeedMonitor() {
        if (!debugState) return;

        Runnable runner = () -> {
            long previousBytes = 0;

            do {

                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    LOG.warning(e.getMessage());
                }

                System.out.println(MessageFormat.format("Speed: {0}; bucket: {1}",
                        downloader.getDownloadedBytesSummary().longValue() - previousBytes,
                        downloader.getBucketForAllThreads().get()));

                previousBytes = downloader.getDownloadedBytesSummary().longValue();
            } while (downloader.getIsAlive());
        };

        ExecutorService service = Executors.newSingleThreadExecutor();

        service.execute(runner);

        service.shutdown();
    }
}

