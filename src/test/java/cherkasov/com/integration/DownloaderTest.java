package cherkasov.com.integration;

import cherkasov.com.ConnectionType;
import cherkasov.com.Downloader;
import cherkasov.com.Parameters;
import cherkasov.com.TaskEntity;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.*;
import java.util.logging.Level;

import static org.junit.Assert.*;
import static cherkasov.com.ProjectLogger.LOG;


/**
 * Tests downloading the fake files from memory stream.
 * Ignored by gradle. Must be started manually.
 */
public class DownloaderTest {
    private ConcurrentLinkedQueue<TaskEntity> queueTasks;
    private Parameters parameters;
    private final int tasksCount = 10;
    private final int maxDownloadSpeed = 5000000;
    private final int numberOfThreads = 5;
    private final String filenameWithLinks = "links.txt";
    private final String folderName = "output";
    private Path output;

    /**
     * Fills the queue with tasks that have fake urls and file names
     * Creates temp dir for downloaded files
     */
    @Before
    public void setUp() throws IOException {
        queueTasks = new ConcurrentLinkedQueue<>();

        for (int i = 1; i <= tasksCount; i++) {
            queueTasks.add(new TaskEntity("http://" + i, i + ".txt"));
        }

        output = Files.createTempDirectory(folderName);

        parameters = new Parameters(numberOfThreads, maxDownloadSpeed, filenameWithLinks, output.toString(), true);

    }

    /**
     * Removes temp directory after tests finished
     */
    @After
    public void tearDown() {
        removeDirectory(output.toFile());
    }

    /**
     * Removes a non empty directory
     * @param dir   directory name for remove
     */
    public static void removeDirectory(File dir) {
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null && files.length > 0) {
                for (File aFile : files) {
                    removeDirectory(aFile);
                }
            }
            dir.delete();
        } else {
            dir.delete();
        }
    }

    /**
     * Downloads files and checks that summary bytes equals == tasksCount * 10 000 000 bytes per task
     */
    @Test
    public void start() {
        final Downloader downloader = new Downloader(queueTasks, parameters, ConnectionType.FAKE);

        long timer = System.nanoTime();

        downloader.start();

        System.out.println(downloader.getDownloadedBytesSummary().get());

        System.out.println((System.nanoTime() - timer) / 1_000_000_000 + " sec");

        assertEquals(tasksCount * 10_000_000, downloader.getDownloadedBytesSummary().get());

        LOG.log(Level.INFO, "DownloaderTest pass");
    }
}