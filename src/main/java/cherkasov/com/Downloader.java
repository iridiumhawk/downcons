package cherkasov.com;


import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.stream.Stream;

import static cherkasov.com.Main.LOG;


public class Downloader {
    private final ConcurrentLinkedQueue<DownloadEntity> queueThreadTasks;
    private final ConcurrentHashMap<String, Long> currentSpeedOfThreads = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, DownloadStatistics> statisticsOfThreads = new ConcurrentHashMap<>();
    private final ParserParameters parametersOfWork;

    private volatile AtomicLong downloadedBytesSum = new AtomicLong(0L);
    private volatile AtomicLong downloadBucket = new AtomicLong(0L);
    private volatile AtomicLong spentTimeSummary = new AtomicLong(0L);

    private final int middleSpeedOneThread;
    private final int inputBufferOneThread;
    private final int nanoTimeToSeconds = 1_000_000_000;
    private final int granularityOfManagement = 10;
    //del
    private long startTime;

    private final List<String> statistic = new ArrayList<>();

    public AtomicLong getDownloadedBytesSum() {
        return downloadedBytesSum;
    }

    public Downloader(ConcurrentLinkedQueue<DownloadEntity> queueTasks, ParserParameters parserParameters) {
        this.queueThreadTasks = queueTasks;
        this.parametersOfWork = parserParameters;
        this.middleSpeedOneThread = parametersOfWork.getMaxDownloadSpeed() / parametersOfWork.getNumberOfThreads();
        this.inputBufferOneThread = middleSpeedOneThread / granularityOfManagement; //how often thread will be managed
    }

