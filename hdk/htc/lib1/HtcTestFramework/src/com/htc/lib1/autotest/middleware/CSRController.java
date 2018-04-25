package com.htc.lib1.autotest.middleware;

import android.os.Handler;

/**
 * CSRController is using on control co-work target; it's main component of co-work interface
 */
public abstract class CSRController {
	private ISRCallback mCallback;

	/**
	 * Main worker of SR.
	 * @param controlTarget Control target reference.
	 * @param h Give handler if target need running on specific thread; or pass null if unnecessary
	 */
	public CSRController(Object controlTarget, Handler h) {

	}

	/**
	 * Using on set SR recorder
	 * @param callback SR recorder callback
	 */
	public void setCallback(ISRCallback callback) {
		mCallback = callback;
	}

	/**
	 * Get SR recorder callback
	 * @return SR recorder callback
	 */
	public ISRCallback getCallback() {
		return mCallback;
	}

	/**
	 * SR recorder record method
	 * @param event SREvent
	 */
	public void recordEvent(CSREvent event) {
		if (mCallback == null) {
			return;
		}
		mCallback.record(event);
	}

	/**
	 * Get co-work target handler; AP owner need return Handler from this method, if SREvent execute on specific thread(UI Thread); or return null, if no need execute on specific thread
	 * @return Handler
	 */
	public abstract Handler getHandler();

	/**
	 * Execute given event.
	 * @param event SREvent
	 */
	public abstract void injectEvent(CSREvent event); // throws Exception if error

	/**
	 * Return SRSpy instance.
	 * @return SRSpy
	 */
	public abstract ISRSpy getSpy(); // throws Exception if error

    /**
     * Generate action String from SREvent.
	 * @param event Input SREvent
	 * @return Generated Action String Array
     */
	public abstract String[] genActionString(CSREvent event);
}
