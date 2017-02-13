package cherkasov.com;

import org.junit.Before;
import org.junit.Test;

//full test
public class MainTest {
    private final String[] argsAllFullCorrect = {"-n", "5", "-l", "100k", "-f", "links.txt", "-o", "output"};


    @Before
    public void initTest(){
        new Main();
    }

    @Test
    public void testMainArgsFullCorrect() throws Exception {
        Main.main(argsAllFullCorrect);
    }

  /*  @Test
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
*/
}