package cherkasov.com.streamsource

import java.io.ByteArrayInputStream
import java.util.*

/**
 * Emulates the HTTP server and returns the data stream from memory
 */
class FakeHttpServer(private val buffer: Int) : ByteArrayInputStream(ByteArray(buffer)) {
    /**
     * Fill buffer with static data (byte 255)
     * @return the full byte array
     */
    private fun getBuffer(): ByteArray {
        val result = ByteArray(buffer)
        Arrays.fill(result, 255.toByte())
        return result
    }

    /**
     * Reads up to `len` bytes of data into an array of bytes
     * from this input stream.
     */
    @Synchronized
    override fun read(b: ByteArray, off: Int, len: Int): Int {
        return super.read(b, off, len)
    }

    /**
     * Create new buffer `buf` in memory
     * Set size of buffer
     * @param bufferSize for set size of new buffer
     */
    init {
        buf = getBuffer()
    }
}