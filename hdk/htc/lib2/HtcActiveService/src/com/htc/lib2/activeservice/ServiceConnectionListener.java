package com.htc.lib2.activeservice;

/**
 * Used for receiving notifications from HtcActiveManager when active
 * service is connected or disconnected.
 */

public interface ServiceConnectionListener {

    /**
     * Called when active service is connected.
     * @return
     */
    public void onConnected();

    /**
     * Called when active service is disconnected.
     * @return
     */
    public void onDisconnected();

}
