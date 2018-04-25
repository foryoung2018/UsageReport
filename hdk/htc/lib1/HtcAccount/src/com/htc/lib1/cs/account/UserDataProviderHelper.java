
package com.htc.lib1.cs.account;

import android.annotation.SuppressLint;
import android.content.ContentProviderClient;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import com.htc.lib1.cs.logging.CommLoggerFactory;
import com.htc.lib1.cs.logging.HtcLogger;

/**
 * Helper class to access user data provider. It only works with system HTC
 * Account authenticator.
 * 
 * @author samael_wang@htc.com
 */
public class UserDataProviderHelper {
    private HtcLogger mLogger = new CommLoggerFactory(this).create();
    private Context mContext;
    private Uri mUserDataUri;

    /**
     * Create an instance.
     * 
     * @param context {@link Context} to operate on.
     */
    public UserDataProviderHelper(Context context) {
        if (context == null)
            throw new IllegalArgumentException("'context' is null.");

        mContext = context;
    }

    /**
     * Get the system HTC Account user data provider URI.
     * 
     * @return The content URI (containing the path "/userdata") accordingly or
     *         {@code null} if no provider userdata provider found.
     */
    public Uri getUserDataProviderUri() {
        if (mUserDataUri != null) {
            return mUserDataUri;
        }

        // Finding the correct URI by making acquisition.
        ContentProviderClient client = acquireUserDataProviderClient();
        if (client != null) {
            client.release();
            mLogger.debugS("Use userdata provider: ", mUserDataUri);
            return mUserDataUri;
        }

        mLogger.warning("Not able to find proper user data provider to use.");
        return null;
    }

    /**
     * Acquire a content provider client of user data provider from the system
     * HTC Account authenticator.
     * 
     * @return {@link ContentProviderClient} or {@code null} if no provider
     *         userdata provider found.
     */
    @SuppressLint("NewApi")
    public ContentProviderClient acquireUserDataProviderClient() {
        ContentProviderClient client;

        if (mUserDataUri != null) {
            /*
             * Use acquireUnstableContentProviderClient if available (i.e.
             * api16+).
             */
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                client = mContext.getContentResolver()
                        .acquireUnstableContentProviderClient(mUserDataUri);
            } else {
                client = mContext.getContentResolver().acquireContentProviderClient(mUserDataUri);
            }
        } else {
            Uri workingContentUri;

            /*
             * Try if the preferred provider exists. Use
             * acquireUnstableContentProviderClient if available (i.e. api16+).
             */
            workingContentUri = HtcAccountDefs.CONTENT_URI_USER_DATA_PROVIDER;
            mLogger.debugS("Trying userdata provider: ", workingContentUri);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                client = mContext.getContentResolver()
                        .acquireUnstableContentProviderClient(workingContentUri);
            } else {
                client = mContext.getContentResolver()
                        .acquireContentProviderClient(workingContentUri);
            }

            if (client == null) {
                /*
                 * Try to fallback to legacy content Uri.Use
                 * acquireUnstableContentProviderClient if available (i.e.
                 * api16+).
                 */
                workingContentUri = HtcAccountDefs.CONTENT_URI_LEGACY_USER_DATA_PROVIDER;
                mLogger.debugS("Trying userdata provider: ", workingContentUri);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    client = mContext.getContentResolver()
                            .acquireUnstableContentProviderClient(workingContentUri);
                } else {
                    client = mContext.getContentResolver()
                            .acquireContentProviderClient(workingContentUri);
                }
            }

            // Update URI cache.
            if (client != null) {
                mUserDataUri = workingContentUri;
            }
        }

        return client;
    }
}
