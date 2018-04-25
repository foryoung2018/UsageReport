
package com.htc.lib1.cs.httpclient;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import android.os.Handler;

import com.htc.lib1.cs.logging.CommLoggerFactory;
import com.htc.lib1.cs.logging.HtcLogger;

/**
 * Represents the result of an asynchronous {@link HttpConnection} call.
 * 
 * @author samael_wang@htc.com
 * @param <T>
 */
public class HttpConnectionFuture<T> implements Runnable {

    /** Default timeout in seconds. */
    public static final long DEFAULT_TIMEOUT_SECONDS = 30;
    private HtcLogger mLogger = new CommLoggerFactory(this).create();
    private InternalFutureTask mInternalFuture;
    private HttpConnectionCallback<T> mCallback;
    private Handler mHandler;

    /**
     * Construct an instance.
     * 
     * @param connection Connection to operate on. Must not be {@code null}.
     */
    public HttpConnectionFuture(HttpConnection<T> connection) {
        this(connection, null, null);
    }

    /**
     * Construct an instance.
     * 
     * @param connection Connection to operate on. Must not be {@code null}.
     * @param callback Optional callback to invoke when the task finishes.
     * @param handler Handler to run the callback on.
     */
    public HttpConnectionFuture(HttpConnection<T> connection, HttpConnectionCallback<T> callback,
            Handler handler) {
        if (connection == null)
            throw new IllegalArgumentException("'connection' is null.");
        if (callback != null && handler == null)
            throw new IllegalArgumentException("'handler' is null while 'callback' is given.");

        mInternalFuture = new InternalFutureTask(connection);
        mCallback = callback;
        mHandler = handler;
    }

    /**
     * Returns true if this task completed. Completion may be due to normal
     * termination, an exception, or cancellation.
     * 
     * @return true if this task completed
     */
    public boolean isDone() {
        return mInternalFuture.isDone();
    }

    /**
     * Returns true if this task was cancelled before it completed normally.
     * 
     * @return true if this task was cancelled before it completed
     */
    public boolean isCanceled() {
        return mInternalFuture.isCancelled();
    }

    /**
     * Get the result of the {@link HttpConnection} call with default timeout,
     * which is {@link #DEFAULT_TIMEOUT_SECONDS}.
     * 
     * @return Response as an object.
     * @throws InterruptedException If the task has been interrupted before
     *             complete.
     * @throws ConnectivityException If the network is not available.
     * @throws ConnectionException If an error occurs when trying to send the
     *             request.
     * @throws IOException If an I/O error occurs when trying to write data to
     *             the remote server or parse the response.
     * @throws HttpException If remote server returns a HTTP error.
     */
    public T getResult() throws InterruptedException, ConnectivityException,
            ConnectionException, IOException, HttpException {
        return internalGetResult(null, null);
    }

    /**
     * Get the result of the {@link HttpConnection} call.
     * 
     * @param timeout The maximum time to wait.
     * @param unit The time unit of the timeout argument. This must not be
     *            {@code null}.
     * @return Response as an object.
     * @throws InterruptedException If the task has been interrupted before
     *             complete.
     * @throws ConnectivityException If the network is not available.
     * @throws ConnectionException If an error occurs when trying to send the
     *             request.
     * @throws IOException If an I/O error occurs when trying to write data to
     *             the remote server or parse the response.
     * @throws HttpException If remote server returns a HTTP error.
     */
    public T getResult(long timeout, TimeUnit unit) throws InterruptedException,
            ConnectivityException, ConnectionException, IOException, HttpException {
        return internalGetResult(timeout, unit);
    }

    private T internalGetResult(Long timeout, TimeUnit unit) throws InterruptedException,
            ConnectivityException, ConnectionException, IOException, HttpException {
        try {
            if (timeout == null) {
                return mInternalFuture.get();
            } else {
                return mInternalFuture.get(timeout, unit);
            }
        } catch (TimeoutException e) {
            throw new ConnectionException(e.getMessage(), e);
        } catch (ExecutionException e) {
            /*
             * Extract the exception thrown by HttpConnection.call().
             */
            final Throwable cause = e.getCause();

            if (cause instanceof ConnectivityException) {
                throw (ConnectivityException) cause;
            } else if (cause instanceof ConnectionException) {
                throw (ConnectionException) cause;
            } else if (cause instanceof IOException) {
                throw (IOException) cause;
            } else if (cause instanceof HttpException) {
                throw (HttpException) cause;
            } else {
                throw new IllegalStateException(cause);
            }
        } finally {
            mInternalFuture.cancel(true /* interrupt if running */);
        }
    }

    @Override
    public void run() {
        mLogger.verbose();
        mInternalFuture.run();
    }

    /**
     * Internal wrapper of a future task for {@link HttpConnectionFuture}.
     * 
     * @author samael_wang@htc.com
     */
    private class InternalFutureTask extends FutureTask<T> {

        public InternalFutureTask(Callable<T> callable) {
            super(callable);
        }

        protected void done() {
            if (mCallback != null) {
                postToHandler(mHandler, mCallback, HttpConnectionFuture.this);
            }
        }

        /**
         * Post the result to the callback on given handler.
         * 
         * @param handler The handler for the callback to run.
         * @param callback Callback to execute.
         * @param future Future returned from a specific http client call.
         */
        private void postToHandler(Handler handler, final HttpConnectionCallback<T> callback,
                final HttpConnectionFuture<T> future) {
            handler.post(new Runnable() {
                public void run() {
                    callback.run(future);
                }
            });
        }
    }
}
