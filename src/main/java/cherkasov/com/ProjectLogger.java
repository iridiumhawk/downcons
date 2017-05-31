package cherkasov.com;

import java.util.logging.*;

/**
 * Logger for all classes in the project
 */
public class ProjectLogger {
    public static final Logger LOG = ProjectLogger.initFileLogging(Manager.class.getSimpleName());

//    private static final String LOG_FILE_NAME = "downloader.log";
//    private static final int LIMIT_LOG_FILE_SIZE = 1000000; // 1 Mb
    private static final Level LOGGER_LEVEL = Level.OFF;

    /**
     * Initializes logger
     * @param loggerName    name of logger
     * @return              instance of logger
     */
    private static Logger initFileLogging(String loggerName) {

        Logger logger = Logger.getLogger(loggerName);

        logger.setLevel(LOGGER_LEVEL);

        // Create txt Formatter
        SimpleFormatter formatterTxt = new SimpleFormatter();

/*
        FileHandler fileHandler;

        try {
            fileHandler = new FileHandler(LOG_FILE_NAME, LIMIT_LOG_FILE_SIZE, 1, true);
            fileHandler.setFormatter(formatterTxt);

            logger.addHandler(fileHandler);

        } catch (IOException e) {
            e.printStackTrace();
        }
*/

        //console formatter
        Handler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(formatterTxt);

        return logger;
    }
}
