package cherkasov.com;

import org.junit.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.Assert.*;

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
        final String[] argsFull = {"-n", "120", "-l", "1000k", "-f", "test.txt", "-o", "output", "-d", "false"};

        parser = new ParserParameters(argsFull);
        Parameters parametersExpected = parser.parseArgs();

        Parameters parametersActual = new Parameters(120, 1024000, "test.txt", "output", false);

        assertEquals(parametersExpected, parametersActual);
    }

    @Test
    public void testParseArgsNoParams() throws Exception {
        final String[] argsWithNoParam = {""};

        parser = new ParserParameters(argsWithNoParam);
        Parameters parametersExpected = parser.parseArgs();

        Parameters parametersActual = new Parameters(1, 1000000, "links.txt", "download", false);

        assertEquals(parametersExpected, parametersActual);
    }

    @Test
    public void testParseArgsCracks() throws Exception {
        final String[] argsCrack = {"-n", "-l", "1000", "-f", "-f", "test.txt", "-o", "output"};

        parser = new ParserParameters(argsCrack);
        Parameters parametersExpected = parser.parseArgs();

        Parameters parametersActual = new Parameters(1, 1000, "test.txt", "output", false);

        assertEquals(parametersExpected, parametersActual);
    }

    @Test
    public void testParseArgsMissedParam() throws Exception {
        final String[] argsWithMissedParam = {"-n", "-l", "1000", "-f", "test.txt", "-o", "output"};

        parser = new ParserParameters(argsWithMissedParam);
        Parameters parametersExpected = parser.parseArgs();

        Parameters parametersActual = new Parameters(1, 1000, "test.txt", "output", false);

        assertEquals(parametersExpected, parametersActual);
    }

    @Test
    public void testParseArgsMissedParamAtEnd() throws Exception {
         final String[] argsWithMissedParamAtEnd = {"-n", "120", "-l", "1000", "-f", "test.txt", "-o"};

        parser = new ParserParameters(argsWithMissedParamAtEnd);
        Parameters parametersExpected = parser.parseArgs();

        Parameters parametersActual = new Parameters(120, 1000, "test.txt", "download", false);

        assertEquals(parametersExpected, parametersActual);
    }

    @Test
    public void testParseArgsFileNameEmpty() throws Exception {
         final String[] argsWithFileNameEmpty = {"-n", "120", "-l", "1000", "-f", "", "-o", "output"};

        parser = new ParserParameters(argsWithFileNameEmpty);
        Parameters parametersExpected = parser.parseArgs();

        Parameters parametersActual = new Parameters(120, 1000, "links.txt", "output", false);

        assertEquals(parametersExpected, parametersActual);
    }

    @Test
    public void testParseArgsFileNameMissed() throws Exception {
         final String[] argsWithFileNameMissed = {"-n", "120", "-l", "1000", "-f", "-o", "output"};

        parser = new ParserParameters(argsWithFileNameMissed);
        Parameters parametersExpected = parser.parseArgs();

        Parameters parametersActual = new Parameters(120, 1000, "links.txt", "output", false);

        assertEquals(parametersExpected, parametersActual);
    }

    @Test
    public void testParseArgsFolderNameMissed() throws Exception {
         final String[] argsWithFolderNameMissed = {"-n", "120", "-l", "1000", "-f", "test.txt" ,"-o", "-d" , "false"};

        parser = new ParserParameters(argsWithFolderNameMissed);
        Parameters parametersExpected = parser.parseArgs();

        Parameters parametersActual = new Parameters(120, 1000, "test.txt", "download", false);

        assertEquals(parametersExpected, parametersActual);
    }

    @Test
    public void testParseArgsNotFull() throws Exception {
        final String[] argsNotFull = {"-f", "test.txt"};

        parser = new ParserParameters(argsNotFull);
        Parameters parametersExpected = parser.parseArgs();

        Parameters parametersActual = new Parameters(1, 1000000, "test.txt", "download", false);

        assertEquals(parametersExpected, parametersActual);
    }


    @Test
    public void testParseIntoNumber() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        Class clazz = parser.getClass();

        Method method = clazz.getDeclaredMethod("parseIntoNumber", String.class);
        method.setAccessible(true);

        assertEquals(method.invoke(parser, "2M"), 2 * 1024 * 1024);
        assertEquals(method.invoke(parser, "2m"), 2 * 1024 * 1024);
        assertEquals(method.invoke(parser, "2k"), 2 * 1024);
        assertEquals(method.invoke(parser, "2K"), 2 * 1024);
        assertEquals(method.invoke(parser, "123456"), 123456);
        assertEquals(method.invoke(parser, "-1"), 0);
        assertEquals(method.invoke(parser, "x"), 0);
        assertEquals(method.invoke(parser, ""), 0);
    }
}