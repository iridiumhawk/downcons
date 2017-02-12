package cherkasov.com;


import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.stream.Stream;

import static cherkasov.com.Main.LOG;

public class Downloader {
    private final ConcurrentLinkedQueue<DownloadEntity> queueThreadTasks;
    private final ConcurrentHashMap<String, Long> currentSpeedOfThreads = new ConcurrentHashMap<>(); //for thread speed
    private final ParserParameters parametersOfWork;

    private volatile AtomicLong downloadedBytesSum = new AtomicLong(0L);
    private volatile AtomicLong spentTimeSummary = new AtomicLong(0L);

    private final int middleSpeedOneThread;
    private final int inputBufferOneThread;
    private final int nanoTimeToSeconds = 1_000_000_000;
    private final int granularityOfManagement = 10;

    private final List<String> statistic = new ArrayList<>();

/*    public Downloader() {
    }*/

    public AtomicLong getDownloadedBytesSum() {
        return downloadedBytesSum;
    }

    public Downloader(ConcurrentLinkedQueue<DownloadEntity> queueTasks, ParserParameters parserParameters) {
        this.queueThreadTasks = queueTasks;
        this.parametersOfWork = parserParameters;
        this.middleSpeedOneThread = parametersOfWork.getMaxDownloadSpeed() / parametersOfWork.getNumberOfThreads();
        this.inputBufferOneThread = middleSpeedOneThread / granularityOfManagement; //haw often thread will be managed
    }


    public void start() {

        threadMonitor();

        threadsExecutor(parametersOfWork.getNumberOfThreads());

        saveStatistics();
    }

    private void saveStatistics() {
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
    private void threadMonitor() {

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
    }

    //launch all threads
    private void threadsExecutor(int threadCounter) {

        final CountDownLatch latch = new CountDownLatch(threadCounter);

        Runnable runner = () -> {

            while (!queueThreadTasks.isEmpty()) {
                try {
                    downloadFile(queueThreadTasks.poll(), Thread.currentThread().getName());

                } catch (Exception e) {
                    LOG.log(Level.WARNING, "Download Exception, " + e.getMessage());
                }
            }

            latch.countDown(); //when all tasks in queue ended
        };

        ExecutorService service = Executors.newFixedThreadPool(threadCounter);
        LOG.log(Level.INFO, "Thread pool started");

        for (int i = 0; i < threadCounter; i++) {
            service.execute(runner);
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            LOG.log(Level.WARNING, "InterruptedException, " + e.getMessage());
        }

        LOG.log(Level.INFO, "Download was done");
        LOG.log(Level.INFO, MessageFormat.format("Time spent summary for all threads: {0} seconds", this.spentTimeSummary.get() / nanoTimeToSeconds));
    }

    //todo private
    private void downloadFile(DownloadEntity urlFile, String nameThread) {
        if (urlFile == null) return;

        LOG.log(Level.INFO, "Download url: " + urlFile.getUrl() + " start");

//        currentSpeedOfThreads.put(nameThread, 0 );//this.middleSpeedOneThread

//        int inputBuffer = this.inputBufferOneThread;
        long timePauseThread = 100_000_000; //initial pause 100 msec for eliminate burst download
        long bytesDownloaded = 0;
        long timeSpentByTask = 0;
        long speedCurrentThread;

//        URL link = new URL("http://.txt");
        URL link;

        try {
            link = new URL(urlFile.getUrl());
        } catch (MalformedURLException e) {
//            e.printStackTrace();
            LOG.log(Level.WARNING, "MalformedURLException Exception, " + e.getMessage());
            return;
        }

        //todo change path for win and linux
        String fileName = parametersOfWork.getOutputFolder() + "\\" + urlFile.getFileName();


        try (
                InputStream in = new BufferedInputStream(link.openStream());
                FileOutputStream fos = new FileOutputStream(fileName)
        ) {

            byte[] buf = new byte[this.inputBufferOneThread];

            int numBytesRead;
            while (true) {
                Long timer = System.nanoTime();

                numBytesRead = in.read(buf);

                if (numBytesRead == -1) {
                    break;
                }

                timer = System.nanoTime() - timer;

                timeSpentByTask += timer;

                fos.write(buf, 0, numBytesRead);

                bytesDownloaded += numBytesRead;

                if (timer <= 0) {
                    timer = 1L;
                }

                //approximately
                speedCurrentThread = nanoTimeToSeconds / (timer + timePauseThread) * this.inputBufferOneThread;

                currentSpeedOfThreads.put(nameThread, speedCurrentThread);

                timePauseThread = getTimePauseThread(timePauseThread, speedCurrentThread);

                TimeUnit.NANOSECONDS.sleep(timePauseThread);

                timeSpentByTask += timePauseThread;
            }


        } catch (IOException e) {
            LOG.log(Level.WARNING, "IOException, " + e.getMessage());

        } catch (InterruptedException e) {
            LOG.log(Level.WARNING, "InterruptedException, " + e.getMessage());
        }

        //on exit thread write downloaded bytes
        addDownloadedBytes(bytesDownloaded);

        //sum time of all threads, it will greater than time work for program
        addSpentTime(timeSpentByTask);

        currentSpeedOfThreads.put(nameThread, 0L);

        LOG.log(Level.INFO, MessageFormat.format("File {3} downloaded -  bytes: {0} for time: {1} by thread: {2}", bytesDownloaded, timeSpentByTask / nanoTimeToSeconds, nameThread, link.toString()));
    }

    //strategy for thread balancing
    private long getTimePauseThread(long timePauseThread, long speedCurrentThread) {
        long result = timePauseThread;

        //get full sum speed
        long sumSpeedAllThreads = currentSpeedOfThreads.values().stream().mapToLong(Long::longValue).sum();

        if (sumSpeedAllThreads > parametersOfWork.getMaxDownloadSpeed()) {
            if (speedCurrentThread > this.middleSpeedOneThread) {
                result += 10_000_000; // 100 / this.middleSpeedOneThread / speedCurrentThread  ; //add to sleep time about 100 msec
            } else {
                result -= 10_000_000;
            }
        } else {
            result -= 10_000_000;
        }
        return result < 0 ? 0 : result;
    }

    //visual information about current speed all threads
    private void speedInfo() {
        //get full sum speed
        long sumSpeedAllThreads = currentSpeedOfThreads.values().stream().mapToLong(Long::longValue).sum();

        synchronized (this) {

            statistic.add(MessageFormat.format("speed all: {0}, midspeed: {3}, threads: {1} \n" , sumSpeedAllThreads, currentSpeedOfThreads.toString(), this.parametersOfWork.getMaxDownloadSpeed(), this.middleSpeedOneThread));
        }
    }

    //atomically add downloaded bytes to counter
    private void addDownloadedBytes(long bytes) {
        downloadedBytesSum.getAndAdd(bytes);
    }

    private void addSpentTime(long time) {
        spentTimeSummary.getAndAdd(time);
    }

}






