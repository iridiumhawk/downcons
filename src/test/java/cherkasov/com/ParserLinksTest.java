package cherkasov.com;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.junit.Assert.*;

/**
 * Created by hawk on 08.02.2017.
 */
public class ParserLinksTest {
    private ParserLinks parserLinks;

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        parserLinks = new ParserLinks("123456789.txt");
    }

    @After
    public void tearDown() throws Exception {

    }


    @Test
    public void loadFileFailTest() throws Exception {
//        exception.expect(FileNotFoundException.class);
//        parserLinks.loadFile();
    }


    @Test
    public void parseLinksTest() {

        List<String> lines = new ArrayList<>();
        lines.add("http://example.com/archive.zip my_archive.zip");

        ConcurrentLinkedQueue<TaskEntity> queue = parserLinks.parseLinks(lines);

        assertEquals(queue.poll(), new TaskEntity("http://example.com/archive.zip", "my_archive.zip"));

    }


}