package cherkasov.com;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;

import static cherkasov.com.ProjectLogger.LOG;

/**
java -jar utility.jar -n 5 -l 2000k -o output_folder -f links.txt

    -n количество одновременно качающих потоков (1,2,3,4....)
    -l общее ограничение на скорость скачивания, для всех потоков,
    размерность - байт/секунда, можно использовать суффиксы k,m (k=1024, m=1024*1024)
    -f путь к файлу со списком ссылок
    -o имя папки, куда складывать скачанные файлы

В конце работы утилита должна выводить статистику - время работы и количество скачанных байт.

Формат файла со ссылками:
<HTTP ссылка><пробел><имя файла, под которым его надо сохранить>

пример:
http://example.com/archive.zip my_archive.zip
http://example.com/image.jpg picture.jpg

added key "-d" for debug messages
 */

/**
 * Entry point for application.
 * Manages all subtasks.
 */
public class Manager {
    private final String[] args;
    private long workingTime = 0L;

    public static void main(String[] args) {
        Manager manager = new Manager(args);

        manager.execute();

        LOG.log(Level.INFO, "End program");

    }

    public Manager(String[] args) {
        this.args = args;
    }

    public void execute() {

        LOG.setLevel(Level.WARNING);

        //parsing command line parameters
        final ParserParameters parserParameters = new ParserParameters(args);
        final Parameters parameters = parserParameters.parseArgs();

        if (parameters == null) {
            LOG.log(Level.WARNING, "Parameters incorrect.");
            System.exit(1);
        }

        if (parameters.isDebug()) {
            LOG.setLevel(Level.INFO);
        }

        //parsing links file
        final ParserLinks parserLinks = new ParserLinks(parameters.getFileNameWithLinks());
        List<String> stringsFromFile;

        try {
            stringsFromFile = parserLinks.loadFile();
        } catch (FileNotFoundException e) {
            LOG.log(Level.SEVERE, "Abort. Reason: " + e.getMessage());
//            e.printStackTrace();
            return;
//            System.exit(2);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Error reading links file. Reason: " + e.getMessage());
//            e.printStackTrace();
            return;
//            System.exit(3);
        }

        final Queue<TaskEntity> queueTasks = parserLinks.parseLinks(stringsFromFile);

        //gets output folder
        Path dir = Paths.get(parameters.getOutputFolder());

        //checks for exist and creates output folder if needed
        if (!Files.exists(dir) || !Files.isDirectory(dir)) {
            try {
                Files.createDirectory(dir);
                LOG.log(Level.INFO, "Created Directory. " + dir.getFileName());
            } catch (SecurityException sec) {
                LOG.log(Level.SEVERE, "Create Directory SecurityException. " + sec.getMessage());
//                sec.printStackTrace();
//                System.exit(4);
                    return;
            } catch (IOException e) {
                LOG.log(Level.SEVERE, "Create Directory Exception. " + e.getMessage());
//                e.printStackTrace();
//                System.exit(4);
            return;
            }

        }

        workingTime = System.currentTimeMillis();

        //create downloader instance
        final Downloader downloader = new Downloader(queueTasks, parameters, ConnectionType.HTTP);
        downloader.start();

        workingTime = System.currentTimeMillis() - workingTime;

        System.out.println(MessageFormat.format(
                "Time spent for all tasks: {0} seconds",
                workingTime / 1000));

        long downloadedBytes = downloader.getDownloadedBytesSummary().get();

        System.out.println(MessageFormat.format(
                "Total downloaded: {0} byte ({1} MegaByte), average speed: {2} byte/sec",
                downloadedBytes,
                downloadedBytes / 1024 / 1024,
                downloadedBytes * 1000 / workingTime ));
    }
}
