package cherkasov.com.streamsource;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by cherkasov on 09.03.17.
 */
public interface Connection {
    /**
     *
     * @return length of content from input source
     */
    long getContentLength();

    /**
     *
     * @return input stream of established connection
     * @throws IOException if input stream is not available
     */
    InputStream getInputStream() throws IOException;

    /**
     * close current connection
     */
    void disconnect();

    /**
     *
     * @return status of connection
     */
    boolean isConnected();

    /**
     * Connect to given source
     * @return true if connection established
     */
    boolean connect();
}
