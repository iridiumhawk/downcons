package cherkasov.com;

import org.junit.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.Assert.*;

/**
 * Tests correct parsing of command line parameters
 */

public class ParserParametersTest {
    private ParserParameters parser;

    @Before
    public void initTest() {
        parser = new ParserParameters();
    }

    @After
    public void tearDown() throws Exception {
        parser = null;
    }

    @Test
    public void testParseArgsFullParams() throws Exception {
        final String[] argsFull = {"-n", "10", "-l", "1000k", "-f", "test.txt", "-o", "output", "-d"};

        parser = new ParserParameters();
        Parameters parametersExpected = parser.parseArgs(argsFull);

        Parameters parametersActual = new Parameters(10, 1024000, "test.txt", "output", true);

        assertEquals(parametersExpected, parametersActual);
    }

    @Test
    public void testParseArgsNoParams() throws Exception {
        final String[] argsWithNoParam = {""};

        parser = new ParserParameters();
        Parameters parametersExpected = parser.parseArgs(argsWithNoParam);

        assertEquals(parametersExpected, null);
    }

    @Test
    public void testParseArgsCracks() throws Exception {
        final String[] argsCrack = {"-n", "10", "-l", "1000", "-f", "-f", "test.txt", "-o", "output"};

        parser = new ParserParameters();
        Parameters parametersExpected = parser.parseArgs(argsCrack);

        assertEquals(parametersExpected, null);
    }

    @Test
    public void testParseArgsMissedParam() throws Exception {
        final String[] argsWithMissedParam = {"-n", "-l", "1000", "-f", "test.txt", "-o", "output"};

        parser = new ParserParameters();
        Parameters parametersExpected = parser.parseArgs(argsWithMissedParam);

        assertEquals(parametersExpected, null);
    }


    @Test
    public void testParseArgsFileNameEmpty() throws Exception {
        final String[] argsWithFileNameEmpty = {"-n", "10", "-l", "1000", "-f", "", "-o", " "};

        parser = new ParserParameters();
        Parameters parametersExpected = parser.parseArgs(argsWithFileNameEmpty);

        assertEquals(parametersExpected, null);
    }

    @Test
    public void testParseArgsNotFull() throws Exception {
        final String[] argsNotFull = {"-f", "test.txt"};

        parser = new ParserParameters();
        Parameters parametersExpected = parser.parseArgs(argsNotFull);

        assertEquals(parametersExpected, null);
    }


    @Test
    public void testParseIntoNumber() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        Class clazz = parser.getClass();

        Method method = clazz.getDeclaredMethod("parseIntoNumber", String.class);
        method.setAccessible(true);

        assertEquals(method.invoke(parser, "2M"), (long) 2 * 1024 * 1024);
        assertEquals(method.invoke(parser, "2m"), (long) 2 * 1024 * 1024);
        assertEquals(method.invoke(parser, "2K"), (long) 2 * 1024);
        assertEquals(method.invoke(parser, "2k"), (long) 2 * 1024);
        assertEquals(method.invoke(parser, "123456"), 123456L);
        assertEquals(method.invoke(parser, "10000000000"), 10000000000L);
        assertEquals(method.invoke(parser, Long.toString(Long.MAX_VALUE)), 9223372036854775807L);
        assertEquals(method.invoke(parser, Long.toString(Long.MAX_VALUE)+"123"), 0L);
        assertEquals(method.invoke(parser, "-1"), 0L);
        assertEquals(method.invoke(parser, "x"), 0L);
        assertEquals(method.invoke(parser, ""), 0L);
        assertEquals(method.invoke(parser, (Object) null), 0L);
    }
}