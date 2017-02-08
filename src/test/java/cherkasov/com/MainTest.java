package cherkasov.com;

import org.junit.Before;
import org.junit.Test;

//full test
public class MainTest {
    private final String[] argsAllFullCorrect = {"-n", "2", "-l", "1000", "-f", "C:\\java\\downcons\\src\\test\\resources\\links.txt", "-o", "output"};
    private final String[] argsWithMissedParam = {"-n", "-l", "1000", "-f", "links.txt", "-o", "output"};
    private final String[] argsWithMissedParamAtEnd = {"-n", "120", "-l", "1000", "-f", "links.txt", "-o"};
    private final String[] argsWithFileNameEmpty = {"-n", "120", "-l", "1000", "-f", "", "-o", "output"};
    private final String[] argsWithFileNameMissed = {"-n", "120", "-l", "1000", "-f", "-o", "output"};
    private final String[] argsNotFull = {"-f", "links.txt"};
    private final String[] argsWithNoParam = {""};

    @Before
    public void initTest(){
        new Main();
    }

    @Test
    public void testMainArgsFullCorrect() throws Exception {
        Main.main(argsAllFullCorrect);
    }

    @Test
    public void testMainArgsWithMissedParam() throws Exception {
        Main.main(argsWithMissedParam);
    }

    @Test
    public void testMainArgsWithMissedParamAtEnd() throws Exception {
        Main.main(argsWithMissedParamAtEnd);
    }

    @Test
    public void testMainArgsWithFileNameEmpty() throws Exception {//todo check Exception
        Main.main(argsWithFileNameEmpty);
    }

    @Test
    public void testMainArgsWithFileNameMissed() throws Exception {//todo check Exception
        Main.main(argsWithFileNameMissed);
    }

    @Test
    public void testMainArgsNotFull() throws Exception {
        Main.main(argsNotFull);
    }

    @Test
    public void testMainArgsWithNoParam() throws Exception {//todo check Exception
        Main.main(argsWithNoParam);
    }

}