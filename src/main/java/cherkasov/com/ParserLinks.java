package cherkasov.com;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;

import static cherkasov.com.Main.LOG;

//parsing file with links and put them in tasks queue
public class ParserLinks {
    private final String fileName;


    public ParserLinks(String fileName) {
        this.fileName = fileName;

    }

    public List<String> loadFile() throws FileNotFoundException {

        if (!Files.exists(Paths.get(fileName))) {
            throw  new FileNotFoundException("File with links does not exist.");
        }

        List<String> lines = new ArrayList<>();
        try {
            lines = Files.readAllLines(Paths.get(fileName), StandardCharsets.UTF_8);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Error reading file. " + e.getMessage());
            e.printStackTrace();
        }
        return lines;
    }


    public ConcurrentLinkedQueue<TaskEntity> parseLinks(final List<String> lines) throws NullPointerException {

//        List<String> lines = loadFile();

        if (lines == null) {
            throw new RuntimeException("List of links and files is null.");
        }

        final ConcurrentLinkedQueue<TaskEntity> queueTasks = new ConcurrentLinkedQueue<>();

        for (String line : lines) {

            //if line is fewer than 3 chars length, then this line is useless
            if (line.length() < 3) {
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

