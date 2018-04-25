
package com.htc.lib1.cs.googleads;

import android.content.Context;

import com.htc.lib1.cs.logging.CommLoggerFactory;
import com.htc.lib1.cs.logging.HtcLogger;

/**
 * Google advertise utils.
 * 
 * @author autosun_li@htc.com
 */
public class GoogleAdvertiseUtils {
    private static HtcLogger sLogger = new CommLoggerFactory(GoogleAdvertiseUtils.class).create();

    /**
     * Get Google advertising ID. Return empty string if not available.
     * It is a blocking call, should never be called on main thread.
     *
     * @return Google advertising ID.
     */
    public static String getAdvertisingId(Context context) {
        sLogger.verbose();

        String id = "";

        try {
            /*
             * Get google advertising ID via JAVA reflection to loose
             * dependency of google play service library.
             */
            Class<?> advertisingIdClientClass =
                Class.forName("com.google.android.gms.ads.identifier.AdvertisingIdClient");
            Class<?>[] paramTypes = { Context.class };
            Object[] params = { context };
            Object infoObj = advertisingIdClientClass.getMethod("getAdvertisingIdInfo", paramTypes)
                .invoke(null, params);

            Class<?> infoClass =
                Class.forName("com.google.android.gms.ads.identifier.AdvertisingIdClient$Info");
            Object gaidObj = infoClass.getMethod("getId", (Class<?>[]) null)
                .invoke(infoObj, (Object[]) null);
            id = (String) gaidObj;
        } catch (Exception e) {
            sLogger.warningS("Get google advertising id failed due to ", e.toString());
        } finally {
            sLogger.debugS("Google ads id: ", id);
        }

        return id;
    }
}

