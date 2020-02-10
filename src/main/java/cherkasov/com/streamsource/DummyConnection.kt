package cherkasov.com.streamsource

import java.io.IOException
import java.io.InputStream

/**
 * Dummy
 * For test purposes only
 * All methods return false or null
 */
class DummyConnection : Connection {
    override val contentLength: Long
        get() = 0

    @get:Throws(IOException::class)
    override val inputStream: InputStream?
        get() = null

    override fun disconnect() {}
    override val isConnected: Boolean
        get() = false

    override fun connect(): Boolean {
        return false
    }
}