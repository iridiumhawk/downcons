package cherkasov.com;

import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;

public class DebugThreads {

    private final boolean debugState;
    private final Downloader downloader;

    public DebugThreads(boolean debugState, Downloader downloader) {
        this.debugState = debugState;
        this.downloader = downloader;
    }

    //for testing
    public void threadSpeedMonitor() {
        if (! debugState) return;

        Thread threadMonitor = new Thread(() -> {
            long previousBytes = 0;

            while (true) {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    System.out.println(e.getMessage());
                }

                System.out.println(MessageFormat.format("Speed: {0}; bucket: {1}", downloader.getDownloadedBytesSummary().longValue() - previousBytes, downloader.getBucketForAllThreads().get()));

                previousBytes = downloader.getDownloadedBytesSummary().longValue();
            }
        });

        threadMonitor.start();
    }
}

