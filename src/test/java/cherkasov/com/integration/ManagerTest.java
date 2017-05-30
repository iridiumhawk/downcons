package cherkasov.com.integration;

import cherkasov.com.Manager;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * Integration test of full functionality
 */

public class ManagerTest {
    private final String[] ARGS_ALL_FULL_CORRECT = {"-n", "5", "-l", "1M", "-f", "links.txt", "-o", "output", "-d"};

    @Ignore
    @Test
    public void testMainArgsFullCorrect() throws Exception {
        Manager.main(ARGS_ALL_FULL_CORRECT);
    }
}