package cherkasov.com.streamsource;

import java.io.ByteArrayInputStream;
import java.util.Arrays;

/**
 * Created by cherkasov on 09.03.17.
 */
// return requested buffer from memory
public class FakeHttpServer extends ByteArrayInputStream {
    private final int buffer;


    public FakeHttpServer(int bufferSize) {
        super(new byte[bufferSize]);
        this.buffer = bufferSize;
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
