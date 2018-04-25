
package com.htc.lib1.cs.push;

import com.htc.lib1.cs.logging.HtcLoggerFactory;

/**
 * Specialized {@link HtcLoggerFactory} for libpush.
 * 
 * @author samael_wang
 */
public class PushLoggerFactory extends HtcLoggerFactory {
    private static final String TAG = "libpush";

    public PushLoggerFactory() {
        super(TAG, TAG);
    }

    public PushLoggerFactory(Object obj) {
        super(TAG, TAG, obj);
    }

    public PushLoggerFactory(Class<?> cls) {
        super(TAG, TAG, cls);
    }

}
