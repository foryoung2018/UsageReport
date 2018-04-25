
package com.htc.lib1.cs.app;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentProvider;
import android.content.Context;
import android.content.pm.PackageManager;

import com.htc.lib1.cs.logging.CommLoggerFactory;
import com.htc.lib1.cs.logging.HtcLogger;

/**
 * Helper class to change the settings of application components (i.e.
 * {@link Activity}, {@link Service}, {@link ContentProvider} and
 * {@link BroadcastReceiver}).
 * <p>
 * The naming follows a general rule: if a helper class includes only static
 * methods it's named as {@code Utils}, and if it needs an instance to work
 * (including singleton implementation) it's named as {@code Helper}.
 * </p>
 */
public class AppComponentSettingUtils {
    private static HtcLogger sLogger = new CommLoggerFactory(AppComponentSettingUtils.class).create();

    /**
     * Enable given component.
     * 
     * @param context Context to operate on.
     * @param cls The class of the receiver to enable.
     */
    public static void enable(Context context, Class<?> cls) {
        sLogger.verbose("Enabling: ", cls);
        setEnalingState(context, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, cls);
    }

    /**
     * Enable given component.
     * 
     * @param context Context to operate on.
     * @param cls The class of the receiver to enable.
     */
    public static void enable(Context context, String cls) {
        sLogger.verbose("Enabling: ", cls);
        setEnalingState(context, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, cls);
    }

    /**
     * Disable given component.
     * 
     * @param context Context to operate on.
     * @param cls The class of the receiver to disable.
     */
    public static void disable(Context context, Class<?> cls) {
        sLogger.verbose("Disabling: ", cls);
        setEnalingState(context, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, cls);
    }

    /**
     * Disable given component.
     * 
     * @param context Context to operate on.
     * @param cls The class of the receiver to disable.
     */
    public static void disable(Context context, String cls) {
        sLogger.verbose("Disabling: ", cls);
        setEnalingState(context, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, cls);
    }

    /**
     * @param context Context to operate on.
     * @param state {@link PackageManager#COMPONENT_ENABLED_STATE_ENABLED} or
     *            {@link PackageManager#COMPONENT_ENABLED_STATE_DISABLED}
     * @param cls The class of the application component to operate on.
     */
    private static void setEnalingState(Context context, int state, Class<?> cls) {
        Context appContext = context.getApplicationContext();
        PackageManager pm = appContext.getPackageManager();
        ComponentName compName = new ComponentName(appContext, cls);
        pm.setComponentEnabledSetting(compName, state,
                PackageManager.DONT_KILL_APP);
    }

    /**
     * @param context Context to operate on.
     * @param state {@link PackageManager#COMPONENT_ENABLED_STATE_ENABLED} or
     *            {@link PackageManager#COMPONENT_ENABLED_STATE_DISABLED}
     * @param cls The class of the application component to operate on.
     */
    private static void setEnalingState(Context context, int state, String cls) {
        Context appContext = context.getApplicationContext();
        PackageManager pm = appContext.getPackageManager();
        ComponentName compName = new ComponentName(appContext, cls);
        pm.setComponentEnabledSetting(compName, state,
                PackageManager.DONT_KILL_APP);
    }
}
