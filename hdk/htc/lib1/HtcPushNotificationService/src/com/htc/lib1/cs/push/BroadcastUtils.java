
package com.htc.lib1.cs.push;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;

import com.htc.lib0.htcdebugflag.HtcWrapHtcDebugFlag;
import com.htc.lib1.cs.logging.HtcLogger;

/**
 * Helper class to send broadcasts.
 * <p>
 * The naming follows a general rule: if a helper class includes only static
 * methods it's named as {@code Utils}, and if it needs an instance to work
 * (including singleton implementation) it's named as {@code Helper}.
 * </p>
 * 
 * @author samael_wang@htc.com
 */
public class BroadcastUtils {
    private static HtcLogger sLogger = new PushLoggerFactory(BroadcastUtils.class).create();

    /**
     * Deliver messages to app packages. Note if the push client works as a
     * library, it won't deliver any message to other packages.
     * 
     * @param context Context to operate on.
     * @param applist Target app package names separated with spaces.
     * @param messages Messages to send. Must not be {@code null}.
     */
    public static void deliverMessages(Context context, String applist, Bundle messages) {
        if (context == null)
            throw new IllegalArgumentException("'context' is null.");
        if (TextUtils.isEmpty(applist))
            throw new IllegalArgumentException("'applist' is null or empty.");
        if (messages == null)
            throw new IllegalArgumentException("'messages' is null.");

        Intent intent = new Intent(PnsBroadcasts.ACTION_DELIVER_MESSAGE);
        intent.putExtras(messages);

        if (PnsDefs.PKG_NAME_PNS_CLIENT.equals(context.getPackageName())) {
            // Deliver to all receivers as a central PNS agent.
            for (String app : applist.split("\\s+")) {
                sLogger.debugS("Delivering message to ", app);
                intent.setPackage(app);
                sendBroadcast(context, intent);
            }
        } else {
            // Deliver to local only.
            sLogger.debug("Delivering message locally.");
            sendBroadcast(context, intent);
        }
    }

    /**
     * Send registration succeed broadcast.
     * 
     * @param context Context to operate on.
     */
    public static void registrationSuccessed(Context context) {
        if (context == null)
            throw new IllegalArgumentException("'context' is null.");

        sendBroadcast(context, new Intent(PnsBroadcasts.ACTION_REGISTRATION_SCUESSED));
    }

    /**
     * Send registration failed broadcast.
     * 
     * @param context Context to operate on.
     * @param message Error message for diagnosing.
     */
    public static void registrationFailed(Context context, String message) {
        if (context == null)
            throw new IllegalArgumentException("'context' is null.");

        Intent intent = new Intent(PnsBroadcasts.ACTION_REGISTRATION_FAILED);
        intent.putExtra(PnsBroadcasts.KEY_MESSAGE, message);
        sendBroadcast(context, intent);
    }

    /**
     * Send update registration succeed broadcast.
     * 
     * @param context Context to operate on.
     */
    public static void updateSuccessed(Context context) {
        if (context == null)
            throw new IllegalArgumentException("'context' is null.");

        sendBroadcast(context, new Intent(PnsBroadcasts.ACTION_UPDATE_SCUESSED));
    }

    /**
     * Send update registration failed broadcast.
     * 
     * @param context Context to operate on.
     * @param message Error message for diagnosing.
     */
    public static void updateFailed(Context context, String message) {
        if (context == null)
            throw new IllegalArgumentException("'context' is null.");

        Intent intent = new Intent(PnsBroadcasts.ACTION_UPDATE_FAILED);
        intent.putExtra(PnsBroadcasts.KEY_MESSAGE, message);
        sendBroadcast(context, intent);
    }

    /**
     * Send unregistration succeed broadcast.
     * 
     * @param context Context to operate on.
     */
    public static void unregistrationSuccessed(Context context) {
        if (context == null)
            throw new IllegalArgumentException("'context' is null.");

        sendBroadcast(context, new Intent(PnsBroadcasts.ACTION_UNREGISTRATION_SCUESSED));
    }

    /**
     * Send unregistration failed broadcast.
     * 
     * @param context Context to operate on.
     * @param message Error message for diagnosing.
     */
    public static void unregistrationFailed(Context context, String message) {
        if (context == null)
            throw new IllegalArgumentException("'context' is null.");

        Intent intent = new Intent(PnsBroadcasts.ACTION_UNREGISTRATION_FAILED);
        intent.putExtra(PnsBroadcasts.KEY_MESSAGE, message);
        sendBroadcast(context, intent);
    }

    /**
     * Send a broadcast. All broadcasts apply the flag
     * {@link Intent#FLAG_INCLUDE_STOPPED_PACKAGES}, and require permission
     * {@link PnsInternalDefs#PERMISSION_RECEIVE_MESSAGE} if it works as a stand
     * alone push client.
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

        if (PnsDefs.PKG_NAME_PNS_CLIENT.equals(context.getPackageName())) {
            // Sending broadcasts as a central PNS agent.
            context.sendBroadcast(intent, PnsInternalDefs.PERMISSION_RECEIVE_MESSAGE);
        } else {
            // Sending broadcasts to local only.
            boolean delivered = LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            sLogger.verbose("broadcast delivered: ", delivered);
        }
    }
}
