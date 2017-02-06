package cherkasov.com;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ParserParametersTest {
    private final String[] argsFull = {"-n", "120", "-l", "1000", "-f", "links.txt", "-o", "output"};
    private ParserParameters parser;

    @Before
    public void initTest() {
        parser = new ParserParameters(argsFull);
    }

    @Test
    public void testGetNumberOfThreads() throws Exception {
        assertEquals(parser.getNumberOfThreads(), 120);
    }

    @Test
    public void testGetMaxDownloadSpeed() throws Exception {
        assertEquals(parser.getMaxDownloadSpeed(), 1000);
    }

    @Test
    public void testGetFileNameWithLinks() throws Exception {
        assertEquals(parser.getFileNameWithLinks(), "links.txt");
    }

    @Test
    public void testGetOutputFolder() throws Exception {
        assertEquals(parser.getOutputFolder(), "output");
    }
}