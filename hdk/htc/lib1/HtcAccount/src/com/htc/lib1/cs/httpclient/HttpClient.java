
package com.htc.lib1.cs.httpclient;

import java.net.URL;
import java.util.Map;
import java.util.TreeMap;

import android.content.Context;
import android.os.Handler;

import com.htc.lib1.cs.httpclient.HttpConnection.HttpErrorStreamReader;
import com.htc.lib1.cs.httpclient.HttpConnection.HttpInputStreamReader;
import com.htc.lib1.cs.httpclient.HttpConnection.HttpOutputStreamWriter;
import com.htc.lib1.cs.workflow.AsyncWorkflowTask;

/**
 * Simple HTTP client built on top of {@link HttpConnection}. The futures are
 * always execute on {@link AsyncWorkflowTask#THREAD_POOL_EXECUTOR}.
 * 
 * @author samael_wang@htc.com
 */
public class HttpClient {

    /**
     * The POJO class representing a request.
     * 
     * @author samael_wang
     * @param <T>
     */
    public static class HttpRequest<T> {
        /**
         * URL to connect to.
         */
        public URL url;

        /**
         * Header fields.
         */
        public Map<String, String> requestProperties;

        /**
         * Data output writer to use in POST / PUT methods.
         */
        public HttpOutputStreamWriter dataWriter;

        /**
         * Response input reader to use.
         */
        public HttpInputStreamReader<T> responseReader;

        /**
         * Error reader to use.
         */
        public HttpErrorStreamReader errorReader;
    }

    /**
     * Builder to build the request.
     * 
     * @author samael_wang
     * @param <T>
     */
    public class HttpRequestBuilder<T> {
        private HttpRequest<T> mmRequest;

        public HttpRequestBuilder(URL url, HttpInputStreamReader<T> reader) {
            mmRequest = new HttpRequest<T>();
            mmRequest.url = url;
            mmRequest.requestProperties = mDefaultRequestProperties != null ?
                    mDefaultRequestProperties : new TreeMap<String, String>();
            mmRequest.responseReader = reader;
            mmRequest.errorReader = mDefaultErrorReader;
        }

        /**
         * Add a request property.
         * 
         * @param key Key of the property.
         * @param value Value of the property.
         * @return {@link HttpRequestBuilder}
         */
        public HttpRequestBuilder<T> addRequestProperty(String key, String value) {
            mmRequest.requestProperties.put(key, value);
            return this;
        }

        /**
         * Add request properties.
         * 
         * @param requestProperties Request properties to add.
         * @return {@link HttpRequestBuilder}
         */
        public HttpRequestBuilder<T> addRequestProperties(Map<String, String> requestProperties) {
            mmRequest.requestProperties.putAll(requestProperties);
            return this;
        }

        /**
         * Set data output writer used in POST / PUT methods.
         * 
         * @param writer Writer to use.
         * @return {@link HttpRequestBuilder}
         */
        public HttpRequestBuilder<T> setDataWriter(HttpOutputStreamWriter writer) {
            mmRequest.dataWriter = writer;
            return this;
        }

        /**
         * Set error reader.
         * 
         * @param reader Reader to use.
         * @return {@link HttpRequestBuilder}
         */
        public HttpRequestBuilder<T> setErrorReader(HttpErrorStreamReader reader) {
            mmRequest.errorReader = reader;
            return this;
        }

        /**
         * Build the request.
         * 
         * @return {@link HttpRequest}
         */
        public HttpRequest<T> build() {
            return mmRequest;
        }
    }

    private Context mContext;
    private HttpErrorStreamReader mDefaultErrorReader;
    private Map<String, String> mDefaultRequestProperties;
    private Handler mMainHandler;

