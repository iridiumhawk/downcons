package cherkasov.com;


import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;

import static cherkasov.com.Main.LOG;

public class Downloader {
    private final ConcurrentLinkedQueue<DownloadEntity> queueTasks;
    private final ConcurrentHashMap<String, Integer> currentSpeedOfThreads = new ConcurrentHashMap<>(); //for thread
    private final ParserParameters params;

    private volatile AtomicLong downloadedBytes = new AtomicLong(0L);
    private volatile AtomicLong spentTime = new AtomicLong(0L);

    private final int middleSpeedOneThread;
    private final int inputBufferOneThread;
    private final List<Long> statistic = new ArrayList<>();

/*    public Downloader() {
    }*/

    public AtomicLong getDownloadedBytes() {
        return downloadedBytes;
    }

    public Downloader(ConcurrentLinkedQueue<DownloadEntity> queueTasks, ParserParameters parserParameters) {
        this.queueTasks = queueTasks;
        this.params = parserParameters;
        this.middleSpeedOneThread = params.getMaxDownloadSpeed() / params.getNumberOfThreads();
        this.inputBufferOneThread = this.middleSpeedOneThread / 10; //about 100 ms for one read buffer
    }


    public void start() {
        Path dir = Paths.get(params.getOutputFolder());

        if (!Files.isDirectory(dir)) {
            try {
                Files.createDirectory(dir);
            } catch (IOException e) {
//                e.printStackTrace();
                LOG.log(Level.WARNING, "createDirectory Exception, " + e.getMessage());
            }
        }

        threadsExecutor(params.getNumberOfThreads());
    }

    private void threadsExecutor(int threadCounter) {

        final CountDownLatch latch = new CountDownLatch(threadCounter);

        Runnable runner = () -> {

            while (!queueTasks.isEmpty()) {
                try {
                    downloadFile(queueTasks.poll(), Thread.currentThread().getName());

                } catch (Exception e) {
                    LOG.log(Level.WARNING, "Download Exception, " + e.getMessage());
                }
            }

            //when tasks ended
            latch.countDown();
        };

        ExecutorService service = Executors.newFixedThreadPool(threadCounter);
        LOG.log(Level.INFO, "thread pool started");

        for (int i = 0; i < threadCounter; i++) {
            service.execute(runner);
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            LOG.log(Level.WARNING, "InterruptedException, " + e.getMessage());
        }

        LOG.log(Level.INFO, "Download was done");
        LOG.log(Level.INFO, MessageFormat.format("Time spent for all threads: {0} seconds", this.spentTime.get() / 1000));
    }

    //todo private
    private void downloadFile(DownloadEntity urlFile, String nameThread) {
        LOG.log(Level.INFO, "download url: " + urlFile.getUrl() + " start");
        currentSpeedOfThreads.put(nameThread, this.middleSpeedOneThread);

        int inputBuffer = this.inputBufferOneThread;
        int timePauseThread = 0;
        int bytesDownloaded = 0;
        int timeSpentThread = 0;


//        URL link = new URL("http://.txt");
        URL link = null;
        try {
            link = new URL(urlFile.getUrl());
        } catch (MalformedURLException e) {
//            e.printStackTrace();
            LOG.log(Level.WARNING, "MalformedURLException Exception, " + e.getMessage());
            return;
        }

        //todo change path for win and linux
        String fileName = params.getOutputFolder() + "\\" + urlFile.getFileName();


        try (
                InputStream in = new BufferedInputStream(link.openStream());
                FileOutputStream fos = new FileOutputStream(fileName)
        ) {

            byte[] buf = new byte[inputBuffer];

            int numBytesRead;
            while (true) {
                Long timer = System.currentTimeMillis();

                numBytesRead = in.read(buf);

                if (numBytesRead == -1) {
                    break;
                }

                timer = System.currentTimeMillis() - timer;

                timeSpentThread += timer;

                fos.write(buf, 0, numBytesRead);

                bytesDownloaded += numBytesRead;

                int speedCurrentThread = 0;

                if (timer > 0) {
                    //approximately
                    speedCurrentThread = 1000 / timer.intValue() * inputBuffer;
                }

                currentSpeedOfThreads.put(nameThread, speedCurrentThread);

                timePauseThread = getTimePauseThread(timePauseThread, speedCurrentThread);

                TimeUnit.MILLISECONDS.sleep(timePauseThread);
            }


        } catch (IOException e) {
            LOG.log(Level.WARNING, "IOException, " + e.getMessage());

        } catch (InterruptedException e) {
            LOG.log(Level.WARNING, "InterruptedException, " + e.getMessage());
        }



        //todo on exit thread write downloaded bytes
        addDownloadedBytes(bytesDownloaded);

        //sum time of all threads, it will greater than time work for program
        addSpentTime(timeSpentThread);


//        System.out.println("download bytes: " + bytesDownloaded);
//        System.out.println("spent time: " + timeSpent);

        LOG.log(Level.INFO, MessageFormat.format("downloaded bytes: {0} for time: {1} by thread: {2}", bytesDownloaded, timeSpentThread, nameThread));
    }

    //strategy for thread balancing
    private int getTimePauseThread(int timePauseThread, int speedCurrentThread) {
        int result = timePauseThread;

        //get full sum speed
        long sumSpeedAllThreads = currentSpeedOfThreads.values().stream().mapToInt(Integer::intValue).sum();

        if (sumSpeedAllThreads > params.getMaxDownloadSpeed()) {
            if (speedCurrentThread > this.middleSpeedOneThread) {
                result += 100 / speedCurrentThread / this.middleSpeedOneThread; //add to sleep time about 100 msec
            } else {
                result = 0;
            }
        } else {
            result = 0;
        }
        return result < 0 ? 0 : result;
    }


    //atomically add downloaded bytes to counter
    private void addDownloadedBytes(long bytes) {
        downloadedBytes.getAndAdd(bytes);
    }

    private void addSpentTime(long time) {
        spentTime.getAndAdd(time);
    }

}






