package cherkasov.com;


import cherkasov.com.source.*;

import java.io.*;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;

import static cherkasov.com.ProjectLogger.LOG;


public class Downloader {
    private final ConcurrentLinkedQueue<TaskEntity> queueThreadTasks;
    private final Parameters parameters;
    private final DebugThreads debugThreads;
    private final ConnectionType connectionType;
    private final long bucketMaxSize;
    private final int middleSpeedOneThread;
    private final int inputBufferOneThread;

    private volatile AtomicLong downloadedBytesSummary = new AtomicLong(0L);
    private volatile AtomicLong bucketForAllThreads = new AtomicLong(0L);
    private volatile AtomicLong spentTimeSummary = new AtomicLong(0L);

    private final int CONVERT_NANO_TO_SECONDS = 1_000_000_000;
    //how often thread will be managed (send request to server and get buffer), times in one second
    private final int GRANULARITY_OF_DOWNLOADING = 20;


    public Downloader(ConcurrentLinkedQueue<TaskEntity> queueTasks, Parameters parameters, ConnectionType connectionType) {
        this.queueThreadTasks = queueTasks;
        this.parameters = parameters;
        this.middleSpeedOneThread = parameters.getMaxDownloadSpeed() / parameters.getNumberOfThreads();
        this.bucketMaxSize = parameters.getMaxDownloadSpeed();
        this.inputBufferOneThread = middleSpeedOneThread / GRANULARITY_OF_DOWNLOADING;

        this.connectionType = connectionType;
        this.debugThreads = new DebugThreads(parameters.isDebug(), this);
    }

    public AtomicLong getBucketForAllThreads() {
        return bucketForAllThreads;
    }

    public AtomicLong getDownloadedBytesSummary() {
        return downloadedBytesSummary;
    }

    //atomically add downloaded bytes to counter
    private void addDownloadedBytes(long bytes) {
        downloadedBytesSummary.getAndAdd(bytes);
    }

    //atomically add time spent by thread to counter
    private void addSpentTime(long time) {
        spentTimeSummary.getAndAdd(time);
    }


    public void start() {
//run debug
//        debugThreads.threadMonitor();

        debugThreads.threadSpeedMonitor();

        //start filling bucket
        threadBucketFill();

        //start threads
        threadsExecutor(parameters.getNumberOfThreads());

//save debug data
//       debugThreads. saveStatistics();
    }


    //Token Bucket Algorithm
    private void threadBucketFill() {

        Thread threadMonitor = new Thread(() -> {

            //todo implement various strategy of filling
            //milliseconds
            final long timeToSleepBeforeFill = 10;

            final long valueOfFilling = bucketMaxSize / 1000 * timeToSleepBeforeFill;

            while (true) {
                try {

                    increaseBucket(valueOfFilling);
                    TimeUnit.MILLISECONDS.sleep(timeToSleepBeforeFill);
                } catch (InterruptedException e) {
                    LOG.log(Level.WARNING, "threadBucketFill InterruptedException");
                }
            }
        });

        threadMonitor.start();
    }

    private void increaseBucket(final long updateValue) {

        bucketForAllThreads.accumulateAndGet(updateValue, (current, given) -> {
            long result = current + given;
            if (result > bucketMaxSize) {
                result = bucketMaxSize;
            }
            return result;
        });
    }

    private synchronized boolean checkAndDecreaseBucket(final long updateValue) {

        if (bucketForAllThreads.get() < updateValue) {
            return false;
        }

        bucketForAllThreads.accumulateAndGet(updateValue, (current, given) -> current - given);

        return true;
    }

