
package com.htc.lib1.cs.account;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import android.accounts.OperationCanceledException;

/**
 * A {@link DataServiceFuture} represents the result of an asynchronous call of
 * database operations encapsulated in a data service used by
 * {@link HtcAccountManager}. Methods are provided to check if the computation
 * is complete, to wait for its completion, and to retrieve the result of the
 * computation. The result can only be retrieved using method
 * {@link #getResult()} when the computation has completed, blocking if
 * necessary until it is ready. Cancellation is performed by the
 * {@link #cancel(boolean)} method. Additional methods are provided to determine
 * if the task completed normally or was cancelled. Once a computation has
 * completed, the computation cannot be cancelled.
 * 
 * @author samael_wang@htc.com
 * @param <V>
 */
public interface DataServiceFuture<V> {
    /**
     * Callback interface to use with {@link DataServiceFuture}.
     * 
     * @author samael_wang@htc.com
     * @param <V>
     */
    public interface DataServiceCallback<V> {

        /**
         * The callback method to invoke when {@link DataServiceFuture} has
         * completed the job.
         * 
         * @param future The {@link DataServiceFuture} which has completed the
         *            job.
         */
        public void run(DataServiceFuture<V> future);
    }

    /**
     * Callable interface specialized for {@link DataServiceFuture}.
     * 
     * @author samael_wang@htc.com
     * @param <V>
     */
    /* package */interface DataServiceCallable<V> extends Callable<V> {
        public V call() throws IOException;
    }

    /**
     * Attempts to cancel execution of this task. This attempt will fail if the
     * task has already completed, has already been cancelled, or could not be
     * cancelled for some other reason. If successful, and this task has not
     * started when <tt>cancel</tt> is called, this task should never run. If
     * the task has already started, then the <tt>mayInterruptIfRunning</tt>
     * parameter determines whether the thread executing this task should be
     * interrupted in an attempt to stop the task.
     * <p>
     * After this method returns, subsequent calls to {@link #isDone} will
     * always return <tt>true</tt>. Subsequent calls to {@link #isCancelled}
     * will always return <tt>true</tt> if this method returned <tt>true</tt>.
     * 
     * @param mayInterruptIfRunning <tt>true</tt> if the thread executing this
     *            task should be interrupted; otherwise, in-progress tasks are
     *            allowed to complete
     * @return <tt>false</tt> if the task could not be cancelled, typically
     *         because it has already completed normally; <tt>true</tt>
     *         otherwise
     */
    boolean cancel(boolean mayInterruptIfRunning);

    /**
     * Returns <tt>true</tt> if this task was cancelled before it completed
     * normally.
     * 
     * @return <tt>true</tt> if this task was cancelled before it completed
     */
    boolean isCancelled();

    /**
     * Returns <tt>true</tt> if this task completed. Completion may be due to
     * normal termination, an exception, or cancellation -- in all of these
     * cases, this method will return <tt>true</tt>.
     * 
     * @return <tt>true</tt> if this task completed
     */
    boolean isDone();

    /**
     * Accessor for the future result the {@link DataServiceFuture} represents.
     * This call will block until the result is available. In order to check if
     * the result is available without blocking, one may call {@link #isDone()}
     * and {@link #isCancelled()}. If the request that generated this result
     * fails or is canceled then an exception will be thrown rather than the
     * call returning normally.
     * 
     * @return the actual result
     * @throws android.accounts.OperationCanceledException if the request was
     *             canceled by the caller, or the thread is interrupted
     * @throws java.io.IOException if the authenticator returned an error
     *             response that indicates that it encountered an IOException
     *             while communicating with the authentication server
     */
    V getResult() throws OperationCanceledException, IOException;

    /**
     * Accessor for the future result the {@link DataServiceFuture} represents.
     * This call will block until the result is available. In order to check if
     * the result is available without blocking, one may call {@link #isDone()}
     * and {@link #isCancelled()}. If the request that generated this result
     * fails or is canceled then an exception will be thrown rather than the
     * call returning normally. If a timeout is specified then the request will
     * automatically be canceled if it does not complete in that amount of time.
     * 
     * @param timeout the maximum time to wait
     * @param unit the time unit of the timeout argument. This must not be null.
     * @return the actual result
     * @throws android.accounts.OperationCanceledException if the request was
     *             canceled by the caller, the thread is interrupted, or
     *             execution timed out
     * @throws java.io.IOException if the authenticator returned an error
     *             response that indicates that it encountered an IOException
     *             while communicating with the authentication server
     */
    V getResult(long timeout, TimeUnit unit) throws OperationCanceledException, IOException;
}
