
package com.htc.lib1.cs.httpclient;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import android.content.Context;

import com.htc.lib1.cs.ConnectivityHelper;
import com.htc.lib1.cs.logging.CommLoggerFactory;
import com.htc.lib1.cs.logging.HtcLogger;

/**
 * A wrapper of {@link HttpURLConnection} which uses {@code OkHttpClient} to
 * make a http/https request. To make a non-blocking call, simply use a
 * {@link HttpConnectionFuture} to execute the {@link HttpConnection}.
 * <p>
 * Callers could also use the same way to set execution timeout in a blocking
 * call. We don't setup connection / read timeout as it can't decide the whole
 * execution timeout since DNS lookup timeout in unknown and unchangeable in
 * {@link HttpURLConnection}.
 * </p>
 */
public class HttpConnection<T> implements Callable<T> {

    /**
     * {@link HttpOutputStreamWriter} writes caller-specific data to the output
     * stream which will be sent to the remote HTTP server if using PUT or POST
     * method.
     * 
     * @author samael_wang@htc.com
     */
    public interface HttpOutputStreamWriter {
        /**
         * Invoked when the connection has been made and the client is ready to
         * write data to the server. Subclasses should decide what to write.
         * Don't close the {@code ostream} in your implementation,
         * {@link HttpConnection} closes it automatically.
         * 
         * @param ostream Output stream to write to. The method won't be invoked
         *            if {@link HttpConnection} fails to open the output stream,
         *            hence {@code ostream} will never be {@code null}. Note
         *            that {@code ostream} is buffered so it's suggested not to
         *            use a redundant {@link BufferedWriter} on it.
         * @throws IOException If an error occurs when trying to write the data
         *             to the output stream.
         */
        public void writeTo(BufferedOutputStream ostream) throws IOException;
    }

    /**
     * {@lnk HttpInputStreamReader} reads the response body return from the
     * remote HTTP server, and optionally convert it to an object. For example,
     * one could implement json parsing logic inside, and convert the response
     * body to a concrete type of its model.
     * 
     * @author samael_wang@htc.com
     * @param <T> Returning object type.
     */
    public interface HttpInputStreamReader<T> {
        /**
         * Invoked when the remote HTTP server returns a response with a success
         * status code (2xx). The returning object will be returned to the
         * caller of {@link HttpConnection}. Subclass can process the response
         * directly inside this method and just return {@code null} or convert
         * it to a proper object to process later. Don't close the
         * {@code istream} in your implementation, {@link HttpConnection} closes
         * it automatically.
         * 
         * @param statusCode HTTP status code.
         * @param responseHeader HTTP header fields.
         * @param istream Input stream to read from. The method won't be invoked
         *            if {@link HttpConnection} fails to open the input stream,
         *            hence {@code istream} will never be {@code null}. Note
         *            that {@code istream} is buffered so it's suggested not to
         *            use a redundant {@link BufferedReader} on it.
         * @return Response object which will be returned to the caller of
         *         {@link HttpConnection}.
         * @throws IOException If an error occurs when trying to create a
         *             response object from the given response body.
         */
        public T readFrom(int statusCode, Map<String, List<String>> responseHeader,
                BufferedInputStream istream) throws IOException;
    }

    /**
     * When the remote HTTP server returns a status code indicating an error,
     * {@link HttpErrorStreamReader} reads the response body and convert it to a
     * {@link HttpException}.
     * 
     * @author samael_wang@htc.com
     */
    public interface HttpErrorStreamReader {
        /**
         * Invoked when the remote HTTP server returns a HTTP error. Subclasses
         * should convert the response to a proper {@link HttpException}
         * instance to be thrown. Don't close the {@code estream} in your
         * implementation, {@link HttpConnection} closes it automatically.
         * 
         * @param statusCode HTTP status code.
         * @param responseHeader HTTP header fields.
         * @param estream Error stream to read from. The method won't be invoked
         *            if {@link HttpConnection} fails to open the input stream,
         *            hence {@code estream} will never be {@code null}. Note
         *            that {@code estream} is buffered so it's suggested not to
         *            use a redundant {@link BufferedReader} on it.
         * @return {@link HttpException}. It must not be {@code null}.
         */
        public HttpException readFrom(int statusCode, Map<String, List<String>> responseHeader,
                BufferedInputStream estream);
    }