    /**
     * Create an instance.
     * 
     * @param context Context to operate on.
     * @param defaultErrorReader Default error reader to use. Must not be
     *            {@code null}.
     * @param defaultRequestProperties Default request properties.
     */
    public HttpClient(Context context, HttpErrorStreamReader defaultErrorReader,
            Map<String, String> defaultRequestProperties) {
        if (context == null)
            throw new IllegalArgumentException("'context' is null.");
        if (defaultErrorReader == null)
            throw new IllegalArgumentException("'defaultErrorReader' is null.");

        mContext = context;
        mDefaultErrorReader = defaultErrorReader;
        mDefaultRequestProperties = defaultRequestProperties;
        mMainHandler = new Handler(context.getMainLooper());
    }

    /**
     * Create an instance with no default request properties.
     * 
     * @param context Context to operate on.
     * @param defaultErrorReader Default error Reader to use. Must not be
     *            {@code null}.
     */
    public HttpClient(Context context, HttpErrorStreamReader defaultErrorReader) {
        this(context, defaultErrorReader, null);
    }

    /**
     * Create an instance using {@link SimpleErrorStreamReader}.
     * 
     * @param context Context to operate on.
     * @param defaultRequestProperties Default request properties.
     */
    public HttpClient(Context context, Map<String, String> defaultRequestProperties) {
        this(context, new SimpleErrorStreamReader(), defaultRequestProperties);
    }

    /**
     * Create an instance using {@link SimpleErrorStreamReader}.
     * 
     * @param context Context to operate on.
     */
    public HttpClient(Context context) {
        this(context, new SimpleErrorStreamReader(), null);
    }

    /**
     * Get a {@link HttpRequestBuilder} instance based on the default values
     * used by the {@link HttpClient} instance.
     * 
     * @param url URL to connect to.
     * @param responseReader Reader to parse the response. It's necessary to
     *            determine return type.
     * @return {@link HttpRequestBuilder}
     */
    public <T> HttpRequestBuilder<T> getRequestBuilder(URL url,
            HttpInputStreamReader<T> responseReader) {
        return new HttpRequestBuilder<T>(url, responseReader);
    }

    /**
     * Make a non-blocking HTTP GET request.
     * 
     * @param request The request to perform HTTP GET.
     * @param callback Callback to invoke when the task completes.
     * @param handler Handler to run the callback on. If not given, the callback
     *            will be run in main thread.
     * @return {@link HttpConnectionFuture}
     */
    public <T> HttpConnectionFuture<T> get(HttpRequest<T> request,
            HttpConnectionCallback<T> callback, Handler handler) {
        HttpConnection<T> call = new HttpConnection<T>(mContext, request.url, HttpMethod.GET,
                request.responseReader, request.errorReader, request.requestProperties, null);

        HttpConnectionFuture<T> task = new HttpConnectionFuture<T>(call, callback,
                handler == null ? mMainHandler : handler);
        AsyncWorkflowTask.THREAD_POOL_EXECUTOR.execute(task);
        return task;
    }

    /**
     * Make a non-blocking HTTP GET request with default request properties
     * builder and error Reader. Return the response body without conversion.
     * 
     * @param url URL to make the request to.
     * @param callback Callback to invoke when the task completes.
     * @param handler Handler to run the callback on. If not given, the callback
     *            will be run in main thread.
     * @return {@link HttpConnectionFuture}
     */
    public HttpConnectionFuture<String> get(URL url, HttpConnectionCallback<String> callback,
            Handler handler) {
        return get(new HttpRequestBuilder<String>(url, new StringInputStreamReader()).build(),
                callback, handler);
    }

    /**
     * Make a non-blocking HTTP PUT request.
     * 
     * @param request Request to perform HTTP PUT.
     * @param callback Callback to invoke when the task completes.
     * @param handler Handler to run the callback on. If not given, the callback
     *            will be run in main thread.
     * @return {@link HttpConnectionFuture}
     */
    public <T> HttpConnectionFuture<T> put(HttpRequest<T> request,
            HttpConnectionCallback<T> callback,
            Handler handler) {
        HttpConnection<T> call = new HttpConnection<T>(mContext, request.url, HttpMethod.PUT,
                request.responseReader, request.errorReader, request.requestProperties,
                request.dataWriter);

        HttpConnectionFuture<T> task = new HttpConnectionFuture<T>(call, callback,
                handler == null ? mMainHandler : handler);
        AsyncWorkflowTask.THREAD_POOL_EXECUTOR.execute(task);
        return task;
    }

