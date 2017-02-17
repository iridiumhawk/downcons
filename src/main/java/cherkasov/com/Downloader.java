package cherkasov.com;


import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;

import static cherkasov.com.Main.LOG;


public class Downloader {
    private final ConcurrentLinkedQueue<TaskEntity> queueThreadTasks;
    private final ParserParameters parametersOfWork;
    private  final DebugThreads debugThreads;

    private volatile AtomicLong downloadedBytesSum = new AtomicLong(0L);


    private volatile AtomicLong downloadBucket = new AtomicLong(0L);
    private volatile AtomicLong spentTimeSummary = new AtomicLong(0L);

    private final int middleSpeedOneThread;
    private final int inputBufferOneThread;
    private final int convertNanoToSeconds = 1_000_000_000;
    private final int granularityOfManagement = 10;



    public Downloader(ConcurrentLinkedQueue<TaskEntity> queueTasks, ParserParameters parserParameters) {
        this.queueThreadTasks = queueTasks;
        this.parametersOfWork = parserParameters;
        this.middleSpeedOneThread = parametersOfWork.getMaxDownloadSpeed() / parametersOfWork.getNumberOfThreads();
        this.inputBufferOneThread = middleSpeedOneThread / granularityOfManagement; //how often thread will be managed

        this.debugThreads = new DebugThreads(parametersOfWork.isDebug(), this);
    }

    public AtomicLong getDownloadBucket() {
        return downloadBucket;
    }

    public AtomicLong getDownloadedBytesSum() {
        return downloadedBytesSum;
    }

    //atomically add downloaded bytes to counter
    private void addDownloadedBytes(long bytes) {
        downloadedBytesSum.getAndAdd(bytes);
    }

    //atomically add tine spent by thread to counter
    private void addSpentTime(long time) {
        spentTimeSummary.getAndAdd(time);
    }


    public void start() {

//        debugThreads.threadMonitor();

        debugThreads.threadSpeedMonitor();

        threadBucketFill();

        threadsExecutor(parametersOfWork.getNumberOfThreads());

//       debugThreads. saveStatistics();
    }


    //Token Bucket Algorithm
    private void threadBucketFill() {

        Thread threadMonitor = new Thread(() -> {

            while (true) {
                try {
                    //todo implement various strategy of filling
                    increaseBucket(parametersOfWork.getMaxDownloadSpeed() / 10);
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (InterruptedException e) {
                }
            }
        });

        threadMonitor.start();
    }

    private void increaseBucket(long updateValue) {

        downloadBucket.accumulateAndGet(updateValue, (current, given) -> {
            long result = current + given;
            if (result > parametersOfWork.getMaxDownloadSpeed()) {
                result = parametersOfWork.getMaxDownloadSpeed();
            }
            return result;
        });
    }

    private synchronized boolean checkAndDecreaseBucket(long updateValue) {

        if (downloadBucket.get() < updateValue) {
            return false;
        }

        downloadBucket.accumulateAndGet(updateValue, (current, given) -> current - given);

        return true;
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

            //if no more tasks in queue
            latch.countDown();
        };

        ExecutorService service = Executors.newFixedThreadPool(threadCounter);

        LOG.log(Level.INFO, "Thread pool start");

        for (int i = 0; i < threadCounter; i++) {
            service.execute(runner);
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            LOG.log(Level.WARNING, "InterruptedException, " + e.getMessage());
        }

        LOG.log(Level.INFO, MessageFormat.format("Download was done.\nTime spent summary for all threads: {0} seconds", spentTimeSummary.get() / convertNanoToSeconds));
    }

