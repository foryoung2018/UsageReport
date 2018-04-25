
package com.htc.lib1.cs.app;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;

import com.htc.lib0.htcdebugflag.HtcWrapHtcDebugFlag;
import com.htc.lib1.cs.auth.BuildConfig;
import com.htc.lib1.cs.VersionHelper;
import com.htc.lib1.cs.logging.CommLoggerFactory;
import com.htc.lib1.cs.logging.HtcLogger;

/**
 * Simple class to keep logging application components info.
 * 
 * @author samael_wang@htc.com
 */
public class ApplicationMonitor {
    private static final int MAX_COMPONENT_NAME_LENGTH = 64;
    private static final String DELIMITER = "**";
    private static ApplicationMonitor sInstance;
    private HtcLogger mLogger = new CommLoggerFactory(this).create();
    private Context mContext;
    private PackageChangeReceiver mPackageChangeReceiver;

    public static synchronized ApplicationMonitor init(Context context) {
        if (context == null)
            throw new IllegalArgumentException("'context' is null.");

        if (sInstance == null) {
            sInstance = new ApplicationMonitor(context.getApplicationContext());
        }

        return sInstance;
    }

    // Private constructor.
    public ApplicationMonitor(Context context) {
        mContext = context;

        logBasicAppInfo();
        logComponentInfo();

        // Register component changes.
        mPackageChangeReceiver = new PackageChangeReceiver();
        mContext.registerReceiver(mPackageChangeReceiver,
                new IntentFilter(Intent.ACTION_PACKAGE_CHANGED));
    }

    /**
     * Log version info.
     */
    private void logBasicAppInfo() {
        // Log version.
        VersionHelper versionHelper = VersionHelper.get(mContext);
        mLogger.info("PackageName:             ", mContext.getPackageName());
        mLogger.info("ProcessName:             ", ProcessUtils.getProcessName(mContext));
        mLogger.info("VersionName:             ", versionHelper.getVersionName());
        mLogger.info("VersionCode:             ", versionHelper.getVersionCode());
        mLogger.info("ApplicationPath:         ", versionHelper.getApplicationPath());
        mLogger.info("AppFileDataPath:         ", mContext.getFilesDir());
        mLogger.info("Htc_SECURITY_DEBUG_flag: ",
                HtcWrapHtcDebugFlag.Htc_SECURITY_DEBUG_flag);
        mLogger.info("Htc_DEBUG_flag:          ", HtcWrapHtcDebugFlag.Htc_DEBUG_flag);
        mLogger.info("BuildConfig.DEBUG:       ", BuildConfig.DEBUG);
    }

