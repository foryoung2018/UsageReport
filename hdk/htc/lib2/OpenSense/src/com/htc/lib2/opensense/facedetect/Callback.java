package com.htc.lib2.opensense.facedetect;

import com.htc.lib2.opensense.facedetect.Entry;

/**
 * asynchronous face detection callback interface
 * 
 * @hide
 */
public interface Callback {
	/**
	 * @param e
	 * face detection result entry 
	 */
	void detect_end(Entry e);

}
