package cherkasov.com;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by hawk on 08.02.2017.
 */
public class ParserLinksTest {
    private ParserLinks parserLinks;

    @Before
    public void setUp() throws Exception {
        parserLinks = new ParserLinks("C:\\java\\downcons\\src\\test\\resources\\links.txt");

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void parserTest(){
        assertSame(new DownloadEntity("http://example.com/archive.zip", "my_archive.zip"), parserLinks.getQueueTasks().poll());
    }

}