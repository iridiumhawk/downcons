package cherkasov.com;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;

import static cherkasov.com.Main.LOG;

//parsing file with links
public class ParserLinks {
    private final ConcurrentLinkedQueue<DownloadEntity> queueTasks = new ConcurrentLinkedQueue<>();

    public ConcurrentLinkedQueue<DownloadEntity> getQueueTasks() {
        return queueTasks;
    }

    public ParserLinks(String fileName) {

        parseLinks(fileName);

        LOG.log(Level.INFO, "parsing file with links done");
    }

    private void parseLinks(String fileName) {

        try {
            List<String> lines = Files.readAllLines(Paths.get(fileName), StandardCharsets.UTF_8);

            for (String line : lines) {

                if (line.length() < 3) {
                    continue;
                }

                char firstInLine = line.charAt(0);
                //comment line
                if (firstInLine == '#') {
                    continue;
                }

//                System.out.println(line);

                String[] urlName = line.trim().split(" "); //trim?
                if (urlName.length >= 2) {
                    queueTasks.add(new DownloadEntity(urlName[0], urlName[1]));
                }

//                LOG.log(Level.INFO, urlName[0] +" : "+ urlName[1]);
            }

        } catch (IOException e) {
            LOG.log(Level.WARNING, e.getMessage());
        }

    }
}

