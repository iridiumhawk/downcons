package cherkasov.com.streamsource;

import java.io.IOException;
import java.io.InputStream;

/**
 * API for connection to input source
 */
public interface Connection {
    /**
     *
     * @return length of content from input source
     */
    long getContentLength();

    /**
     *
     * @return input stream for established connection
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
     * @return <code>true</code> if connection established
     */
    boolean connect();
}
