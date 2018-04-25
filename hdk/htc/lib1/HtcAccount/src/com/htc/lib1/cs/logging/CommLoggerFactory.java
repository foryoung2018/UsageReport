
package com.htc.lib1.cs.logging;

/**
 * Specialized {@link HtcLoggerFactory} for libcomm.
 * 
 * @author samael_wang
 */
public class CommLoggerFactory extends HtcLoggerFactory {
    private static final String TAG = "libcomm";

    public CommLoggerFactory() {
        super(TAG, TAG);
    }

    public CommLoggerFactory(Object obj) {
        super(TAG, TAG, obj);
    }

    public CommLoggerFactory(Class<?> cls) {
        super(TAG, TAG, cls);
    }

}
