package cherkasov.com;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests correct parameters setting
 */
public class ParametersTest {
    private Parameters parameters;
    private final int maxDownloadSpeed = 5000000;
    private final int numberOfThreads = 5;
    private final String filenameWithLinks = "links.txt";
    private final String folderName = "output";

    @Before
    public void setUp() throws Exception {
        parameters = new Parameters(numberOfThreads, maxDownloadSpeed, filenameWithLinks, folderName, true);

    }

    @Test
    public void isDebug() throws Exception {
        assertTrue(parameters.isDebug());
    }

    @Test
    public void getNumberOfThreads() throws Exception {
        assertEquals(numberOfThreads, parameters.getNumberOfThreads());
    }

    @Test
    public void getMaxDownloadSpeed() throws Exception {

        assertEquals(maxDownloadSpeed, parameters.getMaxDownloadSpeed());
    }

    @Test
    public void getFileNameWithLinks() throws Exception {
        assertEquals(filenameWithLinks, parameters.getFileNameWithLinks());

    }

    @Test
    public void getOutputFolder() throws Exception {
       assertEquals(folderName, parameters.getOutputFolder());
    }
}