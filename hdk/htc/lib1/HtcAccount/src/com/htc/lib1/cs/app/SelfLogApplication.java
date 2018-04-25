
package com.htc.lib1.cs.app;

import android.app.Application;
import android.content.ComponentCallbacks2;

import com.htc.lib1.cs.logging.CommLoggerFactory;
import com.htc.lib1.cs.logging.HtcLogger;

/**
 * A base application that logs app version and component status.
 * 
 * @author samael_wang@htc.com
 */
public class SelfLogApplication extends Application {
    protected HtcLogger mLogger = new CommLoggerFactory(this).create();

    @Override
    public void onCreate() {
        super.onCreate();

        // Init application monitor.
        ApplicationMonitor.init(this);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mLogger.warning("System warns low memory.");
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);

        switch (level) {
            case ComponentCallbacks2.TRIM_MEMORY_BACKGROUND:
                mLogger.verbose("TRIM_MEMORY_BACKGROUND");
                break;
            case ComponentCallbacks2.TRIM_MEMORY_COMPLETE:
                mLogger.verbose("TRIM_MEMORY_COMPLETE");
                break;
            case ComponentCallbacks2.TRIM_MEMORY_MODERATE:
                mLogger.verbose("TRIM_MEMORY_MODERATE");
                break;
            case ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL:
                mLogger.verbose("TRIM_MEMORY_RUNNING_CRITICAL");
                break;
            case ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW:
                mLogger.verbose("TRIM_MEMORY_RUNNING_LOW");
                break;
            case ComponentCallbacks2.TRIM_MEMORY_RUNNING_MODERATE:
                mLogger.verbose("TRIM_MEMORY_RUNNING_MODERATE");
                break;
            case ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN:
                mLogger.verbose("TRIM_MEMORY_UI_HIDDEN");
                break;
            default:
                mLogger.verbose("Unknown level: ", level);

        }
    }
}
