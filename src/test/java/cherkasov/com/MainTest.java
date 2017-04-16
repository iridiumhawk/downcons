package cherkasov.com;

import org.junit.Ignore;
import org.junit.Test;

//full test
public class MainTest {
    private final String[] ARGS_ALL_FULL_CORRECT = {"-n", "5", "-l", "1M", "-f", "links.txt", "-o", "output", "-d"};

    @Ignore
    @Test
    public void testMainArgsFullCorrect() throws Exception {
        Main.main(ARGS_ALL_FULL_CORRECT);
    }
}