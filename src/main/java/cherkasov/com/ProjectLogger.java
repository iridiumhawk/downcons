package cherkasov.com;

import java.io.IOException;
import java.util.logging.*;

class ProjectLogger {

    private static final String LOG_FILE_NAME = "downloader.log";
    private static final int LIMIT_FILE_SIZE = 1000000; // 1 Mb

    //todo change format logger
    static Logger initFileLogging(String loggerName) {

        Logger logger = Logger.getLogger(loggerName);

        logger.setLevel(Level.INFO);

        FileHandler fileHandler = null;

        // Create txt Formatter
        SimpleFormatter formatterTxt = new SimpleFormatter();

        try {
            fileHandler = new FileHandler(LOG_FILE_NAME, LIMIT_FILE_SIZE, 1, true);
            fileHandler.setFormatter(formatterTxt);
            logger.addHandler(fileHandler);

        } catch (IOException e) {
            e.printStackTrace();
        }

        //console formatter
        Handler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(formatterTxt);

//        logger.addHandler(consoleHandler);
        return logger;
    }
}
