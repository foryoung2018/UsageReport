package com.htc.lib1.dm.logging;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;

import com.htc.lib0.htcdebugflag.HtcWrapHtcDebugFlag;

/**
 * A log wrapper of Android logger. It ensures no verbose/debug messages in
 * release build, add proper prefix to each message, and support sensitive log.
 * The message might be truncated by Android logcat.
 * <p>
 * The properties of the logger is configurable. Clients could implement
 * {@link com.htc.lib1.dm.logging.CustomProperties} to override default settings.
 * Please note {@link LoggingProperties} caches the result returns from
 * {@link com.htc.lib1.dm.logging.CustomProperties}. To clear the cache, call
 * {@link LoggingProperties#reset()}.
 * <p>
 * Alternatively, client can use
 * {@link com.htc.lib1.dm.logging.LoggingProperties#addProperties(Properties)} to
 * override the properties. The object has even higher priority than
 * {@link com.htc.lib1.dm.logging.CustomProperties}, but will be clear when invoking
 * {@link LoggingProperties#reset()}.
 * <p>
 * The log format is {class-name}: {objects-passed-in}
 * 
 * @see LoggingProperties
 * @see Properties
 * @see DefaultProperties
 */
public class Logger {
    private String mPrefix;
    private String mHashCode;

    /**
     * Create a logger for a specific object. It uses object class name as the
     * prefix of all log messages.
     * 
     * @param object The {@code Object} to use logger.
     * @return Logger.
     */
    public static Logger getLogger(Object object) {
        String name;

        // Get the name from the object or its superclass.
        if (TextUtils.isEmpty(name = object.getClass().getSimpleName())) {
            name = object.getClass().getSuperclass().getSimpleName();
        }

        // If still not able to get name, use anonymous.
        if (TextUtils.isEmpty(name))
            name = "Anonymous";

        return LoggerManager.getLoggerManager().getLogger(name, object.hashCode());
    }

    /**
     * Create a logger for a specific type. The class name will be used as the
     * prefix of all log messages.
     * 
     * @param type The {@code Type} to use logger.
     * @return Logger.
     */
    public static Logger getLogger(Class<?> type) {
        return LoggerManager.getLoggerManager().getLogger(type.getSimpleName(), 0);
    }

    /**
     * Create a logger for a specific type. The class name will be used as the
     * prefix of all log messages.
     *
     * @param type The {@code Type} to use logger.
     * @return Logger.
     */
    public static Logger getLogger(String customPrefix, Class<?> type) {
        return LoggerManager.getLoggerManager().getLogger(customPrefix+type.getSimpleName(), 0);
    }

    /**
     * Create a logger with custom prefix. This only used in
     * {@link LoggerManager}. Users should call {@link Logger#getLogger(Object)}
     * instead.
     * 
     * @param prefix Custom prefix for log.
     */
    public Logger(String prefix, int hashCode) {
        if (TextUtils.isEmpty(prefix))
            throw new IllegalArgumentException("'prefix' is null or empty.");

        mPrefix = prefix;
        if (hashCode == 0)
            mHashCode = "";
        else
            mHashCode = String.format(" {%h}", hashCode);
    }

    /**
     * Log a very fatal message. It's always loggable.
     * 
     * @param objects Objects to log.
     */
    public void whatTheFuck(Object... objects) {
        String msg = LogBuilder.getBuilder().build(mPrefix, mHashCode, objects);
        if (msg != null)
            Log.wtf(LoggingProperties.get().tag(), msg);
    }

    /**
     * Log an error. It's always loggable.
     * 
     * @param objects Objects to log.
     */
    public void error(Object... objects) {
        String msg = LogBuilder.getBuilder().build(mPrefix, mHashCode, objects);
        if (msg != null)
            Log.e(LoggingProperties.get().tag(), msg);
    }

