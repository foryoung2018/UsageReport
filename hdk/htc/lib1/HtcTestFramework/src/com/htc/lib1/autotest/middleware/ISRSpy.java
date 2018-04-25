package com.htc.lib1.autotest.middleware;

/**
 * ISRSpy is using to get status of co-work target; can help SR IDE to generate quick list, too.
 */
public interface ISRSpy {
	/**
	 * Spy Method list in String format. Using by SmartRecorder.
	 * @return Spy method list String array.
	 */
	public String[] getMethodList();
}
