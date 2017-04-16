package cherkasov.com.streamsource;

import java.io.ByteArrayInputStream;
import java.util.Arrays;

/**
 * Created by cherkasov on 09.03.17.
 */
// return requested buffer with random latency from memory
public class FakeHttpServer extends ByteArrayInputStream {
    private final int buffer;
    private final int latency;


    public FakeHttpServer(int bufferSize, int latencyResponse) {
        super(new byte[bufferSize]);
        this.buffer = bufferSize;
        this.latency = latencyResponse;
        this.buf = getBuffer();
    }


    private byte[] getBuffer() {
        byte[] result = new byte[buffer];
        Arrays.fill(result, (byte) 255);
        return result;
    }

    @Override
    public synchronized int read(byte[] b, int off, int len) {

        return super.read(b, off, len);
    }

}
