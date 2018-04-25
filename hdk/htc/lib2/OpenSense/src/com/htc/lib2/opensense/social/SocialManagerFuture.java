package com.htc.lib2.opensense.social;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import android.accounts.OperationCanceledException;

/**
 * The future task interface used in {@link SocialManager}
 * 
 * @param <V> the future task type would process
 * 
 * @hide
 */
public interface SocialManagerFuture<V> {

	/**
	 * Cancels the future task
	 * 
	 * @param mayInterruptIfRunning
	 * 
	 * @return <tt>false</tt> if the task could not be cancelled
	 * 
	 * @hide
	 */
	boolean cancel(boolean mayInterruptIfRunning);

	/**
	 * Checks if the future task is cancelled or not
	 * 
	 * @return if the future task is cancelled or not
	 * 
	 * @hide
	 */
	boolean isCancelled();

	/**
	 * Checks if the future task is done or not
	 * 
	 * @return if the future task is done or not
	 * 
	 * @hide
	 */
	boolean isDone();

	/**
	 * @hide
	 */
	V getResult() throws OperationCanceledException, IOException,
			PluginException;

	/**
	 * @hide
	 */
	V getResult(long timeout, TimeUnit unit) throws OperationCanceledException,
			IOException, PluginException;
}
