package cherkasov.com;

import org.junit.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.Assert.*;

public class ParserParametersTest {
    private final String[] argsFull = {"-n", "120", "-l", "1000k", "-f", "test.txt", "-o", "output", "-d", "false"};
    private final String[] argsCrack = {"-n", "-l", "1000", "-f", "-f", "test.txt", "-o", "output"};
    private final String[] argsWithMissedParam = {"-n", "-l", "1000", "-f", "links.txt", "-o", "output"};
    private final String[] argsWithMissedParamAtEnd = {"-n", "120", "-l", "1000", "-f", "links.txt", "-o"};
    private final String[] argsWithFileNameEmpty = {"-n", "120", "-l", "1000", "-f", "", "-o", "output"};
    private final String[] argsWithFileNameMissed = {"-n", "120", "-l", "1000", "-f", "-o", "output"};
    private final String[] argsNotFull = {"-f", "links.txt"};
    private final String[] argsWithNoParam = {""};

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
        parser = new ParserParameters(argsFull);
        Parameters parametersExpected = parser.parseArgs();

        Parameters parametersActual = new Parameters(120, 1024000, "test.txt", "output", false);

        assertEquals(parametersExpected, parametersActual);
    }

    @Test
    public void testParseArgsNoParams() throws Exception {
        parser = new ParserParameters(argsWithNoParam);
        Parameters parametersExpected = parser.parseArgs();

        Parameters parametersActual = new Parameters(1, 1000000, "links.txt", "download", false);

        assertEquals(parametersExpected, parametersActual);
    }

    @Test
    public void testParseArgsCracks() throws Exception {
        parser = new ParserParameters(argsCrack);
        Parameters parametersExpected = parser.parseArgs();

        Parameters parametersActual = new Parameters(1, 1000, "test.txt", "output", false);

        assertEquals(parametersExpected, parametersActual);
    }


    @Test
    public void testParseIntoNumber() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        Class clazz = parser.getClass();

        Method method = clazz.getDeclaredMethod("parseIntoNumber", String.class);
        method.setAccessible(true);

        assertEquals(method.invoke(parser, "2M"), 2 * 1024 * 1024);
        assertEquals(method.invoke(parser, "2k"), 2 * 1024);
        assertEquals(method.invoke(parser, "23456"), 23456);
        assertEquals(method.invoke(parser, "-1"), 0);
        assertEquals(method.invoke(parser, "x"), 0);
        assertEquals(method.invoke(parser, ""), 0);
    }
}