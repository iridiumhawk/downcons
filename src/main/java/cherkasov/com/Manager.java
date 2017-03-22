package cherkasov.com;

import org.apache.commons.cli.ParseException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;

import static cherkasov.com.ProjectLogger.LOG;

public class Manager {
    private final String[] args;
    private long workingTime = 0L;


    public Manager(String[] args) {
        this.args = args;
    }

    public void execute() {

        LOG.setLevel(Level.WARNING);

        LOG.log(Level.INFO, "Program started");

        //parsing parameters
        final ParserParameters parserParameters = new ParserParameters(args);
        final Parameters parameters = parserParameters.parseArgs();

        if (parameters == null) {
            LOG.log(Level.SEVERE, "Parameters incorrect.");
            System.exit(1);
        }

        //parsing links file
        final ParserLinks parserLinks = new ParserLinks(parameters.getFileNameWithLinks());
        List<String> stringsFromFile = null;

        try {
            stringsFromFile = parserLinks.loadFile();
        } catch (FileNotFoundException e) {
            LOG.log(Level.SEVERE, "Abort. Reason: " + e.getMessage());
            e.printStackTrace();
            System.exit(2);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Error reading links file. Reason: " + e.getMessage());
            e.printStackTrace();
            System.exit(3);
        }

        final ConcurrentLinkedQueue<TaskEntity> queueTasks = parserLinks.parseLinks(stringsFromFile);

        //get output folder
        Path dir = Paths.get(parameters.getOutputFolder());

        //check for exist and create output folder
        if (!Files.isDirectory(dir)) {
            try {
                Files.createDirectory(dir);
            } catch (IOException e) {
                LOG.log(Level.SEVERE, "Create Directory Exception. " + e.getMessage());
                e.printStackTrace();
                System.exit(3);
            }
        }

        workingTime = System.currentTimeMillis();
        //create downloader instance
        final Downloader downloader = new Downloader(queueTasks, parameters, Downloader.ConnectionType.HTTP);
        downloader.start();
        workingTime = (System.currentTimeMillis() - workingTime) / 1000; // in seconds

        System.out.println(MessageFormat.format(
                "Time spent for all tasks: {0} seconds",
                workingTime));

        long downloadedBytes = downloader.getDownloadedBytesSummary().get();

        System.out.println(MessageFormat.format(
                "Total downloaded: {0} byte ({1} MegaByte), average speed: {2} byte/sec",
                downloadedBytes,
                downloadedBytes / 1024 / 1024,
                downloadedBytes / (workingTime)));
        LOG.log(Level.INFO, "Program ended");
        //return bytes from atomic

//        return downloader.getDownloadedBytesSummary().get();
    }
}
