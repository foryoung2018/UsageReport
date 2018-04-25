package com.htc.test.util;

import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.Context;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

/**
 * @author William Bi
 */
public class ApplicationUtils {

    public static final String ApplicationUtils_NAME = "com.htc.test.util.ApplicationUtils";

    private static WakeLock wakeLock;

    public static void acquireWakeLock(Context con) {

        PowerManager pm = (PowerManager) con
                .getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK,
                ApplicationUtils_NAME);
        wakeLock.acquire();

    }

    public static void releaseWakeLock() {
        if (null != wakeLock) {
            wakeLock.release();
            wakeLock = null;
        }

    }

    public static void unlockDevice(Context con) {
        KeyguardManager km = (KeyguardManager) con
                .getSystemService(Context.KEYGUARD_SERVICE);
        if (km.isKeyguardLocked()) {
            KeyguardLock lock = km.newKeyguardLock(ApplicationUtils_NAME);
            lock.disableKeyguard();
        }
    }

}
