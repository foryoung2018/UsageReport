package com.htc.lib2.activeservice;

import com.htc.lib0.htcdebugflag.HtcWrapHtcDebugFlag;

class Log {

    private final static boolean ENABLED = HtcWrapHtcDebugFlag.Htc_DEBUG_flag;

    private final static int FLAG_NOT_ENABLED = -1;

    public static int e(String TAG, String messge) {
        return android.util.Log.e(TAG, messge);
    }

    public static int w(String TAG, String messge) {
        if (ENABLED) {
            return android.util.Log.w(TAG, messge);
        } else {
            return FLAG_NOT_ENABLED;
        }
    }

    public static int i(String TAG, String messge) {
        if (ENABLED) {
            return android.util.Log.i(TAG, messge);
        } else {
            return FLAG_NOT_ENABLED;
        }
    }

    public static int d(String TAG, String messge) {
        if (ENABLED) {
            return android.util.Log.d(TAG, messge);
        } else {
            return FLAG_NOT_ENABLED;
        }
    }

    public static int v(String TAG, String messge) {
        if (ENABLED) {
            return android.util.Log.v(TAG, messge);
        } else {
            return FLAG_NOT_ENABLED;
        }
    }

}
