package com.htc.lib1.autotest.middleware;

/**
 * ISREntry is co-work interface entry; it need implement in a View object
 */
public interface ISREntry {
	/**
	 * SmartRecorder manipulate entry
	 * @return CSRController {@link CSRController}
	 */
	public CSRController getSRController(); // return null if not support
}
