package cherkasov.com;

import cherkasov.com.streamsource.*;

import java.io.*;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;

import static cherkasov.com.ProjectLogger.LOG;

/**
 * Download files from given source through multiple threads
 */
public class Downloader {

    private final ConcurrentLinkedQueue<TaskEntity> queueThreadTasks;
    private final Parameters parameters;
    private final DebugThreads debugThreads;
    private ConnectionType connectionType;
    private final long bucketMaxSize;
    private final long middleSpeedOneThread;
    private final long inputBufferOneThread;

    private AtomicBoolean isAlive = new AtomicBoolean(true);
    private final AtomicLong downloadedBytesSummary = new AtomicLong(0L); //volatile
    private final AtomicLong bucketForAllThreads = new AtomicLong(0L);
    private final AtomicLong spentTimeSummary = new AtomicLong(0L);

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


    public long getSpentTimeSummary() {
        return spentTimeSummary.get();
    }

    public boolean getIsAlive() {
        return isAlive.get();
    }

    public void setIsAlive(boolean isAlive) {
        this.isAlive.set(isAlive);
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

    /**
     * Start debug and bucket threads, then start main thread pool
     */
    public void start() {
        //run debug
        debugThreads.threadSpeedMonitor();

        //start filling bucket
        threadBucketFill();

        //start threads
        threadsExecutor(parameters.getNumberOfThreads());

    }


    /**
     * Token Bucket Algorithm
     * Fills bucket with delay <code>timeToSleepBeforeFill</code> by <code>valueOfFilling</code> bytes
     */
    private void threadBucketFill() {

//        bucketFillThread = new Thread(
        Runnable runner = () -> {

            //todo implement various strategy of filling
            //milliseconds to sleep before fill next portion of bytes
            final long timeToSleepBeforeFill = 100;

            final long valueOfFilling = bucketMaxSize / 1000 * timeToSleepBeforeFill;

            while (getIsAlive()) {

                try {
                    increaseBucket(valueOfFilling);
                    TimeUnit.MILLISECONDS.sleep(timeToSleepBeforeFill);

                } catch (InterruptedException e) {
                    LOG.log(Level.WARNING, "threadBucketFill InterruptedException");
                }
            }
        };

        ExecutorService service = Executors.newSingleThreadExecutor();

        service.execute(runner);

        service.shutdown();
    }

    /**
     * Try put into bucket given amount of bytes
     * The number of bytes exceeding the limit <code>bucketMaxSize</code> will be discarded
     * @param updateValue - amount of byte for fill bucket
     */
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

    /**
    * Pool executor for launch all worker threads
    *
    */
    private void threadsExecutor(final int threadCounter) {

        final CountDownLatch latch = new CountDownLatch(threadCounter);

        Runnable runner = () -> {

            while (!queueThreadTasks.isEmpty()) {
                try {
                    TaskEntity task = queueThreadTasks.poll();
                    downloadFile(task,
                            getConnection(task.getUrl()),
                            Thread.currentThread().getName());

                } catch (Exception e) {
                    LOG.log(Level.WARNING, "Download Exception, " + e.getMessage());
                }
            }

            //stop thread if no more tasks in queue
            latch.countDown();
        };

        ExecutorService service = Executors.newFixedThreadPool(threadCounter);

        for (int i = 0; i < threadCounter; i++) {
            service.execute(runner);
        }

        LOG.log(Level.INFO, "Thread pool started");

        try {
            latch.await();
        } catch (InterruptedException e) {
            LOG.log(Level.WARNING, "InterruptedException, " + e.getMessage());
        }

        setIsAlive(false);

        service.shutdown();

        LOG.log(Level.INFO, MessageFormat.format("Download was done.\nTime spent summary for all threads: {0} seconds", getSpentTimeSummary()  / CONVERT_NANO_TO_SECONDS));
    }

    /**
     * Get connection to the given source
     * @param url - URL for HTTP connection
     * @return Connection
     */
    private Connection getConnection(String url) {
        switch (connectionType) {
            case FAKE:
                return new FakeConnection(new FakeHttpServer(10_000_000));
            case HTTP:
                return new HttpConnection(url);
        }

        return new DummyConnection();
    }

    /**
     * Download file from given source
     * @param task - link to file on server and name of file on disk
     * @param connection - from where download file
     * @param nameThread - name of thread fo logging
     */
    private void downloadFile(TaskEntity task, Connection connection, String nameThread) {

        if (!connection.connect()) {
            LOG.log(Level.WARNING, "Connection is not established!");
            return;
        }

        LOG.log(Level.INFO, "Download file from url: " + task.getUrl() + " start");

        String fileName = Paths.get(parameters.getOutputFolder(), task.getFileName()).toString();

        final long contentLength = connection.getContentLength();
        final long sleepTimeNanoSec = 100_000;
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

                    //doesn't work without checking contentLength
                    //todo check for correct downloading file (length, md5?)
                    if (numBytesRead == -1 || bytesDownloaded == contentLength) {
                        break;
                    }

                    timer = System.nanoTime() - timer;

                    timeSpentByTask += timer;

                    bytesDownloaded += numBytesRead;

                    addDownloadedBytes(numBytesRead);
                }

                TimeUnit.NANOSECONDS.sleep(sleepTimeNanoSec);
                timeSpentByTask += sleepTimeNanoSec;
            }
        } catch (Exception e) {
            LOG.log(Level.WARNING, "Exception in downloader thread, " + e.getMessage());
        }


        connection.disconnect();

        //time of each threads, summary time of all threads will be greater than time work for whole program
        addSpentTime(timeSpentByTask);

        System.out.println("File " + task.getUrl() + " downloaded");

        LOG.log(Level.INFO,
                MessageFormat.format("File {3} downloaded, bytes: {0} for time: {1} sec, by thread: {2}",
                        bytesDownloaded,
                        timeSpentByTask / CONVERT_NANO_TO_SECONDS,
                        nameThread,
                        task.getUrl()));
    }

}






