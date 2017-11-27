package cherkasov.com.streamsource;

import java.io.IOException;
import java.io.InputStream;

/**
 * Connection for input stream from memory buffer
 */
public class FakeConnection implements Connection {
    private InputStream inputStream;
    private boolean connected;

    public FakeConnection(InputStream inputStream) {
        this.inputStream = inputStream;
        this.connected = true;
    }

    @Override
    public boolean connect() {
        return true;
    }

    @Override
    public boolean isConnected() {
        return connected;
    }


    @Override
    public long getContentLength() {
        try {
            return inputStream.available();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public InputStream getInputStream() {
        return inputStream;
    }

    @Override
    public void disconnect() {
        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
