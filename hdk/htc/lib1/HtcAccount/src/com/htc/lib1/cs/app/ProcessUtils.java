
package com.htc.lib1.cs.app;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.os.Looper;
import android.text.TextUtils;

import com.htc.lib1.cs.logging.CommLoggerFactory;
import com.htc.lib1.cs.logging.HtcLogger;

/**
 * Utilities related to current running process.
 *
 * @author samael_wang@htc.com
 */
public class ProcessUtils {
    private static HtcLogger sLogger = new CommLoggerFactory(ProcessUtils.class).create();

    /**
     * Get current running process name.
     *
     * @return Process name.
     */
    public static String getProcessName(Context context) {
        String processName = "Unknown process name";

        if (context == null) {
            sLogger.error("Context is null!");
            return processName;
        }

        int pid = android.os.Process.myPid();
        ActivityManager manager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);

        // Because manager.getRunningAppProcesses() may return null object and cause NullPointerException, add null check to prevent that happened
        if (manager != null && manager.getRunningAppProcesses() != null) {
            for (RunningAppProcessInfo processInfo : manager.getRunningAppProcesses()) {
                if (processInfo.pid == pid) {
                    processName = processInfo.processName;
                    break;
                }
            }
        }

        return processName;
    }

    /**
     * Get the subprocess name, which equals the name you put in
     * {@code android:process} attribute in AndroidManifest.xml.
     *
     * @return The sub-process name or {@code null} if not available.
     */
    public static String getSubProcessName(Context context) {
        if (context == null)
            throw new IllegalArgumentException("'context' is null.");

        try {
            String processName = getProcessName(context);
            int start = processName.indexOf(':');
            if (start >= 0 && processName.length() > start + 1) {
                return processName.substring(start);
            } else {
                return null;
            }
        } catch (IllegalStateException e) {
            /*
             * The package might be updating. Ignore and treat it as no
             * sub-process name available.
             */
            sLogger.warning(e);
            return null;
        }
    }

    /**
     * Check if current process is the main process but not a sub-process.
     *
     * @return {@code true} if it is.
     */
    public static boolean isMainProcess(Context context) {
        return TextUtils.isEmpty(getSubProcessName(context));
    }

    /**
     * Throws a runtime exception if the method is running on main thread.
     */
    public static void ensureNotOnMainThread() {
        final Looper looper = Looper.myLooper();
        if (looper != null && looper == Looper.getMainLooper()) {
            throw new IllegalStateException(
                    "calling this from your main thread can lead to deadlock and/or ANRs.");
        }
    }
}
