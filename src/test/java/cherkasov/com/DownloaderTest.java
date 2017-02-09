package cherkasov.com;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by hawk on 08.02.2017.
 */
public class DownloaderTest {
    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void start() throws Exception {

        new Downloader().downloadFile(new DownloadEntity("http://www.innovative-sol.com/soft/taskmanager/advanced_task_manager.exe","advanced_task_manager.exe"), "download");

    }

}