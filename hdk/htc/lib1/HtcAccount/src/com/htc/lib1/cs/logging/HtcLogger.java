
package com.htc.lib1.cs.logging;

import org.andlog.Builder;
import org.andlog.Logger;

import android.text.TextUtils;
import android.util.Log;

import com.htc.lib0.htcdebugflag.HtcWrapHtcDebugFlag;

/**
 * HTC customized extension to {@link Logger}.
 * 
 * @author samael_wang
 */
public class HtcLogger extends Logger {
    private String mSTag;

    /**
     * @param tag Tag for log messages.
     * @param sTag Tag for sensitive log messages.
     * @param prefix Prefix to append on each log message.
     * @param builder Builder to use.
     */
    public HtcLogger(String tag, String sTag, String prefix, Builder builder) {
        super(tag, prefix, builder);
        if (TextUtils.isEmpty(sTag))
            throw new IllegalArgumentException("'sTag' is null or empty.");

        mSTag = sTag;
    }

    @Override
    public void verbose(Object... objs) {
        if (isLoggable(Log.VERBOSE)) {
            try {
                String msg = mBuilder.build(mPrefix, objs);
                if (!TextUtils.isEmpty(msg))
                    Log.v(mTag, msg);
            } catch (Exception e) {
                Log.w(mTag, "Exception when logging. " + e.getMessage());
            }
        }
    }

    @Override
    public void verbose() {
        if (isLoggable(Log.VERBOSE)) {
            try {
                String msg = mBuilder.build(mPrefix);
                if (!TextUtils.isEmpty(msg))
                    Log.v(mTag, msg);
            } catch (Exception e) {
                Log.w(mTag, "Exception when logging. " + e.getMessage());
            }
        }
    }

    /**
     * Log a {@link Log#VERBOSE} level sensitive message.
     * 
     * @param objs {@link Object}s to log.
     * @see Log#v(String, String)
     */
    public void verboseS(Object... objs) {
        if (HtcWrapHtcDebugFlag.Htc_SECURITY_DEBUG_flag
                || com.htc.lib1.cs.auth.BuildConfig.DEBUG) {
            try {
                String msg = mBuilder.build(mPrefix, objs);
                if (!TextUtils.isEmpty(msg))
                    Log.v(mSTag, msg);
            } catch (Exception e) {
                Log.w(mTag, "Exception when logging. " + e.getMessage());
            }
        }
    }

    @Override
    public void debug(Object... objs) {
        if (isLoggable(Log.DEBUG)) {
            try {
                String msg = mBuilder.build(mPrefix, objs);
                if (!TextUtils.isEmpty(msg))
                    Log.d(mTag, msg);
            } catch (Exception e) {
                Log.w(mTag, "Exception when logging. " + e.getMessage());
            }
        }
    }

    @Override
    public void debug() {
        if (isLoggable(Log.DEBUG)) {
            try {
                String msg = mBuilder.build(mPrefix);
                if (!TextUtils.isEmpty(msg))
                    Log.d(mTag, msg);
            } catch (Exception e) {
                Log.w(mTag, "Exception when logging. " + e.getMessage());
            }
        }
    }

    /**
     * Log a {@link Log#DEBUG} level sensitive message.
     * 
     * @param objs {@link Object}s to log.
     * @see Log#d(String, String)
     */
    public void debugS(Object... objs) {
        if (HtcWrapHtcDebugFlag.Htc_SECURITY_DEBUG_flag
                || com.htc.lib1.cs.auth.BuildConfig.DEBUG) {
            try {
                String msg = mBuilder.build(mPrefix, objs);
                if (!TextUtils.isEmpty(msg))
                    Log.d(mSTag, msg);
            } catch (Exception e) {
                Log.w(mTag, "Exception when logging. " + e.getMessage());
            }
        }
    }

