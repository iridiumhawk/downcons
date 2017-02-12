package cherkasov.com;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;

import static cherkasov.com.Main.LOG;

public class Manager {
    private final String[] args;

    public Manager(String[] args) {
        this.args = args;
    }

    public long execute() {

        //parsing parameters
        ParserParameters parserParameters = new ParserParameters(args);

        //parsing links file
        ParserLinks parserLinks = new ParserLinks(parserParameters.getFileNameWithLinks());

        //check and create output folder
        Path dir = Paths.get(parserParameters.getOutputFolder());

        if (!Files.isDirectory(dir)) {
            try {
                Files.createDirectory(dir);
            } catch (IOException e) {
                LOG.log(Level.WARNING, "createDirectory Exception, " + e.getMessage());
            }
        }

        //create concurrency queue for tasks, from which threads will take url for download
        Downloader downloader = new Downloader(parserLinks.getQueueTasks(), parserParameters);

        downloader.start();

        return downloader.getDownloadedBytesSum().get();
    }

}
