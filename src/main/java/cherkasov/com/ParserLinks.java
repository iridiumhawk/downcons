package cherkasov.com;

import java.util.Map;
import java.util.logging.Level;
import static cherkasov.com.Main.LOG;


//parsing file with links
public class ParserLinks {
    private Map<String, String> linksFiles;

    public ParserLinks(String fileName) {
        parse(fileName);

        LOG.log(Level.INFO, "parsing file with links done");
    }

    private void parse(String fileName) {

    }
}
