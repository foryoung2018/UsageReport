
package com.htc.lib1.cs.pns;

/**
 * PNS Client broadcasts.
 * 
 * @author samael_wang@htc.com
 */
public class PnsBroadcasts {

    /**
     * Action of message delivery intent. The intent extras includes
     * service-dependent messages defined by the integrated services.
     */
    public static final String ACTION_DELIVER_MESSAGE = "com.htc.cs.pushclient.DELIVER_MESSAGE";

    /** Broadcast to indicate registration succeed. Yes it's a typo. */
    public static final String ACTION_REGISTRATION_SCUESSED = "com.htc.cs.pushclient.REGISTRATION_SUCCESSED";

    /**
     * Broadcast to indicate registration failed. The error message could be
     * extracted with {@link #KEY_MESSAGE}.
     */
    public static final String ACTION_REGISTRATION_FAILED = "com.htc.cs.pushclient.REGISTRATION_FAILED";

    /** Broadcast to indicate update succeed. Yes it's a typo. */
    public static final String ACTION_UPDATE_SCUESSED = "com.htc.cs.pushclient.UPDATE_SCUESSED";

    /**
     * Broadcast to indicate update failed. The error message could be extracted
     * with {@link #KEY_MESSAGE}.
     */
    public static final String ACTION_UPDATE_FAILED = "com.htc.cs.pushclient.UPDATE_FAILED";

    /** Broadcast to indicate unregistration succeed. Yes it's a typo. */
    public static final String ACTION_UNREGISTRATION_SCUESSED = "com.htc.cs.pushclient.UNREGISTRATION_SCUESSED";

    /**
     * Broadcast to indicate unregistration failed. The error message could be
     * extracted with {@link #KEY_MESSAGE}.
     */
    public static final String ACTION_UNREGISTRATION_FAILED = "com.htc.cs.pushclient.UNREGISTRATION_FAILED";

    /** Message used in broadcasts. */
    public static final String KEY_MESSAGE = "com.htc.cs.pushclient.MESSAGE";
}
