
package com.htc.lib1.cs.push.google;

import android.content.Context;
import android.os.TransactionTooLargeException;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.htc.lib1.cs.logging.HtcLogger;
import com.htc.lib1.cs.push.PushLoggerFactory;

/**
 * Helper class to check the state / availability of Google Play Services
 * package and recover if possible.
 * <p>
 * The naming follows a general rule: if a helper class includes only static
 * methods it's named as {@code Utils}, and if it needs an instance to work
 * (including singleton implementation) it's named as {@code Helper}.
 * </p>
 * 
 * @author samael_wang@htc.com
 */
public class GooglePlayServicesAvailabilityUtils {
    private static HtcLogger sLogger = new PushLoggerFactory(
            GooglePlayServicesAvailabilityUtils.class).create();

    /**
     * The representation of availability of Google Play Services.
     * 
     * @author samael_wang
     */
    public enum Availability {
        /** Indicates Google Play Services is available and ready to use. */
        AVAILABLE,

        /**
         * Indicates Google Play Services is not available but it's recoverable
         * by user interactions.
         */
        RECOVERABLE,

        /**
         * Indicates Google Play Services is not available and not recoverable
         * by user.
         */
        UNRECOVERABLE
    }

    /**
     * Check if google play services availability is available or recoverable.
     * 
     * @param status Return result code from
     *            {@link GooglePlayServicesUtil#isGooglePlayServicesAvailable(android.content.Context)}
     *            .
     * @return {@link Availability#AVAILABLE} if Google Play Services is
     *         available and ready to use; {@link Availability#RECOVERABLE} if
     *         Google Play Services is not available but recoverable by user
     *         interactions; {@link Availability#UNRECOVERABLE} if Google Play
     *         Services is not available and not recoverable by user.
     */
    public static Availability isAvaiable(int status) {
        sLogger.debug("status=", GooglePlayServicesUtil.getErrorString(status));

        if (ConnectionResult.SUCCESS == status) {
            return Availability.AVAILABLE;
        } else if (
        /* user recoverable by updating google play services */
        ConnectionResult.SERVICE_DISABLED == status
                /* user recoverable by enabling google play services */
                || ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED == status) {
            return Availability.RECOVERABLE;
        } else {
            return Availability.UNRECOVERABLE;
        }
    }

    /**
     * Safe wrapper of
     * {@link GooglePlayServicesUtil#isGooglePlayServicesAvailable(Context)}
     * which handles the {@link RuntimeException} thrown.
     * 
     * @param context context
     * @return status code indicating whether there was an error.
     */
    public static int isGooglePlayServicesAvailable(Context context) {
        try {
            return GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        } catch (RuntimeException e) {
            sLogger.warning(e);
            if (e.getCause() instanceof TransactionTooLargeException) {
                /*
                 * Somehow the response of getPackageInfo() exceeds 1MB limit,
                 * but since it has return value, we can assume
                 * GooglePlayServices presents on the system.
                 */
                return ConnectionResult.SUCCESS;
            }

            // Unknown exception happens.
            return ConnectionResult.SERVICE_MISSING;
        }
    }

}
