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
    public long getContentLength() throws IOException {

        return inputStream.available();
    }

    @Override
    public InputStream getInputStream() {

        return inputStream;
    }

    @Override
    public void disconnect() throws IOException {

            inputStream.close();
    }
}
