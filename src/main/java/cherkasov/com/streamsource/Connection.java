package cherkasov.com.streamsource;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by cherkasov on 09.03.17.
 */
public interface Connection {
    long getContentLength();
    InputStream getInputStream() throws IOException;
    void disconnect();
    boolean isConnected();
    boolean connect();
}
