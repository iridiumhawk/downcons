package cherkasov.com;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.Parameterized;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;
import java.util.stream.Collectors;

/**
 * Created by hawk on 08.02.2017.
 */
public class DownloaderTest {
    private ConcurrentLinkedQueue<TaskEntity> queueTasks;
    private Parameters parameters;

    @Before
    public void setUp() throws Exception {
        queueTasks = new ConcurrentLinkedQueue<>();

        queueTasks.add(new TaskEntity("http://1", "1.txt"));
        queueTasks.add(new TaskEntity("http://2", "2.txt"));
        queueTasks.add(new TaskEntity("http://3", "3.txt"));
        queueTasks.add(new TaskEntity("http://4", "4.txt"));
        queueTasks.add(new TaskEntity("http://5", "5.txt"));
        queueTasks.add(new TaskEntity("http://6", "6.txt"));
        queueTasks.add(new TaskEntity("http://7", "7.txt"));
        queueTasks.add(new TaskEntity("http://8", "8.txt"));
        queueTasks.add(new TaskEntity("http://9", "9.txt"));
        queueTasks.add(new TaskEntity("http://10", "10.txt"));

        parameters = new Parameters(5, 5000000, "", "output", true);
    }

    @Ignore
    @Test
    public void start() throws Exception {

        final Downloader downloader = new Downloader(queueTasks, parameters, Downloader.ConnectionType.FAKE);

        long timer = System.nanoTime();

        downloader.start();

        //return atomic long
        System.out.println(downloader.getDownloadedBytesSummary().get());

        System.out.println((System.nanoTime() - timer) / 1_000_000_000 + " sec");

        assertEquals(downloader.getDownloadedBytesSummary().get(), 100_000_000);

    }


    @Test
    @Ignore
    public void testRealTimeToSleep() throws Exception {
        List<Long> time = new ArrayList<>();

/*        for (int i = 0; i < 10000; i++) {
            TimeUnit.NANOSECONDS.sleep(200000);
        }*/

//todo add switch threads, 10
        for (int i = 0; i < 30; i++) {
            long timer = System.nanoTime();

//            TimeUnit.MILLISECONDS.sleep(3);
            TimeUnit.NANOSECONDS.sleep(500_000_000);
//            LockSupport.parkNanos(3000000);
            time.add(System.nanoTime() - timer);
//
//            System.out.println((System.nanoTime() - timer) );

        }
/*        long summ = 0;
        for (Long aLong : time) {
            summ += aLong;

        }*/

//        long summ = time.stream().mapToLong(l -> l).sum();

        System.out.println(time.stream().mapToLong(l -> l).average());
//        System.out.println(summ / time.size());
        System.out.println(time.stream().min((a, b) -> (int) (a - b)).toString());
        System.out.println(time.stream().max((a, b) -> (int) (a - b)).toString());
    }
}