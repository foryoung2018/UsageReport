package com.htc.lib2.opensense.social;

/**
 * The callback interface used in {@link SocialManager}
 * 
 * @param <V> the future task callback type
 * 
 * @hide
 */
public interface SocialManagerCallback<V> {

	/**
	 * Runs the future task
	 * 
	 * @param future the given future task
	 * 
	 * @hide
	 */
	void run(SocialManagerFuture<V> future);

}
