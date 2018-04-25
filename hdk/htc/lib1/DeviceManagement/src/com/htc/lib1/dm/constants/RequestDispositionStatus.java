package com.htc.lib1.dm.constants;

/**
 * Request disposition status.
 * <p>
 * Constants defining request disposition status codes.
 *
 * Created by Joe_Wu on 8/24/14.
 */
public interface RequestDispositionStatus {

    /**
     * Status code implying that the request succeeded.
     * <p>
     * Any other non-zero positive value implies that the request failed.
     */
    public static int OK = 0;

    /**
     * The server has indicated that the requested app ID is unknown.
     */
    public static int UNKNOWN_APP_ID = 1101;

    /**
     * The server has indicated that the requested app version key is unknown.
     */
    public static int UNKNOWN_APP_VERSION_KEY = 1102;

    /**
     * The server has indicated that there is no published configuration content available
     * for a known appID/version key.
     */
    public static int NO_PUBLISHED_CONTENT = 1103;

    /**
     * There is no configuration content available in the cache.
     * <p>
     * There is no entry in the configuration cache matching the requested application ID.
     * <p>
     * This is different than a request status from the server indicating that there is no content available
     * for a known application ID/version key.
     */
    public static int NO_CONTENT = 1150;

    /**
     * The app is not a known DM enabled app.
     * <p>
     * This probably means that this app has not declared itself as a DM enabled app via it's
     * application meta data.
     */
    public static int UNDECLARED_APP = 1151;
}
