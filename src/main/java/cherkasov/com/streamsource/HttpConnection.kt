package cherkasov.com.streamsource

import cherkasov.com.ProjectLogger.LOG
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.logging.Level

/**
 * Connection to the given URL on HTTP server
 */
class HttpConnection(private val url: String?) : Connection {
    private var httpURLConnection: HttpURLConnection? = null
    override var isConnected: Boolean = false
        get() {
        return field
    }
        private set

    override fun connect(): Boolean {
        if (url == null || url == "") {
            return false
        }
        try {
            val link = URL(url)
            httpURLConnection = link.openConnection() as HttpURLConnection
            val responseCode = httpURLConnection!!.responseCode
            if (responseCode != HttpURLConnection.HTTP_OK) {
                LOG.log(Level.WARNING, "Response Code, $responseCode")
                return false
            }
        } catch (e: IOException) {
            LOG.log(Level.WARNING, "URLException Exception, " + e.message)
            return false
        }
        return true.also { isConnected = it }
    }

    override val contentLength: Long
        get() = httpURLConnection!!.contentLengthLong

    @get:Throws(IOException::class)
    override val inputStream: InputStream
        get() = httpURLConnection!!.inputStream

    override fun disconnect() {
        httpURLConnection!!.disconnect()
    }

}