package cherkasov.com;

import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class DebugThreads {
    private final ConcurrentHashMap<String, Long> currentSpeedOfThreads = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, DownloadStatistics> statisticsOfThreads = new ConcurrentHashMap<>();
    private final List<String> statistic = new ArrayList<>();
    private final boolean debugState;
    private final Downloader downloader;

    public DebugThreads(boolean debugState, Downloader downloader) {
        this.debugState = debugState;
        this.downloader = downloader;
    }

    /* //for testing
    public void threadMonitor() {
        if (! debugState) return;

        Thread threadMonitor = new Thread(() -> {
            while (true) {
                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (InterruptedException e) {
                }
                speedInfo();
            }
        });

        threadMonitor.setDaemon(true);
        threadMonitor.start();
    }*/

    public void saveStatistics() {
        if (! debugState) return;

        final FileWriter fileWriter;
        try {
            fileWriter = new FileWriter("statistic.txt");

            synchronized (this) {
                statistic.forEach(str -> {
                    try {
                        fileWriter.write(str);
                    } catch (IOException e) {
                    }
                });
            }
            fileWriter.close();

        } catch (IOException e) {
        }
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
                }

                System.out.println(MessageFormat.format("Speed: {0}; bucket: {1}", downloader.getDownloadedBytesSum().longValue() - previousBytes, downloader.getDownloadBucket().get()));

                previousBytes = downloader.getDownloadedBytesSum().longValue();
            }
        });

        threadMonitor.start();
    }


    //visual information about current speed all threads
/*    private void speedInfo() {
if (! debugState) return;

        //get full sum speed
//        long sumSpeedAllThreads = currentSpeedOfThreads.values().stream().mapToLong(Long::longValue).sum();
//        long sumSpeedAllThreads = downloadedBytesSum.longValue() / (System.currentTimeMillis() - startTime) * 1000;

        synchronized (this) {

            statistic.add(MessageFormat.format("{0} \n", statisticsOfThreads.toString()));

//            statistic.add(MessageFormat.format("speed all: {0}, midspeed: {3}, threads: {1} \n", sumSpeedAllThreads, currentSpeedOfThreads.toString(), this.parametersOfWork.getMaxDownloadSpeed(), this.middleSpeedOneThread));
        }
    }*/

    class DownloadStatistics {
        long bufferSize;
        long timeOfGettingBuffer;
        long timeOutSleep;
        long speedCounted;

        @Override
        public String toString() {
            return "Stat{" +
                    "buff=" + bufferSize +
                    ", time=" + timeOfGettingBuffer +
                    ", sleep=" + timeOutSleep +
                    ", speed=" + speedCounted +
                    '}';
        }
    }

}

