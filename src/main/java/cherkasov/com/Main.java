package cherkasov.com;

//   java -jar utility.jar -n 5 -l 2000k -o output_folder -f links.txt

/*
    -n количество одновременно качающих потоков (1,2,3,4....)
    -l общее ограничение на скорость скачивания, для всех потоков, размерность - байт/секунда, можно использовать суффиксы k,m (k=1024, m=1024*1024)
    -f путь к файлу со списком ссылок
    -o имя папки, куда складывать скачанные файлы*/

    /*
Формат файла со ссылками:

<HTTP ссылка><пробел><имя файла, под которым его надо сохранить>
пример:

http://example.com/archive.zip my_archive.zip
http://example.com/image.jpg picture.jpg
*/

/*
В конце работы утилита должна выводить статистику - время работы и количество скачанных байт.
*/

import java.io.IOException;
import java.util.logging.*;


public class Main {

    public final static Logger LOG = initLogging(Main.class.getSimpleName());//Logger.getLogger();
    private static Long workingTime;
    private static Long downloadedBytes = 0L;

    public static Logger initLogging(String loggerName) {
        Logger logger = Logger.getLogger(loggerName);
        logger.setLevel(Level.INFO);
        int limitFileSize = 1000000; // 1 Mb

        FileHandler fh = null;
        // Create txt Formatter
        SimpleFormatter formatterTxt = new SimpleFormatter();

        try {
            fh = new FileHandler("queue.log", limitFileSize, 1, true);
            fh.setFormatter(formatterTxt);
            logger.addHandler(fh);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //console formatter
        Handler ch = new ConsoleHandler();
        ch.setFormatter(formatterTxt);
//        logger.addHandler(ch);
        return logger;
    }

    public static void main(String[] args) {

        if (args.length == 0) {
            LOG.log(Level.WARNING, "input parameters absent");
            System.exit(-1);
        }

        LOG.log(Level.INFO, "program start");

        workingTime = System.currentTimeMillis();

        Manager manager = new Manager(args);

        manager.execute();

        workingTime = System.currentTimeMillis() - workingTime;

        System.out.println("Time spend to work: " + workingTime/1000 + "seconds");

        //todo calculate bytes
        System.out.println("Bytes dowloaded: " + downloadedBytes );

        LOG.log(Level.INFO, "program end");


    }


}