    private void downloadFile(TaskEntity urlFile, String nameThread) {
        if (urlFile == null) return;

//        DownloadStatistics statisticThread = statisticsOfThreads.getOrDefault(nameThread, new DownloadStatistics());

//        statisticThread.bufferSize = inputBufferOneThread;

        URL link;
        HttpURLConnection httpURLConnection;

        try {
            link = new URL(urlFile.getUrl());
            httpURLConnection = (HttpURLConnection) link.openConnection();
            int responseCode = httpURLConnection.getResponseCode();

            if (responseCode != HttpURLConnection.HTTP_OK) {
                LOG.log(Level.WARNING, "Response Code, " + responseCode);
                return;
            }
        } catch (IOException e) {
            LOG.log(Level.WARNING, "URLException Exception, " + e.getMessage());
            return;
        }

        LOG.log(Level.INFO, "Download url: " + urlFile.getUrl() + " start");

        String fileName = Paths.get(parametersOfWork.getOutputFolder(), urlFile.getFileName()).toString();

//        String disposition = httpURLConnection.getHeaderField("Content-Disposition");
//        String contentType = httpURLConnection.getContentType();

        int contentLength = httpURLConnection.getContentLength();
        long bytesDownloaded = 0;
        long timeSpentByTask = 0;

//        long timePauseThread = 10; //convertNanoToSeconds / middleSpeedOneThread / inputBufferOneThread; //initial pause eliminate burst download
//        long speedCurrentThread;

        //todo implement various strategy of download from internet
        // opens input stream from the HTTP connection
        try (
                ReadableByteChannel rbc = Channels.newChannel(httpURLConnection.getInputStream());
                FileOutputStream fos = new FileOutputStream(fileName)
        ) {
//                InputStream in = httpURLConnection.getInputStream();
//                InputStream in = new BufferedInputStream(link.openStream());
//            byte[] buf = new byte[inputBufferOneThread];
//            ByteBuffer dst = ByteBuffer.wrap(buf);

            int numBytesRead;

            while (true) {

                //use Token Bucket Algorithm
                if (checkAndDecreaseBucket(inputBufferOneThread)) {// true

                    Long timer = System.nanoTime();

//                numBytesRead = in.read(buf);
//                numBytesRead = rbc.read(dst);

                    numBytesRead = (int) fos.getChannel().transferFrom(rbc, bytesDownloaded, inputBufferOneThread);

                    //don`t work without checking contentLength
                    //todo check for correct downloading file (length, md5?)
                    if (numBytesRead == -1 || bytesDownloaded == contentLength) {
                        break;
                    }

                    timer = System.nanoTime() - timer;

                    timeSpentByTask += timer;

//                fos.write(buf, 0, numBytesRead);
                    fos.flush();

                    bytesDownloaded += numBytesRead;

                    addDownloadedBytes(numBytesRead);

//                    timeSpentByTask += timePauseThread;//del

                    //approximately
/*                    speedCurrentThread = convertNanoToSeconds / (timer + timePauseThread) * inputBufferOneThread;

                    timePauseThread = getTimePauseThread(timePauseThread, speedCurrentThread);
                    currentSpeedOfThreads.put(nameThread, speedCurrentThread);


                    statisticThread.speedCounted = speedCurrentThread;
                    statisticThread.timeOfGettingBuffer = timer;
                    statisticThread.timeOutSleep = timePauseThread;

                    statisticsOfThreads.put(nameThread, statisticThread);*/


                }
                //todo random timeout? //randomTimeOut.nextInt(10) +
                TimeUnit.MILLISECONDS.sleep( 1);
            }
        } catch (Exception e ) {
            LOG.log(Level.WARNING, "IOException, " + e.getMessage());
        }

        httpURLConnection.disconnect();

        //sum time of all threads, it will greater than time work for program
        addSpentTime(timeSpentByTask);

//        currentSpeedOfThreads.put(nameThread, 0L);
//        statisticsOfThreads.put(nameThread, new DownloadStatistics());


        LOG.log(Level.INFO, MessageFormat.format("File {3} downloaded, bytes: {0} for time: {1} sec, by thread: {2}", bytesDownloaded, timeSpentByTask / convertNanoToSeconds, nameThread, link.toString()));
    }
}






