package cherkasov.com.streamsource

import java.io.IOException
import java.io.InputStream

/**
 * Connection for input stream from memory buffer
 */
class FakeConnection(override val inputStream: InputStream) : Connection {
    override val isConnected = true
    override fun connect(): Boolean {
        return true
    }

    @get:Throws(IOException::class)
    override val contentLength: Long
        get() = inputStream.available().toLong()

    @Throws(IOException::class)
    override fun disconnect() {
        inputStream.close()
    }

}