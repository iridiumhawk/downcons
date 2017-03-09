package cherkasov.com.source;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by cherkasov on 09.03.17.
 */
public class FakeConnection implements Connection {
    private InputStream inputStream;
    private boolean connected;

    public FakeConnection(InputStream inputStream) {
        this.inputStream = inputStream;
        this.connected = true;
    }

    @Override
    public boolean connect() { return true;}

    @Override
    public boolean isConnected() {
        return connected;
    }


    @Override
    public long getContentLength() {
        return 10000000;
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