package cherkasov.com;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;

import static cherkasov.com.ProjectLogger.LOG;

//parsing file with links and put them in tasks queue
public class ParserLinks {
    private final String fileName;


    public ParserLinks(String fileName) {
        this.fileName = fileName;

    }

    public List<String> loadFile() throws IOException {

        if (!Files.exists(Paths.get(fileName))) {
            throw  new FileNotFoundException("File with links does not exist.");
        }

        List<String> lines = Files.readAllLines(Paths.get(fileName), StandardCharsets.UTF_8);

        return lines;
    }


    public ConcurrentLinkedQueue<TaskEntity> parseLinks(final List<String> lines)  {

        if (lines == null) {
            throw new NullPointerException("List of links is null.");
        }

        final ConcurrentLinkedQueue<TaskEntity> queueTasks = new ConcurrentLinkedQueue<>();

        for (String line : lines) {

            //if line is fewer than 12 chars length, then this line is useless
            if (line.length() < 12) {
                continue;
            }

            //comment line, go to next
            if (line.charAt(0) == '#') {
                continue;
            }

            String[] urlAndFileName = line.trim().split(" ");

            //add tasks to concurrency queue, from which threads will take url for download
            if (urlAndFileName.length >= 2) {
                queueTasks.add(new TaskEntity(urlAndFileName[0], urlAndFileName[1]));
            }
        }

        LOG.log(Level.INFO, "Parsing file with links done");

        return queueTasks;
    }
}

