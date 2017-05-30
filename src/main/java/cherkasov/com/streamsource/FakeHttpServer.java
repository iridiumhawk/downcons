package cherkasov.com.streamsource;

import java.io.ByteArrayInputStream;
import java.util.Arrays;

/**
 * Created by cherkasov on 09.03.17.
 */
/**
 * Emulates the HTTP server and returns the data stream from memory
 */

public class FakeHttpServer extends ByteArrayInputStream {
    private final int buffer;

    /**
     * Create new buffer <code>buf</code> in memory
     * Set size of buffer
     * @param bufferSize for create new buffer
     */
    public FakeHttpServer(int bufferSize) {
        super(new byte[bufferSize]);
        this.buffer = bufferSize;
        this.buf = getBuffer();
    }

    /**
     * Fill buffer with static data (255)
     * @return the full byte array
     */
    private byte[] getBuffer() {
        byte[] result = new byte[buffer];
        Arrays.fill(result, (byte) 255);

        return result;
    }


    /**
     * Reads up to <code>len</code> bytes of data into an array of bytes
     * from this input stream.
     */
    @Override
    public synchronized int read(byte[] b, int off, int len) {

        return super.read(b, off, len);
    }

}
