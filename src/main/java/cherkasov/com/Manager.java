package cherkasov.com;

import cherkasov.com.exceptions.IncorrectInputParameters;

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
 * java -jar utility.jar -n 5 -l 2000k -o output_folder -f links.txt
 * <p>
 * -n количество одновременно качающих потоков (1,2,3,4....)
 * -l общее ограничение на скорость скачивания, для всех потоков,
 * размерность - байт/секунда, можно использовать суффиксы k,m (k=1024, m=1024*1024)
 * -f путь к файлу со списком ссылок
 * -o имя папки, куда складывать скачанные файлы
 * <p>
 * В конце работы утилита должна выводить статистику - время работы и количество скачанных байт.
 * <p>
 * Формат файла со ссылками:
 * <HTTP ссылка><пробел><имя файла, под которым его надо сохранить>
 * <p>
 * пример:
 * http://example.com/archive.zip my_archive.zip
 * http://example.com/image.jpg picture.jpg
 * <p>
 * added key "-d" for debug messages
 */

/**
 * Entry point for application.
 * Manages all subtasks.
 */
public class Manager {
    private long workingTime = 0L;

    public Manager() {

    }

    public static void main(String[] args) throws Exception {

        Manager manager = new Manager();

        manager.execute(args);

        LOG.log(Level.INFO, "End program");

    }

    public void execute(String[] args) throws IncorrectInputParameters, IOException {

        LOG.setLevel(Level.WARNING);

        //parsing command line parameters
        final ParserParameters parserParameters = new ParserParameters();
        final Parameters parameters = parserParameters.parseArgs(args);

        if (parameters == null) {
            throw new IncorrectInputParameters("Parameters is null.");
        }

        if (parameters.isDebug()) {
            LOG.setLevel(Level.INFO);
        }

        //parsing links file
        final ParserLinks parserLinks = new ParserLinks(parameters.getFileNameWithLinks());
        List<String> stringsFromFile = null;

        stringsFromFile = parserLinks.loadFile();


        final Queue<TaskEntity> queueTasks = parserLinks.parseLinks(stringsFromFile);

        //gets output folder
        Path dir = Paths.get(parameters.getOutputFolder());

        //checks for exist and creates output folder if needed
        if (!Files.exists(dir) || !Files.isDirectory(dir)) {
            Files.createDirectory(dir);
            LOG.log(Level.INFO, "Created Directory. " + dir.getFileName());
        }

        workingTime = System.currentTimeMillis();

        //create downloader instance
        final Downloader downloader = new Downloader(queueTasks, parameters, ConnectionType.HTTP);
        downloader.start();

        workingTime = System.currentTimeMillis() - workingTime;

        LOG.info(MessageFormat.format("Time spent for all tasks: {0} seconds", workingTime / 1000));

        long downloadedBytes = downloader.getDownloadedBytesSummary().get();

        LOG.info(MessageFormat.format("Total downloaded: {0} byte ({1} MegaByte), average speed: {2} byte/sec", downloadedBytes, downloadedBytes / 1024 / 1024, downloadedBytes * 1000 / workingTime));
    }
}
