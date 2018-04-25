
package com.htc.lib1.cc.util;

import android.content.Context;

import com.htc.lib2.configuration.HtcWrapConfiguration;

/**
 * @hide
 * @deprecated internal use only
 */
@Deprecated
public class HtcWrapConfigurationUtil {

    /**
     * Apply HTC specific Fontscale Configuration Caller must have permission
     * "com.htc.permission.APP_DEFAULT" and used System UID.
     *
     * @param context the context which want to apply HTC specific fontscale size configuration.
     * @return true if HTC specific fontscale size applied successfully, false otherwise.
     * @throws RuntimeException throws RuntimeException if HDK invoke failed.
     */
    public static boolean applyHtcFontscale(Context context) {
        boolean res = false;
        try {
            res = HtcWrapConfiguration.applyHtcFontscale(context);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Check if HTC specific Fontscale Configuration should be applied? Caller must have permission
     * "com.htc.permission.APP_DEFAULT" and used System UID.
     *
     * @param context the context which want to check if HTC specific fontscale size configuration
     *            should be applied.
     * @param applied if the activity already applied the htc fontscale size.
     * @return true if HTC new specific fontscale size should be applied, false otherwise.
     * @throws RuntimeException throws RuntimeException if HDK invoke failed.
     * @deprecated [use checkHtcFontscaleChanged(Context context, float fontScale) instead]
     */
    public static boolean checkHtcFontscaleChanged(Context context, boolean applied) {
        boolean res = false;
        try {
            res = HtcWrapConfiguration.checkHtcFontscaleChanged(context, applied);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Check if HTC specific Fontscale Configuration should be applied? Caller must have permission
     * "com.htc.permission.APP_DEFAULT" and used System UID.
     *
     * @param context the context which want to check if HTC specific fontscale size configuration
     *            should be applied.
     * @param fontScale the fontScale applied last time
     * @return true if HTC new specific fontscale size should be applied, false otherwise.
     * @throws RuntimeException throws RuntimeException if HDK invoke failed.
     */
    public static boolean checkHtcFontscaleChanged(Context context, float fontScale) {
        boolean res = false;
        try {
            res = HtcWrapConfiguration.checkHtcFontscaleChanged(context, fontScale);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        return res;
    }

}
