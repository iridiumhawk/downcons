package cherkasov.com;

import java.io.IOException;
import java.util.logging.*;

public class ProjectLogger {
    public static final Logger LOG = ProjectLogger.initFileLogging(Main.class.getSimpleName());

    private static final String LOG_FILE_NAME = "downloader.log";
    private static final int LIMIT_LOG_FILE_SIZE = 1000000; // 1 Mb
    private static final Level LOGGER_LEVEL = Level.OFF;

    //todo change format logger
    private static Logger initFileLogging(String loggerName) {

        Logger logger = Logger.getLogger(loggerName);

        logger.setLevel(LOGGER_LEVEL);

        FileHandler fileHandler;

        // Create txt Formatter
        SimpleFormatter formatterTxt = new SimpleFormatter();

        try {
            fileHandler = new FileHandler(LOG_FILE_NAME, LIMIT_LOG_FILE_SIZE, 1, true);
            fileHandler.setFormatter(formatterTxt);
            logger.addHandler(fileHandler);

        } catch (IOException e) {
            e.printStackTrace();
        }

        //console formatter
        Handler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(formatterTxt);

        return logger;
    }
}
