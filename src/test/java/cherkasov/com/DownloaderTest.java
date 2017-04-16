package cherkasov.com;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import java.util.concurrent.*;
import static org.junit.Assert.*;

/**
 * Created by hawk on 08.02.2017.
 */
public class DownloaderTest {
    private ConcurrentLinkedQueue<TaskEntity> queueTasks;
    private Parameters parameters;

    @Before
    public void setUp() throws Exception {
        queueTasks = new ConcurrentLinkedQueue<>();

        queueTasks.add(new TaskEntity("http://1", "1.txt"));
        queueTasks.add(new TaskEntity("http://2", "2.txt"));
        queueTasks.add(new TaskEntity("http://3", "3.txt"));
        queueTasks.add(new TaskEntity("http://4", "4.txt"));
        queueTasks.add(new TaskEntity("http://5", "5.txt"));
        queueTasks.add(new TaskEntity("http://6", "6.txt"));
        queueTasks.add(new TaskEntity("http://7", "7.txt"));
        queueTasks.add(new TaskEntity("http://8", "8.txt"));
        queueTasks.add(new TaskEntity("http://9", "9.txt"));
        queueTasks.add(new TaskEntity("http://10", "10.txt"));

        parameters = new Parameters(5, 5000000, "links.txt", "output", true);
    }

    @Ignore
    @Test
    public void start() throws Exception {
        final Downloader downloader = new Downloader(queueTasks, parameters, Downloader.ConnectionType.FAKE);

        long timer = System.nanoTime();

        downloader.start();

        //return atomic long
        System.out.println(downloader.getDownloadedBytesSummary().get());

        System.out.println((System.nanoTime() - timer) / 1_000_000_000 + " sec");

        assertEquals(downloader.getDownloadedBytesSummary().get(), 100_000_000);
    }
}