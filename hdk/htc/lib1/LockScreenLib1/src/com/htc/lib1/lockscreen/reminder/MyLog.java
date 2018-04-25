package com.htc.lib1.lockscreen.reminder;

import com.htc.lib0.htcdebugflag.HtcWrapHtcDebugFlag;

import android.util.Log;

/** @hide */
class MyLog {
    
    private static final String TAG = "HtcRemiView";
    // FIXME: need to close this log flag.
    // Disable MyLog.v & MyLog.d after CRC. {
    private static final boolean localLOGV = false;
    private static final boolean localLOGD = false;
    // }
    private static final boolean localLOGI = HtcWrapHtcDebugFlag.Htc_DEBUG_flag;
    private static boolean localSecurity = HtcWrapHtcDebugFlag.Htc_SECURITY_DEBUG_flag;
    
    static void si(String prefix, String msg) {
        if (localSecurity) {
            Log.i(TAG, output(prefix, msg));
        }
    }
    
    static void se(String prefix, String msg) {
        if (localSecurity) {
            Log.w(TAG, output(prefix, msg));
        }
    }

    static void v(String prefix, String msg) {
        if (localLOGV) {
            Log.v(TAG, output(prefix, msg));
        }
    }
    
    static void i(String prefix, String msg) {
        if (localLOGI) {
            Log.i(TAG, output(prefix, msg));
        }
    }
    
    static void d(String prefix, String msg) {
        if (localLOGD) {
            Log.d(TAG, output(prefix, msg));
        }
    }
    
    static void w(String prefix, String msg) {
        Log.w(TAG, output(prefix, msg));
    }
    
    static void w(String prefix, String msg, Exception ex) {
        Log.w(TAG, output(prefix, msg), ex);
    }

    static void e(String prefix, String msg) {
        Log.w(TAG, output(prefix, msg));
    }
    
    static void e(String prefix, String msg, Exception e) {
        Log.w(TAG, output(prefix, msg), e);
    }

    private static String output(String prefix, String msg) {
        return (prefix + ": " + msg);
    }
}

