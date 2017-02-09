package cherkasov.com;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;

import static cherkasov.com.Main.LOG;

public class Downloader {
    private ConcurrentLinkedQueue<DownloadEntity> queueTasks;
    private ParserParameters params;
    private volatile AtomicLong downloadedBytes = new AtomicLong(0L);

    public Downloader() {
    }

    public Downloader(ConcurrentLinkedQueue<DownloadEntity> queueTasks, ParserParameters parserParameters) {
        this.queueTasks = queueTasks;
        this.params = parserParameters;
    }

    private void threadsExecutor(int threadCounter) {

        final CountDownLatch latch = new CountDownLatch(threadCounter);

        Runnable runner = new Runnable() {
            @Override
            public void run() {

                while (!queueTasks.isEmpty()) {
                    try {

                        downloadFile(queueTasks.poll(), params.getOutputFolder());

                        //for test
//                        queueTasks.clear();

                    } catch (Exception e) {
                        LOG.log(Level.WARNING, e.getMessage());
                    }
                }

                //when tasks ended
                latch.countDown();
            }
        };

        ExecutorService service = Executors.newFixedThreadPool(threadCounter);
        LOG.log(Level.INFO, "thread pool started");

        for (int i = 0; i < threadCounter; i++) {
            service.execute(runner);
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            LOG.log(Level.WARNING, e.getMessage());
        }

        LOG.log(Level.INFO, "download was done");
    }

    //todo private
    public void downloadFile(DownloadEntity urlFile, String folder) throws IOException, InterruptedException {
//        LOG.log(Level.INFO, "download url: " + urlFile.getUrl() + " start");

        int inputBuffer = 100000;
        int bytesDownloaded = 0;
        long timeSpent = 0;

        Path dir = Paths.get(folder);

        if (!Files.isDirectory(dir)) {
            Files.createDirectory(dir);
        }

//        URL link = new URL("http://.txt");
        URL link = new URL(urlFile.getUrl());

        //todo output file name + folder
        String fileName = folder + "\\" + urlFile.getFileName();

        InputStream in = new BufferedInputStream(link.openStream());
        FileOutputStream fos = new FileOutputStream(fileName);

        byte[] buf = new byte[inputBuffer];

        int n;
        while (true) {
            Long timer = System.currentTimeMillis();

            n = in.read(buf);

            timer = System.currentTimeMillis() - timer;

            timeSpent += timer;
            if (n == -1) {
                break;
            }

//            System.out.println("get: "+n+" byte, "+timer+" nanosec");

//            if speed > maxDownloadSpeed
            //        TimeUnit.SECONDS.sleep(5L);
//скорость потоков одна на всех, распределять честно?
//each thread download with max speed (curl, get bytes, count time, if speed exceed - sleep for timeslot)

            fos.write(buf,0, n);

            bytesDownloaded += n;

        }

        in.close();
        fos.close();

        System.out.println("Thread finished");

        //todo on exit thread write downloaded bytes
        addDownloadedBytes(bytesDownloaded);

        System.out.println("download bytes: " + bytesDownloaded);
        System.out.println("spent time: " + timeSpent);

//        LOG.log(Level.INFO, "download bytes: " + bytesDownloaded);

    }

    public void start() {
        threadsExecutor(params.getNumberOfThreads());
    }

    //atomically add downloaded bytes to counter
    private void addDownloadedBytes(long bytes) {
        downloadedBytes.getAndAdd(bytes);
    }

}






