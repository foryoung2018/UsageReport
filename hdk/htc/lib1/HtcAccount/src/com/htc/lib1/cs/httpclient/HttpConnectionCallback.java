
package com.htc.lib1.cs.httpclient;

/**
 * Callback to use when the {@link HttpConnectionFuture} finishes.
 * 
 * @author samael_wang@htc.com
 * @param <T>
 */
public interface HttpConnectionCallback<T> {

    /**
     * To be invoked when {@link HttpConnectionFuture} finishes.
     * 
     * @param future The {@link HttpConnectionFuture} which has completed.
     */
    public void run(HttpConnectionFuture<T> future);
}