    private HtcLogger mLogger = new CommLoggerFactory(this).create();
    private Context mContext;
    private URL mUrl;
    private HttpMethod mMethod;
    private HttpInputStreamReader<T> mInputReader;
    private HttpErrorStreamReader mErrorReader;
    private Map<String, String> mRequestProperties;
    private HttpOutputStreamWriter mDataWriter;

    /**
     * @param context Context to operate on.
     * @param url The full path of a HTTP/HTTPS URL (including query string, if
     *            any) to send the request to.
     * @param method HTTP method to use.
     * @param inputReader Reader to read the response and convert it to a
     *            concrete response object depends on what the caller wants.
     * @param errorReader Reader to read the remote HTTP error response and
     *            convert it to a specific {@link HttpException} instance.
     * @param requestProperties Optional properties to be included in the
     *            request header.
     * @param dataWriter Optional writer to write data to send to the remote
     *            server if the method is {@link HttpMethod#PUT} or
     *            {@link HttpMethod#POST}.
     * @return {@link HttpConnection}
     */
    public HttpConnection(Context context, URL url, HttpMethod method,
            HttpInputStreamReader<T> inputReader, HttpErrorStreamReader errorReader,
            Map<String, String> requestProperties, HttpOutputStreamWriter dataWriter) {
        if (context == null)
            throw new IllegalArgumentException("'context' is null.");
        if (url == null)
            throw new IllegalArgumentException("'url' is null.");
        if (method == null)
            throw new IllegalArgumentException("'method' is null.");
        if (inputReader == null)
            throw new IllegalArgumentException("'inputReader' is null.");
        if (errorReader == null)
            throw new IllegalArgumentException("'errorReader' is null.");

        mContext = context;
        mUrl = url;
        mMethod = method;
        mInputReader = inputReader;
        mErrorReader = errorReader;
        mRequestProperties = requestProperties;
        mDataWriter = dataWriter;
    }

    @Override
    public T call() throws ConnectivityException, ConnectionException, IOException,
            HttpException {

        // Check network before trying to connect.
        if (!ConnectivityHelper.get(mContext).isConnected())
            throw new ConnectivityException();

        /*
         * Sometimes the incorrect device time might cause SSL handshake fail.
         * Make the timestamp in log so we have more information if user reports
         * connection fail.
         */
        long connectionStartTime = System.currentTimeMillis();
        mLogger.verbose("HttpConnection performed at ", new Date(connectionStartTime));

        // Make the request.
        HttpURLConnection connection = null;
        try {
            // Open and setup connection.
            connection = openConnection();
            setupRequestMethod(connection);
            setupRequestHeader(connection);

            // Write output data (if any).
            writeData(connection);
        } catch (IOException e) {
            if (connection != null)
                connection.disconnect();

            // Convert to a more meaningful exception.
            throw new ConnectionException(e.getMessage(), e);
        }

        // Read response code and header.
        long responseStart = System.currentTimeMillis();
        int statusCode = connection.getResponseCode();
        Map<String, List<String>> responseHeader = connection.getHeaderFields();
        mLogger.verbose("Performance: Response header read in ",
                System.currentTimeMillis() - responseStart, " ms");

        // Get response body.
        BufferedInputStream bufEstream = null;
        try {
            return readResponse(connection, statusCode, responseHeader);
        } catch (IOException e) {
            /*
             * An IOException can be either caused by an actual HTTP error, or
             * the handler failed to read the response, or even the thread is
             * interrupted for some reasons. In the later cases,
             * getErrorStream() will return null.
             */
            InputStream estream = connection.getErrorStream();
            if (estream != null) {
                bufEstream = new BufferedInputStream(estream);
                throw mErrorReader.readFrom(statusCode, responseHeader, bufEstream);
            } else {
                // If it's not caused by HTTP errors, simply rethrow it.
                throw e;
            }
        } finally {
            // Close error stream, if any.
            if (bufEstream != null)
                bufEstream.close();
            /*
             * On android, this doesn't really disconnect the socket but returns
             * the socket to a pool of connected sockets unless manually call
             * setSystemProperty("http.keepAlive", false);
             */
            connection.disconnect();

            mLogger.verbose("Performance: Total round-trip made in ",
                    System.currentTimeMillis() - connectionStartTime, " ms");
        }
    }

