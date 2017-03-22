package cherkasov.com.source;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Created by cherkasov on 09.03.17.
 */
// return requested buffer with random latency from memory
public class FakeHttpServer extends ByteArrayInputStream {
    private final int buffer;
    private final int latency;
//    private final Random random = new Random();


    public FakeHttpServer(int bufferSize, int latencyResponse) {
        super(new byte[bufferSize]);
        this.buffer = bufferSize;
        this.latency = latencyResponse;
        this.buf = getBuffer();
    }


    private byte[] getBuffer() {
        byte[] result = new byte[buffer];

//        long timer = System.nanoTime();
        Arrays.fill(result, (byte) 255);
//        random.nextBytes(result);
//        System.out.println((System.nanoTime()-timer) / 1000000);

        return result;
    }

    @Override
    public synchronized int read(byte[] b, int off, int len) {
        if (latency  <= 0) {
            return super.read(b, off, len);
        }



//            int middleLatency = random.nextInt(latency);// + random.nextInt(latency)) / 2;

//            Thread.sleep(middleLatency);
/*        try {
            TimeUnit.NANOSECONDS.sleep(latency*1000000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

/*long endTime = System.nanoTime()+latency*140000;

while (true){

    if (System.nanoTime() > endTime){
        break;
    }

}*/


//        return 1000;
        return super.read(b, off, len);
    }

}
