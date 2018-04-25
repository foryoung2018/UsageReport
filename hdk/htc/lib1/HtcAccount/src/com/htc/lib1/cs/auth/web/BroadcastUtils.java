
package com.htc.lib1.cs.auth.web;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;

import com.htc.lib0.htcdebugflag.HtcWrapHtcDebugFlag;
import com.htc.lib1.cs.account.HtcAccountBroadcasts;
import com.htc.lib1.cs.auth.AuthLoggerFactory;
import com.htc.lib1.cs.auth.BuildConfig;
import com.htc.lib1.cs.logging.HtcLogger;

/**
 * Helper class to send identity broadcasts locally.
 * <p>
 * The naming follows a general rule: if a helper class includes only static
 * methods it's named as {@code Utils}, and if it needs an instance to work
 * (including singleton implementation) it's named as {@code Helper}.
 * </p>
 * 
 * @author samael_wang@htc.com
 */
/* package */class BroadcastUtils {
    private static HtcLogger sLogger = new AuthLoggerFactory(BroadcastUtils.class).create();

    /**
     * Send a broadcast to indicate add account flow has completed.
     * 
     * @param context Context to operate on.
     */
    public static void addAccountCompleted(Context context) {
        if (context == null)
            throw new IllegalArgumentException("'context' is null.");

        // Broadcast add account completed message.
        Intent intent = new Intent(HtcAccountBroadcasts.ACTION_ADD_ACCOUNT_COMPLETED);
        intent.putExtra(HtcAccountBroadcasts.KEY_SOURCE_PACKAGE, context.getPackageName());
        sendBroadcast(context, intent);
    }

    /**
     * Send a broadcast to indicate add account flow has been canceled.
     * 
     * @param context Context to operate on.
     */
    public static void addAccountCanceled(Context context) {
        if (context == null)
            throw new IllegalArgumentException("'context' is null.");

        // Broadcast add account canceled message.
        Intent intent = new Intent(HtcAccountBroadcasts.ACTION_ADD_ACCOUNT_CANCELED);
        intent.putExtra(HtcAccountBroadcasts.KEY_SOURCE_PACKAGE, context.getPackageName());
        sendBroadcast(context, intent);
    }

    /**
     * Send a broadcast to indicate account has been removed.
     * 
     * @param context Context to operate on.
     * @param accountChanged True to indicate the account removal was because of
     *            re-signin.
     */
    public static void removeAccountCompleted(Context context) {
        if (context == null)
            throw new IllegalArgumentException("'context' is null.");

        // Broadcast account removed message.
        Intent intent = new Intent(HtcAccountBroadcasts.ACTION_REMOVE_ACCOUNT_COMPLETED);
        sendBroadcast(context, intent);
    }

    /**
     * Send a broadcast to indicate authtoken has been renewed.
     * 
     * @param context Context to operate on.
     * @param authTokenType Type of authtoken which has been renewed.
     */
    public static void authTokenRenewed(Context context, String authTokenType) {
        if (context == null)
            throw new IllegalArgumentException("'context' is null.");
        if (TextUtils.isEmpty(authTokenType))
            throw new IllegalArgumentException("'authTokenType' is null or empty.");

        // Broadcast authtoken renewed message.
        Intent intent = new Intent(HtcAccountBroadcasts.ACTION_AUTH_TOKEN_RENEWED);
        intent.putExtra(HtcAccountBroadcasts.KEY_AUTHTOKEN_TYPE, authTokenType);
        sendBroadcast(context, intent);
    }

    /**
     * Send a local broadcast.
     * 
     * @param context Context to operate on.
     * @param intent Intent to send.
     */
    private static void sendBroadcast(Context context, Intent intent) {
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);

        // Debug resolution only if one of the debug flags is true.
        if (HtcWrapHtcDebugFlag.Htc_SECURITY_DEBUG_flag
                || HtcWrapHtcDebugFlag.Htc_DEBUG_flag
                || BuildConfig.DEBUG) {
            intent.addFlags(Intent.FLAG_DEBUG_LOG_RESOLUTION);
        }

        sLogger.verboseS(intent);
        sLogger.verbose("broadcast delivered: ",
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent));
    }
}
