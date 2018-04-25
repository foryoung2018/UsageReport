package com.htc.lib2.photoplatformcachemanager;

import android.util.Log;

import com.htc.lib0.htcdebugflag.HtcWrapHtcDebugFlag;

public class CLog {
    private static final boolean LOG_ENABLED = HtcWrapHtcDebugFlag.Htc_DEBUG_flag;
    public static void v(String tag, String msg, boolean isAlways) {
        if ( isAlways ) {
           Log.v(tag, Thread.currentThread().getName() + " , " + msg);
        } else if ( LOG_ENABLED ) {
           Log.v(tag, Thread.currentThread().getName() + " , " + msg);
        }
    }

    public static void v(String tag, String msg, Throwable tr, boolean isAlways) {
        if ( isAlways ) {
           Log.v(tag, Thread.currentThread().getName() + " , " + msg, tr);
        } else if ( LOG_ENABLED ) {
           Log.v(tag, Thread.currentThread().getName() + " , " + msg, tr);
        }
    }

    public static void d(String tag, String msg, boolean isAlways) {
        if ( isAlways ) {
           Log.d(tag, Thread.currentThread().getName() + " , " + msg);
        } else if ( LOG_ENABLED ) {
           Log.d(tag, Thread.currentThread().getName() + " , " + msg);
        }
    }

    public static void d(String tag, String msg, Throwable tr, boolean isAlways) {
        if ( isAlways ) {
           Log.d(tag, Thread.currentThread().getName() + " , " + msg, tr);
        } else if ( LOG_ENABLED ) {
           Log.d(tag, Thread.currentThread().getName() + " , " + msg, tr);
        }
    }

    public static void i(String tag, String msg, boolean isAlways) {
        if ( isAlways ) {
           Log.i(tag, Thread.currentThread().getName() + " , " + msg);
        } else if ( LOG_ENABLED ) {
           Log.i(tag, Thread.currentThread().getName() + " , " + msg);
        }
    }
    
    public static void i(String tag, String msg, Throwable tr, boolean isAlways) {
        if ( isAlways ) {
           Log.i(tag, Thread.currentThread().getName() + " , " + msg, tr);
        } else if ( LOG_ENABLED ) {
           Log.i(tag, Thread.currentThread().getName() + " , " + msg, tr);
        }
    }

    public static void w(String tag, String msg, boolean isAlways) {
        if ( isAlways ) {
           Log.w(tag, Thread.currentThread().getName() + " , " + msg);
        } else if ( LOG_ENABLED ) {
           Log.w(tag, Thread.currentThread().getName() + " , " + msg);
        }
    }

    public static void w(String tag, String msg, Throwable tr, boolean isAlways) {
        if ( isAlways ) {
           Log.w(tag, Thread.currentThread().getName() + " , " + msg, tr);
        } else if ( LOG_ENABLED ) {
           Log.w(tag, Thread.currentThread().getName() + " , " + msg, tr);
        }
    }

    public static void w(String tag, Throwable tr, boolean isAlways) {
        if ( isAlways ) {
           Log.w(tag, Thread.currentThread().getName(), tr);
        } else if ( LOG_ENABLED ) {
           Log.w(tag, Thread.currentThread().getName(), tr);
        }
    }

    public static void e(String tag, String msg, boolean isAlways) {
        if ( isAlways ) {
           Log.e(tag, Thread.currentThread().getName() + " , " + msg);
        } else if ( LOG_ENABLED ) {
           Log.e(tag, Thread.currentThread().getName() + " , " + msg);
        }
    }
}
