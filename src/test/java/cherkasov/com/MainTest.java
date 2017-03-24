package cherkasov.com;

import org.junit.Ignore;
import org.junit.Test;

//full test
public class MainTest {
    private final String[] ARGS_ALL_FULL_CORRECT = {"-n", "6", "-l", "300000", "-f", "links1.txt", "-o", "output", "-d", "true"};


    @Ignore
    @Test
    public void testMainArgsFullCorrect() throws Exception {
        Main.main(ARGS_ALL_FULL_CORRECT);
    }


}