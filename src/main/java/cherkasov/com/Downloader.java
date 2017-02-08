package cherkasov.com;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;

import static cherkasov.com.Main.LOG;

public class Downloader {
    private ConcurrentLinkedQueue<DownloadEntity> queueTasks;
    private ParserParameters params;
    private volatile AtomicLong downloadedBytes = new AtomicLong(0L);


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

                        downloadFile(queueTasks.poll(), params.getMaxDownloadSpeed());

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

    private void downloadFile(DownloadEntity urlFile, int maxDownloadSpeed) throws IOException, InterruptedException {
        LOG.log(Level.INFO, "download url: " + urlFile.getUrl() + " start");



//        URL link = new URL("http://cvde.com/robots.txt");
        URL link = new URL(urlFile.getUrl());

        //todo output file name + folder
        String fileName = urlFile.getFileName();

        InputStream in = new BufferedInputStream(link.openStream());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int n = 0;


        while (-1 != (n = in.read(buf))) {
//            if speed > maxDownloadSpeed
            //        TimeUnit.SECONDS.sleep(5L);
//скорость потоков одна на всех, распределять честно?
//each thread download with max speed (curl, get bytes, count time, if speed exceed - sleep for timeslot)

            out.write(buf, 0, n);
        }
        out.close();
        in.close();
        byte[] response = out.toByteArray();

        FileOutputStream fos = new FileOutputStream(fileName);
        fos.write(response);
        fos.close();

        System.out.println("Finished");

        //todo on exit thread write downloaded bytes
        addDownloadedBytes(0);
    }

    public void start() {
        threadsExecutor(params.getNumberOfThreads());
    }

    //atomically add downloaded bytes to counter
    private void addDownloadedBytes(long bytes) {
        downloadedBytes.getAndAdd(bytes);
    }

}






