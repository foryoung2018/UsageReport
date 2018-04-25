
package com.htc.lib1.cs.app;

import java.lang.reflect.Field;

import android.app.Activity;
import android.app.Application.ActivityLifecycleCallbacks;
import android.app.FragmentManager;
import android.os.Bundle;

import com.htc.lib1.cs.logging.CommLoggerFactory;
import com.htc.lib1.cs.logging.HtcLogger;

/**
 * Monitors activity lifecycle and shows its fragment manager.
 */
public class ActivityMonitor implements ActivityLifecycleCallbacks {
    private HtcLogger mLogger = new CommLoggerFactory(this).create();

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        mLogger.debug(activity, " (", activity.getFragmentManager(), ": stateSaved=",
                isFragmentStateSaved(activity.getFragmentManager()), ") created.");
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        mLogger.debug(activity, " (", activity.getFragmentManager(), ": stateSaved=",
                isFragmentStateSaved(activity.getFragmentManager()), ") destroyed.");
    }

    @Override
    public void onActivityPaused(Activity activity) {
        mLogger.debug(activity, " (", activity.getFragmentManager(), ": stateSaved=",
                isFragmentStateSaved(activity.getFragmentManager()), ") paused.");
    }

    @Override
    public void onActivityResumed(Activity activity) {
        mLogger.debug(activity, ": ", activity.getFragmentManager(), ": stateSaved=",
                isFragmentStateSaved(activity.getFragmentManager()), " resumed.");
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        mLogger.debug(activity, " (", activity.getFragmentManager(), ": stateSaved=",
                isFragmentStateSaved(activity.getFragmentManager()), ") saved instance state.");
    }

    @Override
    public void onActivityStarted(Activity activity) {
        mLogger.debug(activity, " (", activity.getFragmentManager(), ": stateSaved=",
                isFragmentStateSaved(activity.getFragmentManager()), ") started");
    }

    @Override
    public void onActivityStopped(Activity activity) {
        mLogger.debug(activity, " (", activity.getFragmentManager(), ": stateSaved=",
                isFragmentStateSaved(activity.getFragmentManager()), ") stopped.");
    }

    private boolean isFragmentStateSaved(FragmentManager fm) {
        try {
            Field mStateSavedField = fm.getClass().getDeclaredField("mStateSaved");
            mStateSavedField.setAccessible(true);
            return mStateSavedField.getBoolean(fm);
        } catch (Exception e) {
            mLogger.error(e);
        }
        return false;
    }

}
