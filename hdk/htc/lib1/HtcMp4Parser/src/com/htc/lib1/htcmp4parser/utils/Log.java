package com.htc.lib1.htcmp4parser.utils;
/**
 * Log utility
 *  @hide
 * {@exthide}
 */
public class Log {
	static private final String TAG = "HTC_MP4_Parser";
	/**
	 * Log function
	 * @param msg print text for msg
	 * @hide
	 */
	static public void i(String msg){
		i(TAG,msg);
	}
	
	/**
	 * Log function
	 * @param tag tag
	 * @param msg msg
	 * @hide
	 */
	static public void i(String tag, String msg) {
		i(tag, msg, null);
	}
	
	/**
	 * Log function
	 * @param msg msg
	 * @param e e
	 * @hide
	 */
	static public void i(String msg, Throwable e) {
		i(TAG, msg , e);
	}
	
	/**
	 * Log function
	 * @param tag tag
	 * @param msg msg
	 * @param e e
	 * @hide
	 */
	static public void i(String tag, String msg, Throwable e) {
		android.util.Log.i(TAG, getLogString(tag, msg), e);
	}
	
	/**
	 * Log function
	 * @param msg msg
	 * @hide
	 */
	static public void v(String msg) {
		v(TAG, msg, null);
	}
	
	/**
	 * Log function
	 * @param tag tag
	 * @param msg msg
	 * @hide
	 */
	static public void v(String tag, String msg) {
		v(tag, msg, null);
	}
	
	/**
	 * Log function
	 * @param msg msg
	 * @param e e
	 * @hide
	 */
	static public void v(String msg, Throwable e) {
		v(TAG, msg , e);
	}
	
	/**
	 * Log function
	 * @param tag tag
	 * @param msg msg
	 * @param e e
	 * @hide
	 */
	static public void v(String tag, String msg, Throwable e) {
		android.util.Log.v(TAG, getLogString(tag, msg), e);
	}
	
	/**
	 * Log function
	 * @param msg msg
	 * @hide
	 */
	static public void w(String msg) {
		w(TAG, msg, null);
	}
	
	/**
	 * Log function
	 * @param tag tag
	 * @param msg msg
	 * @hide
	 */
	static public void w(String tag, String msg) {
		w(tag, msg, null);
	}
	
	/**
	 * Log function
	 * @param msg msg
	 * @param e e
	 * @hide
	 */
	static public void w(String msg, Throwable e) {
		android.util.Log.w(TAG, msg , e);
	}
	
	/**
	 * Log function
	 * @param tag tag
	 * @param msg msg
	 * @param e e
	 * @hide
	 */
	static public void w(String tag, String msg, Throwable e) {
		android.util.Log.w(TAG, getLogString(tag, msg), e);
	}
	
	/**
	 * Log function
	 * @param msg msg
	 * @hide
	 */
	static public void d(String msg) {
		d(TAG, msg, null);
	}
	
	/**
	 * Log function
	 * @param tag tag
	 * @param msg msg
	 * @hide
	 */
	static public void d(String tag, String msg) {
		d(tag, msg, null);
	}
	
	/**
	 * Log function
	 * @param msg msg
	 * @param e e
	 * @hide
	 */
	static public void d( String msg, Throwable e) {
		d(TAG, msg ,e);
	}
	
	/**
	 * Log function
	 * @param tag tag
	 * @param msg msg
	 * @param e e
	 * @hide
	 */
	static public void d(String tag, String msg, Throwable e) {
		android.util.Log.d(TAG, getLogString(tag, msg), e);
	}
	
	/**
	 * Log function
	 * @param msg msg
	 * @hide
	 */
	static public void e(String msg) {
		e(TAG, msg, null);
	}
	
	/**
	 * Log function
	 * @param tag tag
	 * @param msg msg
	 * @hide
	 */
	static public void e(String tag, String msg) {
		e(tag, msg, null);
	}
	
	/**
	 * Log function
	 * @param msg msg
	 * @param e e
	 * @hide
	 */
	static public void e(String msg, Throwable e) {
		e(TAG, msg ,e);
	}
	
	/**
	 * Log function
	 * @param tag tag
	 * @param msg msg
	 * @param e e
	 * @hide
	 */
	static public void e(String tag, String msg, Throwable e) {
		android.util.Log.e(TAG, getLogString(tag, msg), e);
	}
	
	static private String getLogString(String tag, String msg) {
		return String.format("%s\t%s", tag != null? tag: "", msg);
	}
}
