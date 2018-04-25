
package com.htc.lib1.cs.account;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import android.accounts.OperationCanceledException;
import android.content.Context;
import android.os.Handler;

import com.htc.lib1.cs.app.ProcessUtils;

/**
 * A {@link FutureTask} implementation with callback support.
 * 
 * @author samael_wang@htc.com
 * @param <V>
 */
/* package */class DataServiceFutureImpl<V> extends FutureTask<V> implements DataServiceFuture<V> {

    private Context mContext;
    private Handler mHandler;
    private DataServiceFuture.DataServiceCallback<V> mCallback;
    private Handler mMainHandler;

    /**
     * @param context Context to operate on.
     * @param callable Callable to execute.
     * @param callback Optional callback to invoke when the result returns.
     * @param handler Optional handler for the callback, if any.
     */
    public DataServiceFutureImpl(Context context, DataServiceCallable<V> callable,
            DataServiceFuture.DataServiceCallback<V> callback, Handler handler) {
        super(callable);

        if (context == null)
            throw new IllegalArgumentException("'context' is null.");

        mContext = context;
        mCallback = callback;
        mHandler = handler;
        mMainHandler = new Handler(mContext.getMainLooper());
    }

    @Override
    public V getResult() throws OperationCanceledException, IOException {
        return internalGetResult(null, null);
    }

    @Override
    public V getResult(long timeout, TimeUnit unit) throws OperationCanceledException,
            IOException {
        return internalGetResult(timeout, unit);
    }

    @Override
    protected void done() {
        if (mCallback != null) {
            postToHandler(mHandler, mCallback, this);
        }
    }

    private V internalGetResult(Long timeout, TimeUnit unit) throws OperationCanceledException,
            IOException {
        if (!isDone()) {
            ProcessUtils.ensureNotOnMainThread();
        }

        try {
            if (timeout == null) {
                return get();
            } else {
                return get(timeout, unit);
            }
        } catch (InterruptedException e) {
            throw new OperationCanceledException();
        } catch (TimeoutException e) {
            throw new OperationCanceledException();
        } catch (ExecutionException e) {
            // Extract the exception thrown by the callable.
            final Throwable cause = e.getCause();
            if (cause instanceof IOException) {
                throw (IOException) cause;
            } else if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            } else {
                throw new IllegalStateException(cause);
            }
        } finally {
            cancel(true /* interrupt if running */);
        }
    }

    /**
     * Post the result to the callback on main thread or given handler.
     * 
     * @param handler The handler for the callback to run on if not {@code null}
     * @param callback Callback to execute.
     * @param future Future returned from a specific account manager call.
     */
    private <T> void postToHandler(Handler handler, final DataServiceCallback<T> callback,
            final DataServiceFuture<T> future) {
        handler = handler == null ? mMainHandler : handler;
        handler.post(new Runnable() {
            public void run() {
                callback.run(future);
            }
        });
    }
}
