package com.htc.lib1.cc.util;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.Log;

/**
 * For Android-N Multiple Window. The multiple window feature will make the decision by
 * orientation check is not validated, so we need to decide and check if the window screen
 * is suitable for landscape's layout.
 *  @hide
 * @Deprecate
 */
public class WindowUtil {
    private static final String TAG = "WindowUtil";

    private static void logStackTrace(String msg) {
        StackTraceElement[] steList = Thread.currentThread().getStackTrace();
        if ( null == steList )
            return;

        Log.e(TAG, msg);

        for ( int i = 3, len = steList.length; i < len; i++) {
            StackTraceElement ste = steList[i];
            Log.e(TAG, ste.toString());
        }
    }

    /* to decide if the current window is suitable to apply landscape mode in the multiple window mode.
     * @Param res The Resources from view or actvity
     * @Return return true if the window is suitable for landscape, otherwise false
     */
    public static boolean isSuitableForLandscape(Resources res) {
        if ( null == res ) {
            logStackTrace("There is no resources instance");
            return false;
        }

        Configuration c = res.getConfiguration();
        if ( null == c ) {
            logStackTrace("There is no configuration instance");
            return false;
        }

        return ( c.screenWidthDp > c.screenHeightDp
                && ((c.screenWidthDp - c.screenHeightDp) > (c.smallestScreenWidthDp/2)) )?true:false;
    }

    public static int getScreenWidthPx(Resources res) {
        if ( null == res ) {
            logStackTrace("There is no resources instance");
            return 0;
        }

        int widthDp = res.getConfiguration().screenWidthDp;
        float scale = res.getDisplayMetrics().density;
        return  (int) (widthDp * scale + 0.5f);
    }

    public static int getScreenHeightPx(Resources res) {
        if ( null == res ) {
            logStackTrace("There is no resources instance");
            return 0;
        }

        int heightDp = res.getConfiguration().screenHeightDp;
        float scale = res.getDisplayMetrics().density;
        return  (int) (heightDp * scale + 0.5f);
    }
}
