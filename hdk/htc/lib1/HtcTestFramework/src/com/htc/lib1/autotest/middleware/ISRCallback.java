package com.htc.lib1.autotest.middleware;

/**
 * ISRCallback is using for SR recorder
 */
public interface ISRCallback {

	/**
	 * Implement this method to record SREvent to SR script
	 * @param event Input SREvent which want record to SR script
	 */
	void record(CSREvent event);
}
