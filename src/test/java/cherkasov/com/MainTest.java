package cherkasov.com;

import org.junit.Test;

/**
 * Created by hawk on 27.11.2016.
 */
public class MainTest {
    final String[] argsFullCorrect = {"-n","120","-l","1000","-f","links.txt","-o","output"};
    final String[] argsMissedParam = {"-n","-l","1000","-f","links.txt","-o","output"};
    final String[] argsMissedParamAtEnd = {"-n","120","-l","1000","-f","links.txt","-o"};
    final String[] argsFileNameEmpty = {"-n","120","-l","1000","-f","","-o","output"};
    final String[] argsFileNameMissed = {"-n","120","-l","1000","-f","-o","output"};
    final String[] argsNotFull = {"-f","links.txt"};

    @Test

    public void testMain() throws Exception {

        Main.main(argsNotFull);
    }
}