package cherkasov.com.integration;

import cherkasov.com.ConnectionType;
import cherkasov.com.Downloader;
import cherkasov.com.Parameters;
import cherkasov.com.TaskEntity;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.concurrent.*;

import static org.junit.Assert.*;

/**
 * Created by hawk on 08.02.2017.
 */

/**
 * Test download fake files from memory stream
 */
public class DownloaderTest {
    private ConcurrentLinkedQueue<TaskEntity> queueTasks;
    private Parameters parameters;
    private int tasksCount = 10;

    /**
     * Fill queue with tasks that have fake urls and file names
     */
    @Before
    public void setUp() {
        queueTasks = new ConcurrentLinkedQueue<>();

        for (int i = 1; i <= tasksCount; i++) {
            queueTasks.add(new TaskEntity("http://" + i, i + ".txt"));
        }

        parameters = new Parameters(5, 5000000, "links.txt", "output", true);
    }

    /**
     * Download files and check that summary bytes equals (tasksCount * 10 000 000 bytes per task)
     */
    @Ignore
    @Test
    public void start() {
        final Downloader downloader = new Downloader(queueTasks, parameters, ConnectionType.FAKE);

        long timer = System.nanoTime();

        downloader.start();

        System.out.println(downloader.getDownloadedBytesSummary().get());

        System.out.println((System.nanoTime() - timer) / 1_000_000_000 + " sec");

        assertEquals(downloader.getDownloadedBytesSummary().get(), tasksCount * 10_000_000);
    }
}