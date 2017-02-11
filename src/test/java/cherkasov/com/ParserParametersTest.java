package cherkasov.com;

import org.junit.*;

import static org.junit.Assert.*;

public class ParserParametersTest {
    private  final String[] argsFull = {"-n", "120", "-l", "1000", "-f", "links.txt", "-o", "output"};
    //    private final String[] argsCrack = {"-n", "-l", "1000", "-f", "-f" , "links.txt", "-o", "output"};
    private final String[] argsWithMissedParam = {"-n", "-l", "1000", "-f", "links.txt", "-o", "output"};
    private final String[] argsWithMissedParamAtEnd = {"-n", "120", "-l", "1000", "-f", "links.txt", "-o"};
    private final String[] argsWithFileNameEmpty = {"-n", "120", "-l", "1000", "-f", "", "-o", "output"};
    private final String[] argsWithFileNameMissed = {"-n", "120", "-l", "1000", "-f", "-o", "output"};
    private final String[] argsNotFull = {"-f", "links.txt"};
    private final String[] argsWithNoParam = {""};

    private  ParserParameters parser;

    @Before
    public  void initTest() {
        parser = new ParserParameters(argsFull);
    }

    @After
    public void tearDown() throws Exception {
//        parser = null;
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

    @Test
    public void testParseIntoNumber() {
//        assertEquals(parser.parseIntoNumber("2M"), 2*1024*1024);
//        assertEquals(parser.parseIntoNumber("2k"), 2*1024);
//        assertEquals(parser.parseIntoNumber("23456"), 23456);
    }
}