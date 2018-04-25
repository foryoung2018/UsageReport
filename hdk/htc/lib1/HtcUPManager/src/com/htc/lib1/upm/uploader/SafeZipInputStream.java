package com.htc.lib1.upm.uploader;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipInputStream;
import android.util.Log;

/**
 * {@exthide}
 */
public class SafeZipInputStream extends ZipInputStream {
	
	private static final String TAG = "SafeZipInputStream";
	private int mReturnedZeroCount = 0;
	
	public SafeZipInputStream(InputStream stream) {
		super(stream);
	}

	/**
	 * Issue: [Ace MR, ITS#2954] CodedInputStream.readRawBytes() had an endless loop with corrupt file  
	 *        which probably was created when the process was killed. It caused high CPU usage.
	 * Solution: If ZipInputStream.read() return 0 twice continuously, just translate it into -1. It can 
	 *           help caller to close this stream.
	 */

    public int read(byte[] buffer, int start, int length) throws IOException {
    	int read = super.read(buffer, start, length);
    	
    	if(read == 0) {
    		Log.d(TAG, "[read()] return 0, buffer start = "+start+", length = "+length);
    		if(++mReturnedZeroCount > 1) {
    			mReturnedZeroCount = 0;
    			return -1;
    		}
    	}
    	else if(mReturnedZeroCount != 0){
			mReturnedZeroCount = 0;
    	}    	

    	return read;
    }
}
