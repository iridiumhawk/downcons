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

import java.util.logging.*;

public class Main {

    final static Logger LOG = ProjectLogger.initFileLogging(Main.class.getSimpleName());//Logger.getLogger();

    private static Long workingTime = 0L;

    public Main() {
        workingTime = 0L;

        LOG.log(Level.INFO, "Main init");
    }

    public static void main(String[] args) {

        //todo change to default behavior without parameters
/*        if (args.length == 0) {
            LOG.log(Level.WARNING, "input parameters absent");
            System.exit(-1);
        }*/

        LOG.log(Level.INFO, "========================================================");
        LOG.log(Level.INFO, "Program started");

        setWorkingTime();

        Manager manager = new Manager(args);

        manager.execute();

        //todo calculate bytes
        Long downloadedBytes = 0L;

        setWorkingTime();

        System.out.println("Time spent for task: " + workingTime / 1000 + " seconds");

//        System.out.println("Total threads used: ");

        System.out.println("Bytes downloaded: " + downloadedBytes);

        LOG.log(Level.INFO, "Program ended");

    }

    //count time of program working time
    private static void setWorkingTime() {
        workingTime = System.currentTimeMillis() - workingTime;
    }


}