    /**
     * Log a warning. It's always loggable.
     * 
     * @param objects Objects to log.
     */
    public void warning(Object... objects) {
        String msg = LogBuilder.getBuilder().build(mPrefix, mHashCode, objects);
        if (msg != null)
            Log.w(LoggingProperties.get().tag(), msg);
    }

    /**
     * Log an informative message. Be careful that this won't be disable even on
     * release build, which means it will appear on market devices, so only
     * important messages should be log in this level.
     * 
     * @param objects Objects to log.
     */
    public void informative(Object... objects) {
        if (isLoggable(Log.INFO)) {
            String msg = LogBuilder.getBuilder().build(mPrefix, mHashCode, objects);
            if (msg != null)
                Log.i(LoggingProperties.get().tag(), msg);
        }
    }

    /**
     * Log a debug message in debug build. Won't be log on release build by
     * default unless explicitly set a system property: {@literal 'setprop
     * log.tag.<TAG> <LEVEL>'} where LEVEL is either VERBOSE or DEBUG.
     * 
     * @param objects Objects to log.
     */
    public void debug(Object... objects) {
        if (isLoggable(Log.DEBUG)) {
            String msg = LogBuilder.getBuilder().build(mPrefix, mHashCode, objects);
            if (msg != null)
                Log.d(LoggingProperties.get().tag(), msg);
        }
    }

    /**
     * Log a sensitive debug message. Takes no effect in release build.
     * 
     * @param objects Objects to log.
     */
    public void debugS(Object... objects) {
        if (HtcWrapHtcDebugFlag.Htc_SECURITY_DEBUG_flag) {
            String msg = LogBuilder.getBuilder().build(mPrefix, mHashCode, objects);
            if (msg != null)
                Log.d(LoggingProperties.get().sensitiveTag(), msg);
        }
    }

    /**
     * Add a verbose mark in debug build that only shows when either
     * {@link LoggingProperties#enableMethodLog()} or
     * {@link LoggingProperties#enableFileInfoLog()} is enabled to help
     * diagnosing. It can be put in the beginning of methods to visualize
     * {@link Activity} life-cycle or trace calling flows. It won't be log on
     * release build by default unless explicitly set a system property:
     * {@literal 'setprop log.tag.<TAG> VERBOSE'}
     */
    public void verbose() {
        if (isLoggable(Log.VERBOSE)) {
            String msg = LogBuilder.getBuilder().build(mPrefix, mHashCode);
            if (msg != null)
                Log.v(LoggingProperties.get().tag(), msg);
        }
    }

    /**
     * Log a verbose message in debug build. Won't be log on release build by
     * default unless explicitly set a system property: {@literal 'setprop
     * log.tag.<TAG> VERBOSE'}
     * 
     * @param objects Objects to log.
     */
    public void verbose(Object... objects) {
        if (isLoggable(Log.VERBOSE)) {
            String msg = LogBuilder.getBuilder().build(mPrefix, mHashCode, objects);
            if (msg != null)
                Log.v(LoggingProperties.get().tag(), msg);
        }
    }

    /**
     * Log a sensitive verbose message in debug build. Takes no effect in
     * release build.
     * 
     * @param objects Objects to log.
     */
    public void verboseS(Object... objects) {
        if (HtcWrapHtcDebugFlag.Htc_SECURITY_DEBUG_flag) {
            String msg = LogBuilder.getBuilder().build(mPrefix, mHashCode, objects);
            if (msg != null)
                Log.v(LoggingProperties.get().sensitiveTag(), msg);
        }
    }

    /**
     * Check if loggable. Apply to non-sensitive logs only.
     * 
     * @param level Log level.
     * @return True if loggable.
     */
    @SuppressWarnings("unused")
    private static boolean isLoggable(int level) {
        return HtcWrapHtcDebugFlag.Htc_SECURITY_DEBUG_flag || HtcWrapHtcDebugFlag.Htc_DEBUG_flag
                || Log.isLoggable(LoggingProperties.get().tag(), level);
    }
}
