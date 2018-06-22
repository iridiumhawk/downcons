package cherkasov.com;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;

import static cherkasov.com.ProjectLogger.LOG;

/**
 *
 * Parses the file with links and put them in the tasks queue
 */
public class ParserLinks {
    private final String fileName;

    public ParserLinks(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Loads the file with links and splits it into the lines.
     * @return      a list of lines reading from the file
     * @throws      IOException if the file does not exist
     */
    public List<String> loadFile() throws IOException {

        if (!Files.exists(Paths.get(fileName))) {
            throw  new FileNotFoundException("File with links does not exist.");
        }

        return Files.readAllLines(Paths.get(fileName), StandardCharsets.UTF_8);
    }

    /**
     * Adds tasks into a queue.
     * @param lines     the list with url and file name per line
     * @return          a task queue for downloading
     * @throws          NullPointerException if lines is null
     */
    public Queue<TaskEntity> parseLinks(final List<String> lines)  {

        if (lines == null) {
            throw new NullPointerException("List of links is null.");
        }

        final Queue<TaskEntity> queueTasks = new ConcurrentLinkedQueue<>();

        for (String line : lines) {

            //if line is fewer than 12 chars length, then line is useless because there are not urls
            if (line.length() < 12) {
                continue;
            }

            //comment line, go to next line
            if (line.charAt(0) == '#') {
                continue;
            }

            String[] urlAndFileName = line.trim().split(" ");

            //adds tasks to concurrency queue, from which the threads will be take url for download
            if (urlAndFileName.length >= 2) {
                queueTasks.add(new TaskEntity(urlAndFileName[0], urlAndFileName[1]));
            }
        }

        LOG.log(Level.INFO, "Parsing file with links was done");

        return queueTasks;
    }
}

