package com.htc.lib1.dm.logging;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import android.text.TextUtils;

/**
 * LoggerManager maintains a list of loggers.
 */
class LoggerManager {
    private static LoggerManager sInstance = new LoggerManager();
    // Use WeakReference to avoid memory leak.
    private static Map<String, WeakReference<Logger>> sLoggers = new HashMap<String, WeakReference<Logger>>();

    /**
     * Get the instance.
     * 
     * @return LoggerManager.
     */
    public static LoggerManager getLoggerManager() {
        return sInstance;
    }

    /**
     * Get a logger with given name. Create a new one if it doesn't exist.
     * 
     * @param prefix Log prefix. Usually using object name.
     * @param hashCode Object hash code. Used to distinguish different loggers
     *            with the same prefix.
     * @return Logger.
     */
    public synchronized Logger getLogger(String prefix, int hashCode) {
        // Check argument.
        if (TextUtils.isEmpty(prefix))
            throw new IllegalArgumentException("'prefix' is null or empty.");

        // Get logger.
        String key = prefix + hashCode;
        WeakReference<Logger> loggerRef = sLoggers.get(key);
        Logger logger;

        // Create a logger if the logger with the given name is not created yet
        // or it has been cleared by garbage collector.
        if (loggerRef == null || (logger = loggerRef.get()) == null) {
            logger = new Logger(prefix, hashCode);
            loggerRef = new WeakReference<Logger>(logger);
            sLoggers.put(key, loggerRef);
        }

        return logger;
    }
}
