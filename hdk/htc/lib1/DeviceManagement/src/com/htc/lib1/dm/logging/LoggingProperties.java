package com.htc.lib1.dm.logging;

import android.util.Log;

import com.htc.lib0.htcdebugflag.HtcWrapHtcDebugFlag;
/**
 * Logging properties reader.
 */
public class LoggingProperties {
    public static final int MAX_TAG_LENGTH = 23;
    private static final String TAG = "CSLogger";
    private static LoggingProperties sInstance;
    private Properties mProperties = new CustomProperties();

    /**
     * Get the instance of {@link LoggingProperties}.
     * 
     * @return Instance.
     */
    public static synchronized LoggingProperties get() {
        /* Create an instance if not created yet. */
        if (sInstance == null) {
            // Initialize properties.
            sInstance = new LoggingProperties();
            if (Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "Debug flag: " + HtcWrapHtcDebugFlag.Htc_DEBUG_flag);
                Log.d(TAG, "Security flag: " + HtcWrapHtcDebugFlag.Htc_SECURITY_DEBUG_flag);
            }
        }

        return sInstance;
    }

    /**
     * Remove the instance to reset all properties.
     */
    public static synchronized void reset() {
        if (Log.isLoggable(TAG, Log.INFO)) {
            Log.i(TAG, "Reset properties.");
        }
        sInstance = null;
        LogBuilder.reset();
    }

    /**
     * Construct an {@link LoggingProperties} instance.
     * 
     * @param facilities Wrappers to used for different facilities. The order
     *            decides the priority. {@link LoggingProperties} queries all
     *            facility wrappers in order, and returns the first valid value
     *            returned from facility wrapper.
     *            <p>
     *            The list must not be {@code null}.
     */
    private LoggingProperties() {
    	// Make the constructor being private.
    }

    /**
     * Get logger tag.
     * 
     * @return Tag to use.
     */
    public String tag() {
        return mProperties.tag();
    }

    /**
     * Get logger tag for sensitive logs.
     * 
     * @return Tag to use.
     */
    public String sensitiveTag() {
        return mProperties.senstiveTag();
    }

    /**
     * Check if method name should be logged.
     * 
     * @return True if should be logged.
     */
    public boolean enableMethodLog() {
        return mProperties.enableMethodLog();
    }

    /**
     * Check if file info should be logged.
     * 
     * @return True if should be logged.
     */
    public boolean enableFileInfoLog() {
        return mProperties.enableFileInfoLog();
    }

    /**
     * Check if {@link ThrowableFormatter} should be enabled.
     * 
     * @return True if should be enabled.
     */
    public boolean enableThrowableFormatter() {
        return mProperties.enableThrowableFormatter();
    }

    /**
     * Check if {@link BundleFormatter} should be enabled.
     * 
     * @return True if should be enabled.
     */
    public boolean enableBundleFormatter() {
        return mProperties.enableBundleFormatter();
    }
}