    /**
     * Make a non-blocking HTTP PUT request with default request properties
     * builder and error Reader. Write the data as a string and return the
     * response body without conversion.
     * 
     * @param url URL to make the request to.
     * @param data Data to send to the remote server.
     * @param callback Callback to invoke when the task completes.
     * @param handler Handler to run the callback on. If not given, the callback
     *            will be run in main thread.
     * @return {@link HttpConnectionFuture}
     */
    public HttpConnectionFuture<String> put(URL url, String data,
            HttpConnectionCallback<String> callback, Handler handler) {
        return put(new HttpRequestBuilder<String>(url, new StringInputStreamReader())
                .setDataWriter(new StringOutputStreamWriter(data)).build(), callback, handler);
    }

    /**
     * Make a non-blocking HTTP POST request.
     * 
     * @param request Request to perform HTTP POST.
     * @param callback Callback to invoke when the task completes.
     * @param handler Handler to run the callback on. If not given, the callback
     *            will be run in main thread.
     * @return {@link HttpConnectionFuture}
     */
    public <T> HttpConnectionFuture<T> post(HttpRequest<T> request,
            HttpConnectionCallback<T> callback, Handler handler) {
        HttpConnection<T> call = new HttpConnection<T>(mContext, request.url, HttpMethod.POST,
                request.responseReader, request.errorReader, request.requestProperties,
                request.dataWriter);

        HttpConnectionFuture<T> task = new HttpConnectionFuture<T>(call, callback,
                handler == null ? mMainHandler : handler);
        AsyncWorkflowTask.THREAD_POOL_EXECUTOR.execute(task);
        return task;
    }

    /**
     * Make a non-blocking HTTP POST request with default request properties
     * builder and error Reader. Write the data as a string and return the
     * response body without conversion.
     * 
     * @param url URL to make the request to.
     * @param data Data to send to the remote server.
     * @param callback Callback to invoke when the task completes.
     * @param handler Handler to run the callback on. If not given, the callback
     *            will be run in main thread.
     * @return {@link HttpConnectionFuture}
     */
    public HttpConnectionFuture<String> post(URL url, String data,
            HttpConnectionCallback<String> callback, Handler handler) {
        return post(new HttpRequestBuilder<String>(url, new StringInputStreamReader())
                .setDataWriter(new StringOutputStreamWriter(data)).build(), callback, handler);
    }

    /**
     * Make a non-blocking HTTP DELETE request.
     * 
     * @param request Request to perform HTTP DELETE.
     * @param callback Callback to invoke when the task completes.
     * @param handler Handler to run the callback on. If not given, the callback
     *            will be run in main thread.
     * @return {@link HttpConnectionFuture}
     */
    public <T> HttpConnectionFuture<T> delete(HttpRequest<T> request,
            HttpConnectionCallback<T> callback, Handler handler) {
        HttpConnection<T> call = new HttpConnection<T>(mContext, request.url,
                HttpMethod.DELETE, request.responseReader, request.errorReader,
                request.requestProperties, null);

        HttpConnectionFuture<T> task = new HttpConnectionFuture<T>(call, callback,
                handler == null ? mMainHandler : handler);
        AsyncWorkflowTask.THREAD_POOL_EXECUTOR.execute(task);
        return task;
    }

    /**
     * Make a non-blocking HTTP DELETE request with default request properties
     * builder and error Reader. Return the response body without conversion.
     * 
     * @param url URL to make the request to.
     * @param callback Callback to invoke when the task completes.
     * @param handler Handler to run the callback on. If not given, the callback
     *            will be run in main thread.
     * @return {@link HttpConnectionFuture}
     */
    public HttpConnectionFuture<String> delete(URL url, HttpConnectionCallback<String> callback,
            Handler handler) {
        return delete(new HttpRequestBuilder<String>(url, new StringInputStreamReader())
                .build(), callback, handler);
    }
}
