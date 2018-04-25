/**
 * HTC Corporation Proprietary Rights Acknowledgment
 *
 * Copyright (C) 2010 HTC Corporation
 *
 * All Rights Reserved.
 *
 * The information contained in this work is the exclusive property of HTC Corporation
 * ("HTC").  Only the user who is legally authorized by HTC ("Authorized User") has
 * right to employ this work within the scope of this statement.  Nevertheless, the 
 * Authorized User shall not use this work for any purpose other than the purpose 
 * agreed by HTC.  Any and all addition or modification to this work shall be 
 * unconditionally granted back to HTC and such addition or modification shall be 
 * solely owned by HTC.  No right is granted under this statement, including but not 
 * limited to, distribution, reproduction, and transmission, except as otherwise 
 * provided in this statement.  Any other usage of this work shall be subject to the 
 * further written consent of HTC.
 */
package com.htc.lib1.masthead.view;

import android.annotation.TargetApi;

import com.htc.lib0.htcdebugflag.HtcWrapHtcDebugFlag;

class Logger {
	/** is debug build? */
    public static boolean securedLOGD = HtcWrapHtcDebugFlag.Htc_SECURITY_DEBUG_flag;
    public static boolean LOGD = securedLOGD && (com.htc.lib1.masthead.BuildConfig.DEBUG || HtcWrapHtcDebugFlag.Htc_DEBUG_flag );
    public static boolean API_ABOVE_18 = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2;
    
    public static void v(String tag, String msg) {
        if(LOGD) {
            android.util.Log.v(tag, msg);
        }
    }

    public static void v(String tag, String format, Object... args) {
        if(LOGD) {
            android.util.Log.v(tag, String.format(format, args));
        }
    }

    public static void v(String tag, Throwable tr, String msg) {
        if(LOGD) {
            android.util.Log.v(tag, msg, tr);
        }
    }

    public static void v(String tag, Throwable tr, String format, Object... args) {
        if(LOGD) {
            android.util.Log.v(tag, String.format(format, args), tr);
        }
    }

    public static void d(String tag, String msg) {
        if(LOGD) {
            android.util.Log.d(tag, msg);
        }
    }

    public static void d(String tag, String format, Object... args) {
        if(LOGD) {
            android.util.Log.d(tag, String.format(format, args));
        }
    }

    public static void d(String tag, Throwable tr, String msg) {
        if(LOGD) {
            android.util.Log.d(tag, msg, tr);
        }
    }

    public static void d(String tag, Throwable tr, String format, Object... args) {
        if(LOGD) {
            android.util.Log.d(tag, String.format(format, args), tr);
        }
    }

    public static void i(String tag, String msg) {
        android.util.Log.i(tag, msg);
    }

    public static void i(String tag, String format, Object... args) {
        android.util.Log.i(tag, String.format(format, args));
    }

    public static void i(String tag, Throwable tr, String msg) {
        android.util.Log.i(tag, msg, tr);
    }

    public static void i(String tag, Throwable tr, String format, Object... args) {
        android.util.Log.i(tag, String.format(format, args), tr);
    }

    public static void w(String tag, String msg) {
        android.util.Log.w(tag, msg);
    }

    public static void w(String tag, String format, Object... args) {
        android.util.Log.w(tag, String.format(format, args));
    }

    public static void w(String tag, Throwable tr, String msg) {
        android.util.Log.w(tag, msg, tr);
    }

    public static void w(String tag, Throwable tr, String format, Object... args) {
        android.util.Log.w(tag, String.format(format, args), tr);
    }

    public static void e(String tag, String msg) {
        android.util.Log.e(tag, msg);
    }

    public static void e(String tag, String format, Object... args) {
        android.util.Log.e(tag, String.format(format, args));
    }

    public static void e(String tag, Throwable tr, String msg) {
        android.util.Log.e(tag, msg, tr);
    }

    public static void e(String tag, Throwable tr, String format, Object... args) {
        android.util.Log.e(tag, String.format(format, args), tr);
    }

    @TargetApi(18)
	public static void beginTrace(String message) {
		if (LOGD && API_ABOVE_18) {

//			android.os.Trace.traceBegin(TRACE_TAG_VIEW, message);
		    android.os.Trace.beginSection(message);
		}
	}
	
    @TargetApi(18)
	public static void endTrace() {
		if (LOGD && API_ABOVE_18) {

//			android.os.Trace.traceEnd(TRACE_TAG_VIEW);
		    android.os.Trace.endSection();
		}
	}

}
