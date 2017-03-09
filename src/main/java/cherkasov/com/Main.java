package cherkasov.com;

// using
// java -jar utility.jar -n 5 -l 2000k -o output_folder -f links.txt
/*
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
*/

import java.text.MessageFormat;
import java.util.logging.*;

public class Main {

    public static final Logger LOG = ProjectLogger.initFileLogging(Main.class.getSimpleName());

    private static long workingTime = 0L;

    public static void main(String[] args) {
        LOG.setLevel(Level.INFO);

        LOG.log(Level.INFO, "Program started");

        workingTime = System.currentTimeMillis();

        Manager manager = new Manager(args);

        long downloadedBytes = manager.execute();

        workingTime = (System.currentTimeMillis() - workingTime) / 1000; // in seconds

        System.out.println(MessageFormat.format(
                "Time spent for all tasks: {0} seconds",
                workingTime));

        System.out.println(MessageFormat.format(
                "Total downloaded: {0} byte ({1} MegaByte), average speed: {2} byte/sec",
                downloadedBytes,
                downloadedBytes / 1024 / 1024,
                downloadedBytes / (workingTime)));

        LOG.log(Level.INFO, "Program ended");
    }
}

