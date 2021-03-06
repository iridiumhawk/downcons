package cherkasov.com.streamsource;

import java.io.IOException;
import java.io.InputStream;

/**
 * Dummy
 * For test purposes only
 * All methods return false or null
 */

public class DummyConnection implements Connection {
    @Override
    public long getContentLength() {
        return 0;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return null;
    }

    @Override
    public void disconnect() {
    }

    @Override
    public boolean isConnected() {
        return false;
    }

    @Override
    public boolean connect() {
        return false;
    }
}
