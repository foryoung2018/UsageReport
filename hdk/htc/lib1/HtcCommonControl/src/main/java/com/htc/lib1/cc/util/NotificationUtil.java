package com.htc.lib1.cc.util;

import java.lang.reflect.Field;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class NotificationUtil {
    private static final String TAG = "NotificationUtil";
    private static final String CONSTS_PATH = "android.service.notification.Consts";

    /**
     * Helper method to get internal string constant value
     */
    public static String getStringConst(String constName) {
        Object value = getConst(constName);
        return value != null ? value.toString() : null;
    }

    /**
     * Helper method to get internal integer constant value
     */
    public static int getIntConst(String constName) {
        Object value = getConst(constName);
        int intValue = Integer.MIN_VALUE;
        if (value == null)
            return intValue;
        try {
            intValue = (Integer) value;
        } catch (Exception e) {
            Log.w(TAG, constName + " is not an integer", e);
        }
        return intValue;
    }

    /**
     * Helper method to get internal constant value
     */
    public static Object getConst(String constName) {
        if (constName == null) {
            Log.w(TAG, "const name is null");
            return null;
        }

        Class clz = null;
        try {
            clz = Class.forName(CONSTS_PATH);
        } catch (ClassNotFoundException e) {
            Log.w(TAG, "can't find class");
            return null;
        }

        Field field = null;
        try {
            field = clz.getField(constName);
        } catch (NoSuchFieldException e) {
            Log.w(TAG, "can't find const: " + constName);
            return null;
        }

        Object value = null;
        try {
            value = field.get(null);
        } catch (IllegalAccessException e) {
            Log.w(TAG, "can't access value: " + constName, e);
            return null;
        }

        return value;
    }

    /**
     * Helper method to enable notification features
     */
    public static Notification enableNotificationFeatures(Notification n,
            String... features) {
        final String key = getStringConst("EXTRA_HTC_FEATURES");
        if (key != null) {
            int value = 0;
            for (String feature : features) {
                int flag = getIntConst(feature);
                if (flag != Integer.MIN_VALUE)
                    value |= flag;
            }
            if (value > 0) {
                Bundle b = n.extras;
                if (b == null) {
                    b = new Bundle();
                } else {
                    value |= b.getInt(key, 0);
                }
                b.putInt(key, value);
                n.extras = b;
            }
        }
        return n;
    }

    /**
     * Helper method to control status bar glowing effect
     */
    public static void glow(Context context, String mode) {
        Log.d(TAG, "set glow mode to " + mode);
        final String action = getStringConst("STATUS_BAR_GLOW_ACTION");
        if (context != null && action != null) {
            String extraKey = getStringConst("STATUS_BAR_GLOW_MODE");
            int glowMode = getIntConst(mode);
            if (extraKey != null && glowMode != Integer.MIN_VALUE) {
                Intent intent = new Intent(action);
                intent.putExtra(extraKey, glowMode);
                context.sendBroadcast(intent);
            }
        }
    }
}