    //launch all threads
    private void threadsExecutor(final int threadCounter) {

        final CountDownLatch latch = new CountDownLatch(threadCounter);

        Runnable runner = () -> {

            while (!queueThreadTasks.isEmpty()) {
                try {
                    TaskEntity task = queueThreadTasks.poll();
                    downloadFile(task,
                            getConnection(task.getUrl()),
                            Thread.currentThread().getName());
//                    downloadFile(task, new HttpConnection(task.getUrl()), Thread.currentThread().getName());

                } catch (Exception e) {
                    LOG.log(Level.WARNING, "Download Exception, " + e.getMessage());
                }
            }

            //if no more tasks in queue
            latch.countDown();
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

        LOG.log(Level.INFO, MessageFormat.format("Download was done.\nTime spent summary for all threads: {0} seconds", spentTimeSummary.get() / CONVERT_NANO_TO_SECONDS));
    }

    private Connection getConnection(String url) {
        switch (connectionType) {
            case FAKE:
                return new FakeConnection(new FakeHttpServer(10000000, 50));
            case HTTP:
                return new HttpConnection(url);
        }

        return new DummyConnection();
    }

    private void downloadFile(TaskEntity task, Connection connection, String nameThread) {

        if (!connection.connect()) {
            LOG.log(Level.WARNING, "Don't have connection!");
            return;
        }

        LOG.log(Level.INFO, "Download url: " + task.getUrl() + " start");

        String fileName = Paths.get(parameters.getOutputFolder(), task.getFileName()).toString();

//        String disposition = httpURLConnection.getHeaderField("Content-Disposition");
//        String contentType = httpURLConnection.getContentType();

        final long contentLength = connection.getContentLength();
        final long sleepTimeNanoSec = 1_000_000;
        long bytesDownloaded = 0;
        long timeSpentByTask = 0;

        //todo implement various strategy of download from internet
        // opens input stream from the HTTP connection
        try (
                ReadableByteChannel readableByteChannel = Channels.newChannel(connection.getInputStream());
                FileOutputStream fileOutputStream = new FileOutputStream(fileName)
        ) {

            long numBytesRead;
            long currentBuffer = inputBufferOneThread;

            while (true) {
//                ByteBuffer bb = ByteBuffer.allocate((int)currentBuffer);

                //use Token Bucket Algorithm
                if (checkAndDecreaseBucket(currentBuffer)) {

                    long timer = System.nanoTime();

                    numBytesRead = fileOutputStream.
                            getChannel().
                            transferFrom(readableByteChannel, bytesDownloaded, currentBuffer);

//                    numBytesRead = readableByteChannel.read(bb);

                    //doesn't work without checking contentLength
                    //todo check for correct downloading file (length, md5?)
                    if (numBytesRead == -1 || bytesDownloaded == contentLength) {
                        break;
                    }

                    timer = System.nanoTime() - timer;

//                    System.out.println(timer);

                    //increase buffer size for compensation latency
//                    long theoreticalSpeed = numBytesRead / timer * CONVERT_NANO_TO_SECONDS;

                    currentBuffer = middleSpeedOneThread * timer / CONVERT_NANO_TO_SECONDS ;
                    currentBuffer = inputBufferOneThread >= currentBuffer ? inputBufferOneThread : currentBuffer > middleSpeedOneThread ? middleSpeedOneThread : currentBuffer;
                    //todo max buffersize = bucket size
//                    currentBuffer = theoreticalSpeed < middleSpeedOneThread ? (long) (1.5 * currentBuffer) : (long) (0.9 * currentBuffer);
//                    System.out.println(currentBuffer);


                    timeSpentByTask += timer;

//                    fileOutputStream.write(bb.array());
//                    fileOutputStream.flush();

                    bytesDownloaded += numBytesRead;

                    addDownloadedBytes(numBytesRead);

                }
                //todo random timeout? //randomTimeOut.nextInt(10)
                TimeUnit.NANOSECONDS.sleep(sleepTimeNanoSec);
//                timeSpentByTask += sleepTimeNanoSec;
            }
        } catch (Exception e) {
            LOG.log(Level.WARNING, "Exception in downloader thread, " + e.getMessage());
//            System.out.println(e.getMessage());
        }

        connection.disconnect();

        //time of each threads, summary time of all threads will be greater than time work for whole program
        addSpentTime(timeSpentByTask);

        LOG.log(Level.INFO,
                MessageFormat.format("File {3} downloaded, bytes: {0} for time: {1} sec, by thread: {2}",
                        bytesDownloaded,
                        timeSpentByTask / CONVERT_NANO_TO_SECONDS,
                        nameThread,
                        task.getUrl()));
    }

    enum ConnectionType {
        HTTP,
        FAKE
    }
}






