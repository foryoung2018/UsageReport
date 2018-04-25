package com.htc.lib1.autotest.middleware;

/**
 * CSREvent is using on transform between String object and SREvent object
 */
public class CSREvent {

	private String mEventString;
	/**
	 * Generate SREvent from String.
	 * @param strInput action String
	 */
	public CSREvent(String strInput) {
		mEventString = strInput;
	}

	/**
	 * Convert EventType and Bundle data into String
	 * @return Return SREvent String
	 */
	public String toString() {
		return mEventString;
	}
	
}
