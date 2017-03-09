package cherkasov.com;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;

import static cherkasov.com.Main.LOG;

public class Manager {
    private final String[] args;

    public Manager(String[] args) {
        this.args = args;
    }

    public long execute() {

        //parsing parameters
        final ParserParameters parserParameters = new ParserParameters(args);
        final Parameters parameters = parserParameters.parseArgs();

        //parsing links file
        final ParserLinks parserLinks = new ParserLinks(parameters.getFileNameWithLinks());
        final ConcurrentLinkedQueue<TaskEntity> queueTasks = parserLinks.parseLinks(parserLinks.loadFile());

        //get output folder
        Path dir = Paths.get(parameters.getOutputFolder());

        //check for exist and create output folder
        if (!Files.isDirectory(dir)) {
            try {
                Files.createDirectory(dir);
            } catch (IOException e) {
                LOG.log(Level.WARNING, "create Directory Exception, " + e.getMessage());
                e.printStackTrace();
            }
        }

        //create downloader instance
        final Downloader downloader = new Downloader(queueTasks, parameters);
        downloader.start();

        //return atomic long
        return downloader.getDownloadedBytesSummary().get();
    }
}
