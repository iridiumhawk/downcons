package cherkasov.com.integration;

import cherkasov.com.Manager;
import org.junit.Ignore;
import org.junit.Test;

import java.util.logging.Level;

import static cherkasov.com.ProjectLogger.LOG;

/**
 *
 * Integration test of full functionality.
 * Ignored by gradle. Must be started manually.
 */

public class ManagerTest {
    private final String[] ARGS_ALL_FULL_CORRECT = {"-n", "5", "-l", "1M", "-f", "links.txt", "-o", "output", "-d"};


    @Test
    public void testMainArgsFullCorrect() throws Exception {
        Manager.main(ARGS_ALL_FULL_CORRECT);

        LOG.log(Level.INFO, "ManagerTest pass");

    }
}