package cherkasov.com;

import java.util.Queue;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.junit.Assert.*;

/**
 * Tests correct parsing links file
 */
public class ParserLinksTest {
    private ParserLinks parserLinks;

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        parserLinks = new ParserLinks("not_exist_file.txt");
    }

    @Test
    public void testLoadFileDoesNotExistFile() throws IOException {
        exception.expect(FileNotFoundException.class);
        parserLinks.loadFile();
    }

    @Test
    public void testParseLinksWithNullList() {
        exception.expect(NullPointerException.class);
        parserLinks.parseLinks(null);
    }

    @Test
    public void testParseLinksCorrectAddedTask() {

        List<String> lines = new ArrayList<>();
        lines.add("http://example.com/archive.zip my_archive.zip");

        Queue<TaskEntity> queue = parserLinks.parseLinks(lines);

        TaskEntity actual = new TaskEntity("http://example.com/archive.zip", "my_archive.zip");
        assertEquals(queue.poll(), actual);
    }

    @Test
    public void testParseLinksWithNoCorrectLines() {

        List<String> lines = new ArrayList<>();
        lines.add("http m");

        Queue<TaskEntity> actual = parserLinks.parseLinks(lines);

        assertTrue(actual.isEmpty());
    }

    @Test
    public void testParseLinksEmptyQueueWithEmptyList() {

        Queue<TaskEntity> actual = parserLinks.parseLinks(new ArrayList<>());

        assertTrue(actual.isEmpty());
    }
}