    /**
     * Open a connection.
     * 
     * @return {@link HttpURLConnection}
     * @throws IOException If it fails to open the connection.
     */
    private HttpURLConnection openConnection() throws IOException {
        mLogger.verboseS(mUrl);
        return (HttpURLConnection) mUrl.openConnection();
    }

    /**
     * Setup request method.
     * 
     * @param connection Connection to setup.
     */
    private void setupRequestMethod(HttpURLConnection connection) {
        mLogger.verboseS(mMethod);

        try {
            switch (mMethod) {
                case GET:
                    connection.setDoOutput(false);
                    connection.setRequestMethod("GET");
                    break;
                case PUT:
                    connection.setDoOutput(true);
                    connection.setRequestMethod("PUT");
                    break;
                case POST:
                    connection.setDoOutput(true);
                    connection.setRequestMethod("POST");
                    break;
                case DELETE:
                    connection.setDoOutput(false);
                    connection.setRequestMethod("DELETE");
            }
        } catch (ProtocolException e) {
            throw new IllegalStateException(
                    "HttpURLConnection claims the protocol is not supported: " + mMethod);
        }
    }

    /**
     * Setup request header.
     * 
     * @param connection Connection to set up.
     */
    private void setupRequestHeader(HttpURLConnection connection) {
        if (mRequestProperties != null) {
            mLogger.verboseS(mRequestProperties);
            for (String field : mRequestProperties.keySet()) {
                String value = mRequestProperties.get(field);
                connection.addRequestProperty(field, value);
            }
        }
    }

    /**
     * Write data to the connection.
     * 
     * @param connection Connection to write data to.
     * @throws IOException If an error occurs when writing the data.
     * @return The {@link OutputStream} used to write data or {@code null} if
     *         the connection is not writable.
     */
    private void writeData(HttpURLConnection connection) throws IOException {
        if (mDataWriter != null && connection.getDoOutput()) {
            long time = System.currentTimeMillis();
            BufferedOutputStream ostream = new BufferedOutputStream(connection.getOutputStream());
            try {
                mDataWriter.writeTo(ostream);
                ostream.flush();
            } finally {
                ostream.close();
            }

            mLogger.verbose("Performance: Output data sent in ",
                    System.currentTimeMillis() - time, " ms");
        }
    }

    /**
     * Read the response.
     * 
     * @param connection Connection to read response from.
     * @param statusCode HTTP status code.
     * @param responseHeader HTTP header fields in the response.
     * @return Response object converted by the given
     *         {@link HttpInputStreamReader}.
     * @throws IOException If an error occurs when creating the input stream or
     *             reading the response body.
     */
    private T readResponse(HttpURLConnection connection, int statusCode,
            Map<String, List<String>> responseHeader) throws IOException {
        // Read response body.
        long time = System.currentTimeMillis();
        BufferedInputStream istream = new BufferedInputStream(connection.getInputStream());
        T responseObj;
        try {
            responseObj = mInputReader.readFrom(statusCode, responseHeader, istream);
        } finally {
            istream.close();
        }

        mLogger.verbose("Performance: Response body read in ",
                System.currentTimeMillis() - time, " ms");

        return responseObj;
    }
}