    /**
     * Log components information.
     */
    private void logComponentInfo() {
        if (HtcWrapHtcDebugFlag.Htc_SECURITY_DEBUG_flag
                || HtcWrapHtcDebugFlag.Htc_DEBUG_flag
                || BuildConfig.DEBUG) {
            PackageManager pm = mContext.getPackageManager();
            PackageInfo pinfo;
            try {
                pinfo = pm.getPackageInfo(mContext.getPackageName(), PackageManager.GET_ACTIVITIES
                        | PackageManager.GET_SERVICES | PackageManager.GET_PROVIDERS
                        | PackageManager.GET_RECEIVERS | PackageManager.GET_DISABLED_COMPONENTS);

                // Log activities.
                StringBuilder msgBuilder = new StringBuilder("\n")
                        .append("------------------------------------------------------------------------------------\n")
                        .append("Activities                                                      | Enabled | Exported\n")
                        .append("------------------------------------------------------------------------------------\n");
                if (pinfo.activities != null) {
                    for (ActivityInfo activity : pinfo.activities) {
                        msgBuilder.append(String.format("%-64s| %-7s | %-8s \n",
                                stripComponentName(activity.name),
                                getComponentEnabledSetting(pm, activity.name, activity.enabled),
                                activity.exported ? "Yes" : "No"));
                    }
                }
                msgBuilder
                        .append("------------------------------------------------------------------------------------");
                mLogger.verbose(msgBuilder);

                // Log services.
                msgBuilder = new StringBuilder("\n")
                        .append("------------------------------------------------------------------------------------\n")
                        .append("Services                                                        | Enabled | Exported\n")
                        .append("------------------------------------------------------------------------------------\n");
                if (pinfo.services != null) {
                    for (ServiceInfo service : pinfo.services) {
                        msgBuilder.append(String.format("%-64s| %-7s | %-8s \n",
                                stripComponentName(service.name),
                                getComponentEnabledSetting(pm, service.name, service.enabled),
                                service.exported ? "Yes" : "No"));
                    }
                }
                msgBuilder
                        .append("------------------------------------------------------------------------------------");
                mLogger.verbose(msgBuilder);

                // Log providers.
                msgBuilder = new StringBuilder("\n")
                        .append("------------------------------------------------------------------------------------\n")
                        .append("Providers                                                       | Enabled | Exported\n")
                        .append("------------------------------------------------------------------------------------\n");
                if (pinfo.providers != null) {
                    for (ProviderInfo provider : pinfo.providers) {
                        msgBuilder.append(String.format("%-64s| %-7s | %-8s \n",
                                stripComponentName(provider.name),
                                getComponentEnabledSetting(pm, provider.name, provider.enabled),
                                provider.exported ? "Yes" : "No"));
                    }
                }
                msgBuilder
                        .append("------------------------------------------------------------------------------------");
                mLogger.verbose(msgBuilder);

                // Log receivers.
                msgBuilder = new StringBuilder("\n")
                        .append("------------------------------------------------------------------------------------\n")
                        .append("Receivers                                                       | Enabled | Exported\n")
                        .append("------------------------------------------------------------------------------------\n");
                if (pinfo.receivers != null) {
                    for (ActivityInfo receiver : pinfo.receivers) {
                        msgBuilder.append(String.format("%-64s| %-7s | %-8s \n",
                                stripComponentName(receiver.name),
                                getComponentEnabledSetting(pm, receiver.name, receiver.enabled),
                                receiver.exported ? "Yes" : "No"));
                    }
                }
                msgBuilder
                        .append("------------------------------------------------------------------------------------");
                mLogger.verbose(msgBuilder);

            } catch (NameNotFoundException e) {
                /*
                 * We're looking for our own package. This exception is not
                 * possible.
                 */
                throw new IllegalStateException(e.getMessage(), e);
            }
        }
    }

    /**
     * Strip the name to {@value #MAX_COMPONENT_NAME_LENGTH} characters.
     * 
     * @param name
     * @return
     */
    private String stripComponentName(String name) {
        String packageName = mContext.getPackageName();
        if (name.startsWith(packageName))
            name = name.substring(packageName.length());
        if (name.length() > MAX_COMPONENT_NAME_LENGTH)
            name = DELIMITER + name.substring(name.length()
                    - MAX_COMPONENT_NAME_LENGTH + DELIMITER.length());
        return name;
    }

    /**
     * Get the component enabled setting in readable string.
     */
    private String getComponentEnabledSetting(PackageManager pm, String componentName,
            boolean initialEnabled) {
        switch (pm.getComponentEnabledSetting(
                new ComponentName(mContext.getPackageName(), componentName))) {
            case PackageManager.COMPONENT_ENABLED_STATE_DEFAULT:
                return initialEnabled ? "Yes" : "No";
            case PackageManager.COMPONENT_ENABLED_STATE_ENABLED:
                return "Yes";
            default:
                return "No";
        }
    }

    /**
     * Show component status when changed.
     */
    private class PackageChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            mLogger.info(intent);
            if (Intent.ACTION_PACKAGE_CHANGED.equals(intent.getAction())
                    && mContext.getPackageName().equals(intent.getData().toString())) {
                logComponentInfo();
            }
        }

    }
}
