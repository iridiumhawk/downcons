package cherkasov.com;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Created by cherkasov on 09.03.17.
 */
// return requested buffer with random latency
public class FakeHttpServer {
    private final int buffer;
    private final int latency;
    private final Random random = new Random();

    public FakeHttpServer(int buffer, int latency, Random random) {
        this.buffer = buffer;
        this.latency = latency;
    }


    public byte[] getBuffer(){
       byte[] result = new byte[buffer];

        random.nextBytes(result);

        try {
            TimeUnit.MILLISECONDS.sleep(random.nextInt(latency));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }
}
