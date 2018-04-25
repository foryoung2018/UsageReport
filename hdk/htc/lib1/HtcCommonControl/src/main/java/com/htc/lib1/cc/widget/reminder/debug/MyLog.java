package com.htc.lib1.cc.widget.reminder.debug;

import android.util.Log;
import com.htc.lib1.cc.htcjavaflag.HtcBuildFlag;

/** @hide */
public class MyLog {

    private static final String TAG = "HtcRemiView";
    // FIXME: need to close this log flag.
    // Disable MyLog.v & MyLog.d after CRC. {
    private static final boolean localLOGV = false;
    private static final boolean localLOGD = false;
    // }
    private static final boolean localLOGI = HtcBuildFlag.Htc_DEBUG_flag;
    private static boolean localSecurity = HtcBuildFlag.Htc_SECURITY_DEBUG_flag;

    public static void si(String msg) {
        if (localSecurity) {
            Log.i(TAG, msg);
        }
    }

    public static void si(String prefix, String msg) {
        if (localSecurity) {
            Log.i(TAG, output(prefix, msg));
        }
    }

    public static void se(String prefix, String msg) {
        if (localSecurity) {
            Log.w(TAG, output(prefix, msg));
        }
    }

    public static void se(String prefix, String msg, Throwable tr) {
        if (localSecurity) {
            Log.w(TAG, output(prefix, msg), tr);
        }
    }

    public static void v(String msg) {
        if (localLOGV) {
            Log.v(TAG, msg);
        }
    }

    public static void i(String msg) {
        if (localLOGI) {
            Log.i(TAG, msg);
        }
    }

    public static void d(String msg) {
        if (localLOGD) {
            Log.d(TAG, msg);
        }
    }

    public static void w(String msg) {
        Log.w(TAG, msg);
    }

    public static void e(String msg) {
        Log.w(TAG, msg);
    }

    public static void e(String msg, Throwable tr) {
        Log.w(TAG, msg, tr);
    }

    public static void v(String prefix, String msg) {
        if (localLOGV) {
            Log.v(TAG, output(prefix, msg));
        }
    }

    public static void i(String prefix, String msg) {
        if (localLOGI) {
            Log.i(TAG, output(prefix, msg));
        }
    }

    public static void d(String prefix, String msg) {
        if (localLOGD) {
            Log.d(TAG, output(prefix, msg));
        }
    }

    public static void w(String prefix, String msg) {
        Log.w(TAG, output(prefix, msg));
    }

    public static void w(String prefix, String msg, Exception ex) {
        Log.w(TAG, output(prefix, msg), ex);
    }

    public static void w(String prefix, String msg, Throwable tr) {
        Log.w(TAG, output(prefix, msg), tr);
    }

    public static void e(String prefix, String msg) {
        Log.w(TAG, output(prefix, msg));
    }

    public static void e(String prefix, String msg, Exception e) {
        Log.w(TAG, output(prefix, msg), e);
    }

    public static void e(String prefix, String msg, Throwable tr) {
        Log.w(TAG, output(prefix, msg), tr);
    }

    private static String output(String prefix, String msg) {
        return (prefix + ": " + msg);
    }
}
