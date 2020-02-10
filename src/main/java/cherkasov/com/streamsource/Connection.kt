package cherkasov.com.streamsource;

import java.io.IOException;
import java.io.InputStream;

/**
 * API for connection to input source
 */
public interface Connection {
    /**
     * Gets content length
     * @return length of content from input source
     */
    long getContentLength() throws IOException;

    /**
     * Gets input stream
     * @return input stream for established connection
     * @throws IOException if input stream is not available
     */
    InputStream getInputStream() throws IOException;

    /**
     * Closes current connection
     */
    void disconnect() throws IOException;

    /**
     * Gets status of connection
     * @return status of connection
     */
    boolean isConnected();

    /**
     * Connects to given source
     * @return <code>true</code> if the connection established
     */
    boolean connect();
}
