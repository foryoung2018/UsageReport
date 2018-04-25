/**
 *
 */
package com.htc.test.util;

import android.content.pm.ActivityInfo;

import com.robotium.solo.Solo;

/**
 * @author felka
 *
 */
public class StressUtil {
    public interface EventCallBack {
        void onBeforeRotate(int orientation, int total, int iteration);

        void onAfterRotate(int orientation, int total, int iteration);
    }

    public static void rotateScreen(Solo solo, int total,
            EventCallBack callback, boolean portraitStart) {
        int orientation = (portraitStart) ? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                : ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        for (int i = 0; i < total; i++) {
            if (null != callback)
                callback.onBeforeRotate(orientation, total, i);
            solo.setActivityOrientation(orientation);
            if (null != callback)
                callback.onAfterRotate(orientation, total, i);
            orientation = (orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                    : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
            if (null != callback)
                callback.onBeforeRotate(orientation, total, i);
            solo.setActivityOrientation(orientation);
            if (null != callback)
                callback.onAfterRotate(orientation, total, i);
        }
    }

    public static void rotateScreen(Solo solo, int total, EventCallBack callback) {
        rotateScreen(solo, total, callback, true);
    }

    public static void rotateScreen(Solo solo, EventCallBack callback) {
        rotateScreen(solo, 1, callback);
    }
}
