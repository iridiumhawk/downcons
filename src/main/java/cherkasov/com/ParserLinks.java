package cherkasov.com;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;

import static cherkasov.com.Main.LOG;

//parsing file with links and put them in tasks queue
public class ParserLinks {
    private final ConcurrentLinkedQueue<TaskEntity> queueTasks = new ConcurrentLinkedQueue<>();

    public ParserLinks(String fileName) {

        parseLinks(fileName);

        LOG.log(Level.INFO, "Parsing file with links done");
    }

    private void parseLinks(String fileName) {

        if (!Files.exists(Paths.get(fileName))) {
            LOG.log(Level.WARNING, "File whit links does not exist!");
            System.exit(-1);
        }

        try {
            List<String> lines = Files.readAllLines(Paths.get(fileName), StandardCharsets.UTF_8);

            for (String line : lines) {

                //if line is fewer than 3 chars, then this line is useless
                if (line.length() < 3) {
                    continue;
                }

                char firstCharInLine = line.charAt(0);

                //comment line, go to next
                if (firstCharInLine == '#') {
                    continue;
                }

                String[] urlAndFileName = line.trim().split(" ");

                //add tasks to concurrency queue, from which threads will take url for download
                if (urlAndFileName.length >= 2) {
                    queueTasks.add(new TaskEntity(urlAndFileName[0], urlAndFileName[1]));
                }
            }
        } catch (IOException e) {
            LOG.log(Level.WARNING, e.getMessage());
        }
    }

    public ConcurrentLinkedQueue<TaskEntity> getQueueTasks() {
        return queueTasks;
    }
}

