package cherkasov.com;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by hawk on 10.03.2017.
 */
public class ParametersTest {
    private Parameters parameters;
    @Before
    public void setUp() throws Exception {
        parameters  = new Parameters(10, 10, "links.txt", "output", true);

    }

    @Test
    public void isDebug() throws Exception {
        assertEquals(true, parameters.isDebug());

    }

    @Test
    public void getNumberOfThreads() throws Exception {
     /*   Parameters parameters = new Parameters(0, 0, "", "", false);
        assertEquals(1, parameters.getNumberOfThreads());

        parameters = new Parameters(-1, 0, "", "", false);
        assertEquals(1, parameters.getNumberOfThreads());

        parameters = new Parameters(Integer.MAX_VALUE, 0, "", "", false);*/
        assertEquals(10, parameters.getNumberOfThreads());

    }

    @Test
    public void getMaxDownloadSpeed() throws Exception {
        Parameters parameters = new Parameters(0, 0, "", "", false);
        assertEquals(1000000, parameters.getMaxDownloadSpeed());

        parameters = new Parameters(0, -1, "", "", false);
        assertEquals(1000000, parameters.getMaxDownloadSpeed());

        parameters = new Parameters(0, Integer.MAX_VALUE, "", "", false);
        assertEquals(Integer.MAX_VALUE, parameters.getMaxDownloadSpeed());
    }

    @Test
    public void getFileNameWithLinks() throws Exception {
        Parameters parameters = new Parameters(0, 0, "test.txt", "", false);
        assertEquals("test.txt", parameters.getFileNameWithLinks());

        parameters = new Parameters(0, 0, "", "", false);
        assertEquals("links.txt", parameters.getFileNameWithLinks());

        parameters = new Parameters(0, 0, null, "", false);
        assertEquals("links.txt", parameters.getFileNameWithLinks());

    }

    @Test
    public void getOutputFolder() throws Exception {
        Parameters parameters = new Parameters(0, 0, "", "output", false);
        assertEquals("output", parameters.getOutputFolder());

        parameters = new Parameters(0, 0, "", "", false);
        assertEquals("download", parameters.getOutputFolder());

        parameters = new Parameters(0, 0, "", null, false);
        assertEquals("download", parameters.getOutputFolder());
    }
}