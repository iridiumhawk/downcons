package cherkasov.com;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by hawk on 08.02.2017.
 */
public class DownloaderTest {
    private ConcurrentLinkedQueue<TaskEntity> queueTasks;
    private ParserParameters parserParameters;

    @Before
    public void setUp() throws Exception {
//        queueTasks = new ConcurrentLinkedQueue<>();
        String url = "http://download.advanceduninstaller.com/soft/uninstaller/Advanced_Uninstaller12.exe";
        String file = "Advanced_Uninstaller12.exe";

//        queueTasks.add(new DownloadEntity(url,file));

        queueTasks = new ParserLinks("links.txt").getQueueTasks();

        String[] args = {"-n", "5", "-l", "1M", "-f", "links.txt", "-o", "output"};
        parserParameters = new ParserParameters(args);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void start() throws Exception {

        new Downloader(queueTasks, parserParameters).start();

    }

}