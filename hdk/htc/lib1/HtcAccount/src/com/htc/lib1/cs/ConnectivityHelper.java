
package com.htc.lib1.cs;

import com.htc.lib1.cs.httpclient.ConnectivityException;
import com.htc.lib1.cs.logging.CommLoggerFactory;
import com.htc.lib1.cs.logging.HtcLogger;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Helper class to detect connectivity status.
 * <p>
 * The naming follows a general rule: if a helper class includes only static
 * methods it's named as {@code Utils}, and if it needs an instance to work
 * (including singleton implementation) it's named as {@code Helper}.
 * </p>
 * 
 * @author samael_wang@htc.com
 */
public class ConnectivityHelper {
    public static final int TYPE_INVALID = -1;
    private static ConnectivityHelper sInstance;
    private HtcLogger mLogger = new CommLoggerFactory(this).create();
    private ConnectivityManager mManager;

    /**
     * Get the singleton instance of {@link ConnectivityHelper}
     * 
     * @param context Context used to retrieve application context and create
     *            the instance if necessary.
     * @return Instance.
     */
    public static synchronized ConnectivityHelper get(Context context) {
        // Check argument.
        if (context == null)
            throw new IllegalArgumentException("'context' is null.");

        // Create instance if not created before.
        if (sInstance == null) {
            sInstance = new ConnectivityHelper(context.getApplicationContext());
        }

        return sInstance;
    }

    /**
     * Create an instance.
     * 
     * @param context Context to operate on.
     */
    private ConnectivityHelper(Context context) {
        mManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    /**
     * Check if data network is available.
     * 
     * @return {@code true} if available.
     */
    public boolean isConnected() {
        NetworkInfo info = mManager.getActiveNetworkInfo();
        boolean connected = (info != null && info.isConnected());
        mLogger.verbose("connected=", connected);
        return connected;
    }

    /**
     * Check if the data network is available, or throw an exception.
     * 
     * @throws ConnectivityException If data network is unavailable.
     */
    public void isConnectedOrThrow() throws ConnectivityException {
        if (!isConnected())
            throw new ConnectivityException();
    }

    /**
     * Get the active network type.
     * 
     * @return Activity network type as in {@link NetworkInfo#getType()} or
     *         {@link #TYPE_INVALID} if no data network available.
     */
    public int getActiveNetworkType() {
        NetworkInfo info = mManager.getActiveNetworkInfo();
        int type = TYPE_INVALID;
        if (info != null) {
            mLogger.verbose("active network type = ", info.getTypeName());
            type = info.getType();
        }
        return type;
    }
}
