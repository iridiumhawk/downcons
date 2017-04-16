package cherkasov.com.streamsource;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;

import static cherkasov.com.ProjectLogger.LOG;

/**
 * Created by cherkasov on 09.03.17.
 */
public class HttpConnection implements Connection {
    private HttpURLConnection httpURLConnection;
    private String url;
    private boolean connected = false;

    public HttpConnection(String url) {
        this.url = url;
    }

    @Override
    public boolean connect() {

        if (url == null) {
            return false;
        }

        try {
            URL link = new URL(url);
            httpURLConnection = (HttpURLConnection) link.openConnection();
            int responseCode = httpURLConnection.getResponseCode();

            if (responseCode != HttpURLConnection.HTTP_OK) {
                LOG.log(Level.WARNING, "Response Code, " + responseCode);
                return false;
            }

        } catch (IOException e) {
            LOG.log(Level.WARNING, "URLException Exception, " + e.getMessage());
            return false;
        }

       return connected = true;
    }

    @Override
    public boolean isConnected() {
        return connected;
    }


    @Override
    public long getContentLength() {
        return httpURLConnection.getContentLengthLong();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return httpURLConnection.getInputStream();
    }

    @Override
    public void disconnect() {
        httpURLConnection.disconnect();
    }
}
