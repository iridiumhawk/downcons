package cherkasov.com;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by hawk on 10.03.2017.
 */
public class ParametersTest {

    @Test
    public void isDebug() throws Exception {
        Parameters parameters = new Parameters(0, 0, "", "", false);
        assertEquals(false , parameters.isDebug());
    }

    @Test
    public void getNumberOfThreads() throws Exception {
        Parameters parameters = new Parameters(0, 0, "", "", false);
        assertEquals(1 , parameters.getNumberOfThreads());

        parameters = new Parameters(-1, 0, "", "", false);
        assertEquals(1 , parameters.getNumberOfThreads());

        parameters = new Parameters(Integer.MAX_VALUE, 0, "", "", false);
        assertEquals(Integer.MAX_VALUE , parameters.getNumberOfThreads());

    }

    @Test
    public void getMaxDownloadSpeed() throws Exception {
        Parameters parameters = new Parameters(0, 0, "", "", false);
        assertEquals(1000000 , parameters.getMaxDownloadSpeed());

        parameters = new Parameters(0, -1, "", "", false);
        assertEquals(1000000 , parameters.getMaxDownloadSpeed());

        parameters = new Parameters(0, Integer.MAX_VALUE, "", "", false);
        assertEquals(Integer.MAX_VALUE , parameters.getMaxDownloadSpeed());
    }

    @Test
    public void getFileNameWithLinks() throws Exception {

    }

    @Test
    public void getOutputFolder() throws Exception {

    }

}