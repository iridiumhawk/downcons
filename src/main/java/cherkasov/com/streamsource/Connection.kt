package cherkasov.com.streamsource

import java.io.IOException
import java.io.InputStream

/**
 * API for connection to input source
 */
interface Connection {
    /**
     * Gets content length
     * @return length of content from input source
     */
    @get:Throws(IOException::class)
    val contentLength: Long

    /**
     * Gets input stream
     * @return input stream for established connection
     * @throws IOException if input stream is not available
     */
    @get:Throws(IOException::class)
    val inputStream: InputStream?

    /**
     * Closes current connection
     */
    @Throws(IOException::class)
    fun disconnect()

    /**
     * Gets status of connection
     * @return status of connection
     */
    val isConnected: Boolean

    /**
     * Connects to given source
     * @return `true` if the connection established
     */
    fun connect(): Boolean
}