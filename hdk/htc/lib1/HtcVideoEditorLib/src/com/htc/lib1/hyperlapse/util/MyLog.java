/**
 * custome Log class, with TAG and the name of the method which invoked MyLog API
 */
package com.htc.lib1.hyperlapse.util;

import android.util.Log;
/**
 * @hide
 * @author Winston
 *
 */
public class MyLog {
	public final static String TAG = "HyperLapseLib";

	static String sPkgName = "";

	/**
	 * log verbose messages with TAG and the name of the method which
	 * invoked this method
	 * 
	 * @param description
	 *            detail description shown in the log
	 */
	public static void v(String description) {
		Log.v(TAG, getFuncName() + description);
	}

	/**
	 * log debug messages with TAG and the name of the method which invoked
	 * this method
	 * 
	 * @param description
	 *            detail description shown in the log
	 */
	public static void d(String description) {
		Log.d(TAG, getFuncName() + description);
	}

	/**
	 * log info messages with TAG and the name of the method which invoked
	 * this method
	 * 
	 * @param description
	 *            detail description shown in the log
	 */
	public static void i(String description) {
		Log.i(TAG, getFuncName() + description);
	}

	/**
	 * log warn messages with TAG and the name of the method which invoked
	 * this method
	 * 
	 * @param description
	 *            detail description shown in the log
	 */
	public static void w(String description) {
		Log.w(TAG, getFuncName() + description);
	}

	/**
	 * log error messages with TAG and the name of the method which invoked
	 * this method
	 * 
	 * @param description
	 *            detail description shown in the log
	 */
	public static void e(String description) {
		Log.e(TAG, getFuncName() + description);
	}

	/**
	 * log error messages with TAG and the name of the method which invoked
	 * this method
	 * 
	 * @param description
	 *            detail description shown in the log
	 * @param e
	 *            error or exception message shown in the log
	 */
	public static void e(String description, Throwable e) {
		Log.e(TAG, getFuncName() + description, e);
	}

	/**
	 * log info messages with TAG and only show the name of the method which
	 * invoked this method
	 */
	public static void iFunc() {
		Log.i(TAG, getFuncName());
	}

	/**
	 * log info messages with TAG and some message
	 * 
	 * @param str
	 *            detail description shown in the log
	 */
	public static void assertTrue(String str) {
		Log.i(TAG, str);
	}	

	/**
	 * get the name of the method which invoked those log method
	 * 
	 * @return the name of the method which invoked those log method
	 */
	private static String getFuncName() {
		StackTraceElement ste = Thread.currentThread().getStackTrace()[4];

		String className = ste.getClassName();

		return "["
				+ sPkgName
				+ "]["
				+ className.substring(className.lastIndexOf('.') + 1,
						className.length()) + "." + ste.getMethodName() + "] ";
	}
}
