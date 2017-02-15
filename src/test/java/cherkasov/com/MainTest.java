package cherkasov.com;

import org.junit.Before;
import org.junit.Test;

//full test
public class MainTest {
    private final String[] argsAllFullCorrect = {"-n", "5", "-l", "500000", "-f", "links.txt", "-o", "output"};


    @Before
    public void initTest(){
        new Main();
    }

    @Test
    public void testMainArgsFullCorrect() throws Exception {
        Main.main(argsAllFullCorrect);
    }

}