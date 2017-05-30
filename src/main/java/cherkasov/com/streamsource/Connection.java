package cherkasov.com.streamsource;

import java.io.IOException;
import java.io.InputStream;

/**
 * API for connection to input source
 */
public interface Connection {
    /**
     * Get content length
     * @return length of content from input source
     */
    long getContentLength();

    /**
     * Get input stream
     * @return input stream for established connection
     * @throws IOException if input stream is not available
     */
    InputStream getInputStream() throws IOException;

    /**
     * Closes current connection
     */
    void disconnect();

    /**
     * Get status of connection
     * @return status of connection
     */
    boolean isConnected();

    /**
     * Connect to given source
     * @return <code>true</code> if connection established
     */
    boolean connect();
}
