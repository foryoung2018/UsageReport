
package com.htc.lib1.cs.auth.web;

import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.IBinder;
import android.os.Process;
import android.util.Log;

import com.htc.lib0.htcdebugflag.HtcWrapHtcDebugFlag;
import com.htc.lib1.cs.auth.AuthLoggerFactory;
import com.htc.lib1.cs.auth.BuildConfig;
import com.htc.lib1.cs.logging.HtcLogger;

/**
 * The basic entry point of Identity Client. It's used to sign-in/sign-up HTC
 * Accounts.
 */
public class IdentityService extends Service {
    private HtcLogger mLogger = new AuthLoggerFactory(this).create();
    private IdentityAuthenticator mAuthenticator;

    @Override
    public IBinder onBind(Intent intent) {
        mLogger.verboseS(intent);
        return mAuthenticator.getIBinder();
    }

    @Override
    public void onCreate() {
        mLogger.verbose();
        super.onCreate();

        mAuthenticator = new IdentityAuthenticator(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mLogger.verboseS(intent);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        mLogger.verbose();
        super.onDestroy();

        mAuthenticator = null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mLogger.verboseS(intent);
        return super.onUnbind(intent);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        mLogger.verbose();
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onRebind(Intent intent) {
        mLogger.verboseS(intent);
        super.onRebind(intent);
    }

    /**
     * An exception handler that simply log the exception and kill the process.
     */
    @SuppressWarnings("unused")
    private class UncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
        private Thread.UncaughtExceptionHandler mmSystemDefaultUncaughtExceptionHandler;

        public UncaughtExceptionHandler(
                Thread.UncaughtExceptionHandler defaultUncaughtExceptionHandler) {
            mmSystemDefaultUncaughtExceptionHandler = defaultUncaughtExceptionHandler;
        }

        @Override
        public void uncaughtException(Thread thread, Throwable ex) {
            // Find the root cause.
            Throwable rootCause = ex;
            while (ex.getCause() != null && rootCause != ex.getCause()) {
                rootCause = ex.getCause();
            }

            // Log the exception and root cause in very fatal level.
            mLogger.fatal("Uncaught exception: ", Log.getStackTraceString(ex));
            if (rootCause != ex)
                mLogger.fatal("Root cause: ", Log.getStackTraceString(rootCause));

            if ((HtcWrapHtcDebugFlag.Htc_SECURITY_DEBUG_flag
                    || HtcWrapHtcDebugFlag.Htc_DEBUG_flag
                    || BuildConfig.DEBUG)
                    && mmSystemDefaultUncaughtExceptionHandler != null) {
                mmSystemDefaultUncaughtExceptionHandler.uncaughtException(thread, ex);
            } else {
                // Try everything to kill the process.
                Process.killProcess(Process.myPid());
                System.exit(10);
            }
        }
    }
}
