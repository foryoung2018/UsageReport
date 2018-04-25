
package com.htc.lib1.cs.auth;

import com.htc.lib1.cs.logging.HtcLoggerFactory;

/**
 * Specialized {@link HtcLoggerFactory} for libauth.
 * 
 * @author samael_wang
 */
public class AuthLoggerFactory extends HtcLoggerFactory {
    private static final String TAG = "libauth";

    public AuthLoggerFactory() {
        super(TAG, TAG);
    }

    public AuthLoggerFactory(Object obj) {
        super(TAG, TAG, obj);
    }

    public AuthLoggerFactory(Class<?> cls) {
        super(TAG, TAG, cls);
    }

}
