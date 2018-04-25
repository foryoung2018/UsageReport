
package com.htc.lib1.cs;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import android.os.Handler;
import android.os.Looper;

import com.htc.lib1.cs.logging.CommLoggerFactory;
import com.htc.lib1.cs.logging.HtcLogger;

/**
 * A specialized {@link FutureTask} which supports callback.
 * 
 * @author samael_wang
 * @param <V>
 */
public class CallbackFutureTask<V> extends FutureTask<V> {

    /**
     * Callback used in {@link CallbackFutureTask}.
     * 
     * @author samael_wang
     * @param <V>
     */
    public interface Callback<V> {

        /**
         * Invoked when {@link CallbackFutureTask} is done.
         * 
         * @param future The executed {@link CallbackFutureTask} instance.
         */
        public void run(CallbackFutureTask<V> future);
    }

    private HtcLogger mLogger = new CommLoggerFactory(this).create();
    private Callback<V> mCallback;
    private Handler mHandler;

    /**
     * Creates a {@code CallbackFutureTask} that will, upon running, execute the
     * given {@code Callable}.
     *
     * @param callable The callable task.
     * @throws NullPointerException if the callable is null
     */
    public CallbackFutureTask(Callable<V> callable) {
        this(callable, null, null);
    }

    /**
     * Creates a {@code CallbackFutureTask} that will, upon running, execute the
     * given {@code Callable}. The result will be delivered to {@code callback}
     * on the {@code handler} thread.
     *
     * @param callable The callable task.
     * @param callback Callback to deliver the result to.
     * @param handler Handler to decide which thread the {@code callback} should
     *            be executed on, or {@code null} to run on main thread.
     * @throws NullPointerException if the callable is null
     */
    public CallbackFutureTask(Callable<V> callable, Callback<V> callback, Handler handler) {
        super(callable);

        mCallback = callback;
        mHandler = handler != null ? handler : new Handler(Looper.getMainLooper());
    }

    /**
     * Creates a {@code CallbackFutureTask} that will, upon running, execute the
     * given {@code Runnable}, and arrange that {@code get} will return the
     * given result on successful completion.
     *
     * @param runnable The runnable task.
     * @param result the result to return on successful completion. If you don't
     *            need a particular result, consider using constructions of the
     *            form:
     *            {@code Future<?> f = new FutureTask<Void>(runnable, null)}
     * @throws NullPointerException if the runnable is null
     */
    public CallbackFutureTask(Runnable runnable, V result) {
        this(runnable, result, null, null);
    }

    /**
     * Creates a {@code CallbackFutureTask} that will, upon running, execute the
     * given {@code Runnable}, and arrange that {@code get} will return the
     * given result on successful completion. The result will be delivered to
     * {@code callback} on the {@code handler} thread.
     *
     * @param runnable The runnable task.
     * @param callback Callback to deliver the result to.
     * @param handler Handler to decide which thread the {@code callback} should
     *            be executed on, or {@code null} to run on main thread.
     * @param result the result to return on successful completion. If you don't
     *            need a particular result, consider using constructions of the
     *            form:
     *            {@code Future<?> f = new FutureTask<Void>(runnable, null)}
     * @throws NullPointerException if the runnable is null
     */
    public CallbackFutureTask(Runnable runnable, V result, Callback<V> callback, Handler handler) {
        super(runnable, result);

        mCallback = callback;
        mHandler = handler != null ? handler : new Handler(Looper.getMainLooper());
    }

    @Override
    protected void done() {
        if (mCallback != null) {
            mLogger.debug("Posting to callback ", mCallback);
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    mCallback.run(CallbackFutureTask.this);
                }
            });
        }
    }

}