    public void start() {

        threadMonitor();
        threadSpeedMonitor();
        threadBucketFill();

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

    //Token Bucket Algorithm
    private void threadBucketFill() {

        Thread threadMonitor = new Thread(() -> {

            while (true) {
                try {
                    increaseBucket(parametersOfWork.getMaxDownloadSpeed() / 10);
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (InterruptedException e) {
                }
            }
        });

        threadMonitor.start();
    }

    private void increaseBucket(long updateValue) {

            downloadBucket.accumulateAndGet(updateValue,(current,given)->{ long result = current+given; if (result > parametersOfWork.getMaxDownloadSpeed()) { result = parametersOfWork.getMaxDownloadSpeed();} return result;});
    }

    private synchronized boolean checkBucket(long updateValue) {

        if (downloadBucket.get() < updateValue) {
            return false;
        }

        downloadBucket.accumulateAndGet(updateValue,(current,given)->current - given);

        return true;
    }



    //for testing
    private void threadSpeedMonitor() {

        Thread threadMonitor = new Thread(() -> {
            long previousBytes = 0;

            while (true) {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                }

                System.out.println(downloadedBytesSum.longValue() - previousBytes+" : "+ downloadBucket.get());

                previousBytes = downloadedBytesSum.longValue();
            }
        });

//        threadMonitor.setDaemon(true);
        threadMonitor.start();
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

        startTime = System.currentTimeMillis();

        try {
            latch.await();
        } catch (InterruptedException e) {
            LOG.log(Level.WARNING, "InterruptedException, " + e.getMessage());
        }

        LOG.log(Level.INFO, "Download was done");
        LOG.log(Level.INFO, MessageFormat.format("Time spent summary for all threads: {0} seconds", this.spentTimeSummary.get() / nanoTimeToSeconds));
    }

    //realize new model - bucket

    private void downloadFile(DownloadEntity urlFile, String nameThread) {
        if (urlFile == null) return;

        LOG.log(Level.INFO, "Download url: " + urlFile.getUrl() + " start");


        DownloadStatistics statisticThread = statisticsOfThreads.getOrDefault(nameThread, new DownloadStatistics());

        statisticThread.bufferSize = inputBufferOneThread;

        URL link;
        HttpURLConnection httpConn;

        try {
            link = new URL(urlFile.getUrl());
            httpConn = (HttpURLConnection) link.openConnection();
            int responseCode = httpConn.getResponseCode();

            if (responseCode != HttpURLConnection.HTTP_OK) {
                LOG.log(Level.WARNING, "Response Code, " + responseCode);
                return;
            }

        } catch (IOException e) {
            LOG.log(Level.WARNING, "URLException Exception, " + e.getMessage());
            return;
        }

        String fileName = Paths.get(parametersOfWork.getOutputFolder(), urlFile.getFileName()).toString();

//        String disposition = httpConn.getHeaderField("Content-Disposition");
//        String contentType = httpConn.getContentType();

        int contentLength = httpConn.getContentLength();
        long bytesDownloaded = 0;

        long timePauseThread = 10; //nanoTimeToSeconds / middleSpeedOneThread / inputBufferOneThread; //initial pause eliminate burst download
        long timeSpentByTask = 0;
        long speedCurrentThread;

        // opens input stream from the HTTP connection
        try (
                ReadableByteChannel rbc = Channels.newChannel(httpConn.getInputStream());
//                InputStream in = httpConn.getInputStream();
//                InputStream in = new BufferedInputStream(link.openStream());
             FileOutputStream fos = new FileOutputStream(fileName)


        ) {

            byte[] buf = new byte[inputBufferOneThread];
//            ByteBuffer dst = ByteBuffer.wrap(buf);

            int numBytesRead = 0;
            Random randomTimeOut = new Random();

            while (true) {

            //Token Bucket Algorithm
                if (checkBucket(inputBufferOneThread)) {// true

                    Long timer = System.nanoTime();

                    //todo change method of getting buffer
//                numBytesRead = in.read(buf);
//                numBytesRead = rbc.read(dst);

                    numBytesRead = (int) fos.getChannel().transferFrom(rbc, bytesDownloaded, inputBufferOneThread);
//                fos.flush();

                    if (numBytesRead == -1 || bytesDownloaded == contentLength) {
                        break;
                    }

                    timer = System.nanoTime() - timer;

                    timeSpentByTask += timer;

//                fos.write(buf, 0, numBytesRead);
                    fos.flush();

                    bytesDownloaded += numBytesRead;


                    //approximately
                    speedCurrentThread = nanoTimeToSeconds / (timer + timePauseThread) * inputBufferOneThread;

                    currentSpeedOfThreads.put(nameThread, speedCurrentThread);

                    timePauseThread = getTimePauseThread(timePauseThread, speedCurrentThread);

                    statisticThread.speedCounted = speedCurrentThread;
                    statisticThread.timeOfGettingBuffer = timer;
                    statisticThread.timeOutSleep = timePauseThread;

                    statisticsOfThreads.put(nameThread, statisticThread);

                    addDownloadedBytes(numBytesRead);

                    timeSpentByTask += timePauseThread;//del
                }
                //todo random timeout?
                TimeUnit.MILLISECONDS.sleep(randomTimeOut.nextInt(10) + 1);





            }

        } catch (IOException e) {
            LOG.log(Level.WARNING, "IOException, " + e.getMessage());

        } catch (InterruptedException e) {
            LOG.log(Level.WARNING, "InterruptedException, " + e.getMessage());
        }

        httpConn.disconnect();

        //on exit thread write downloaded bytes
//        addDownloadedBytes(bytesDownloaded);

        //sum time of all threads, it will greater than time work for program
        addSpentTime(timeSpentByTask);

        currentSpeedOfThreads.put(nameThread, 0L);
        statisticsOfThreads.put(nameThread, new DownloadStatistics());


        LOG.log(Level.INFO, MessageFormat.format("File {3} downloaded -  bytes: {0} for time: {1} by thread: {2}", bytesDownloaded, timeSpentByTask / nanoTimeToSeconds, nameThread, link.toString()));
    }

    //strategy for thread balancing
    private long getTimePauseThread(long timePauseThread, long speedCurrentThread) {
        //todo check for 0
        if (speedCurrentThread == 0) {
            speedCurrentThread = 10;
        }

        long result = timePauseThread;

        //get full sum speed
        long sumSpeedAllThreads = currentSpeedOfThreads.values().stream().mapToLong(Long::longValue).sum();

        if (sumSpeedAllThreads > parametersOfWork.getMaxDownloadSpeed()) {
            if (speedCurrentThread > middleSpeedOneThread) {
                result += timePauseThread - (float) timePauseThread / ((float) speedCurrentThread / middleSpeedOneThread); //1_000_000; //add to sleep time about 100 msec
            } else if (speedCurrentThread < middleSpeedOneThread) {
                result -= timePauseThread - (float) timePauseThread * ((float) speedCurrentThread / middleSpeedOneThread); //1_000_000;
            }
        } else if (sumSpeedAllThreads < parametersOfWork.getMaxDownloadSpeed()) {
            result = 0;//timePauseThread - (float) timePauseThread * ((float) speedCurrentThread / middleSpeedOneThread); //1_000_000;
        }
        return result < 0 ? 0 : result;
    }

    //visual information about current speed all threads
    private void speedInfo() {
        //get full sum speed
//        long sumSpeedAllThreads = currentSpeedOfThreads.values().stream().mapToLong(Long::longValue).sum();
//        long sumSpeedAllThreads = downloadedBytesSum.longValue() / (System.currentTimeMillis() - startTime) * 1000;

        synchronized (this) {

            statistic.add(MessageFormat.format("{0} \n", statisticsOfThreads.toString()));

//            statistic.add(MessageFormat.format("speed all: {0}, midspeed: {3}, threads: {1} \n", sumSpeedAllThreads, currentSpeedOfThreads.toString(), this.parametersOfWork.getMaxDownloadSpeed(), this.middleSpeedOneThread));
        }
    }

    //atomically add downloaded bytes to counter
    private void addDownloadedBytes(long bytes) {
        downloadedBytesSum.getAndAdd(bytes);
    }

    private void addSpentTime(long time) {
        spentTimeSummary.getAndAdd(time);
    }

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






