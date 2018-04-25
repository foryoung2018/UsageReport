package com.htc.lib1.locationservicessettingmanager.util;

import com.htc.lib0.htcdebugflag.HtcWrapHtcDebugFlag;

public class SMLog {
	public static final boolean DEBUG_LOG_ENABLED = HtcWrapHtcDebugFlag.Htc_DEBUG_flag;

	private static final String TAG = "[HtcLocationService]";

	public static void i(String tag, String msg) {
		android.util.Log.i(TAG + tag, msg);
	}

	public static void i(String tag, String msg, Throwable e) {
		android.util.Log.i(TAG + tag, msg, e);
	}

	public static void d(String tag, String msg) {
		if (DEBUG_LOG_ENABLED) {
			android.util.Log.d(TAG + tag, msg);
		}
	}

	public static void d(String tag, String msg, Throwable e) {
		if (DEBUG_LOG_ENABLED) {
			android.util.Log.d(TAG + tag, msg, e);
		}
	}

	public static void w(String tag, String msg) {
		android.util.Log.w(TAG + tag, msg);
	}

	public static void w(String tag, String msg, Throwable e) {
		android.util.Log.w(TAG + tag, msg, e);
	}

	public static void e(String tag, String msg) {
		android.util.Log.e(TAG + tag, msg);
	}

	public static void e(String tag, String msg, Throwable e) {
		android.util.Log.e(TAG + tag, msg, e);
	}

}