    @Override
    public void info(Object... objs) {
        if (isLoggable(Log.INFO)) {
            try {
                String msg = mBuilder.build(mPrefix, objs);
                if (!TextUtils.isEmpty(msg))
                    Log.i(mTag, msg);
            } catch (Exception e) {
                Log.w(mTag, "Exception when logging. " + e.getMessage());
            }
        }
    }

    /**
     * Log a {@link Log#INFO} level sensitive message.
     * 
     * @param objs {@link Object}s to log.
     * @see Log#i(String, String)
     */
    public void infoS(Object... objs) {
        if (HtcWrapHtcDebugFlag.Htc_SECURITY_DEBUG_flag
                || com.htc.lib1.cs.auth.BuildConfig.DEBUG) {
            try {
                String msg = mBuilder.build(mPrefix, objs);
                if (!TextUtils.isEmpty(msg))
                    Log.i(mSTag, msg);
            } catch (Exception e) {
                Log.w(mTag, "Exception when logging. " + e.getMessage());
            }
        }
    }

    @Override
    public void warning(Object... objs) {
        if (isLoggable(Log.WARN)) {
            try {
                String msg = mBuilder.build(mPrefix, objs);
                if (!TextUtils.isEmpty(msg))
                    Log.w(mTag, msg);
            } catch (Exception e) {
                Log.w(mTag, "Exception when logging. " + e.getMessage());
            }
        }
    }

    /**
     * Log a {@link Log#WARN} level sensitive message.
     * 
     * @param objs {@link Object}s to log.
     * @see Log#w(String, String)
     */
    public void warningS(Object... objs) {
        if (HtcWrapHtcDebugFlag.Htc_SECURITY_DEBUG_flag
                || com.htc.lib1.cs.auth.BuildConfig.DEBUG) {
            try {
                String msg = mBuilder.build(mPrefix, objs);
                if (!TextUtils.isEmpty(msg))
                    Log.w(mSTag, msg);
            } catch (Exception e) {
                Log.w(mTag, "Exception when logging. " + e.getMessage());
            }
        }
    }

    @Override
    public void error(Object... objs) {
        if (isLoggable(Log.ERROR)) {
            try {
                String msg = mBuilder.build(mPrefix, objs);
                if (!TextUtils.isEmpty(msg))
                    Log.e(mTag, msg);
            } catch (Exception e) {
                Log.w(mTag, "Exception when logging. " + e.getMessage());
            }
        }
    }

    /**
     * Log a {@link Log#ERROR} level sensitive message.
     * 
     * @param objs {@link Object}s to log.
     * @see Log#e(String, String)
     */
    public void errorS(Object... objs) {
        if (HtcWrapHtcDebugFlag.Htc_SECURITY_DEBUG_flag
                || com.htc.lib1.cs.auth.BuildConfig.DEBUG) {
            try {
                String msg = mBuilder.build(mPrefix, objs);
                if (!TextUtils.isEmpty(msg))
                    Log.e(mSTag, msg);
            } catch (Exception e) {
                Log.w(mTag, "Exception when logging. " + e.getMessage());
            }
        }
    }

    @Override
    public void fatal(Object... objs) {
        if (isLoggable(Log.ASSERT)) {
            try {
                String msg = mBuilder.build(mPrefix, objs);
                if (!TextUtils.isEmpty(msg))
                    Log.wtf(mTag, msg);
            } catch (Exception e) {
                Log.w(mTag, "Exception when logging. " + e.getMessage());
            }
        }
    }

    /**
     * Check if loggable. Apply to non-sensitive logs only.
     * 
     * @param level Log level.
     * @return True if loggable.
     */
    @SuppressWarnings("unused")
    private boolean isLoggable(int level) {
        return com.htc.lib1.cs.auth.BuildConfig.DEBUG
                || HtcWrapHtcDebugFlag.Htc_SECURITY_DEBUG_flag
                || HtcWrapHtcDebugFlag.Htc_DEBUG_flag
                || Log.isLoggable(mTag, level);
    }
}
