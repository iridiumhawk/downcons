package cherkasov.com.source;

import cherkasov.com.TaskEntity;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;

import static cherkasov.com.Main.LOG;

/**
 * Created by cherkasov on 09.03.17.
 */
public class HttpConnection implements Connection {
    private HttpURLConnection httpURLConnection;

    private boolean connected;

    public HttpConnection(TaskEntity urlFile) {
        if (urlFile == null) {
            connected = false;
            return;
        }

        try {
            URL link = new URL(urlFile.getUrl());
            httpURLConnection = (HttpURLConnection) link.openConnection();
            int responseCode = httpURLConnection.getResponseCode();

            if (responseCode != HttpURLConnection.HTTP_OK) {
                LOG.log(Level.WARNING, "Response Code, " + responseCode);
                connected = false;
                return;
            }

        } catch (IOException e) {
            LOG.log(Level.WARNING, "URLException Exception, " + e.getMessage());
            connected = false;
            return;
        }

        connected = true;
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
