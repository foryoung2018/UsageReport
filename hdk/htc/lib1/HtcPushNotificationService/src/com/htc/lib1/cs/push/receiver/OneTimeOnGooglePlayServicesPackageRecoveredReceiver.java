
package com.htc.lib1.cs.push.receiver;

import android.content.Context;
import android.content.Intent;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.htc.lib1.cs.push.PnsModel;
import com.htc.lib1.cs.push.google.GooglePlayServicesAvailabilityUtils;
import com.htc.lib1.cs.push.google.GooglePlayServicesAvailabilityUtils.Availability;
import com.htc.lib1.cs.push.utils.AppComponentSettingUtils;

/**
 * One-time receiver to try register and disable itself once Google Play
 * Services package is recovered.
 * 
 * @author samael_wang@htc.com
 */
public class OneTimeOnGooglePlayServicesPackageRecoveredReceiver extends
        AbstractIntentServiceBroadcastReceiver {

    public static class HandleBroadcastServiceImpl extends HandleBroadcastService {

        public HandleBroadcastServiceImpl() {
            super(OneTimeOnGooglePlayServicesPackageRecoveredReceiver.class.getSimpleName());
        }

        @Override
        protected void handleBroadcast(Context context, Intent intent) {
            if (Intent.ACTION_PACKAGE_ADDED.equals(intent.getAction())
                    || Intent.ACTION_PACKAGE_REPLACED.equals(intent.getAction())
                    || Intent.ACTION_PACKAGE_CHANGED.equals(intent.getAction())) {
                String packageName = intent.getData() != null ? intent.getData()
                        .getSchemeSpecificPart() : null;
                mmLogger.debug(packageName, " is installed or changed.");

                if (GooglePlayServicesUtil.GOOGLE_PLAY_SERVICES_PACKAGE.equals(packageName)) {
                    int status = GooglePlayServicesAvailabilityUtils.isGooglePlayServicesAvailable(context);
                    Availability availability = GooglePlayServicesAvailabilityUtils
                            .isAvaiable(status);

                    if (availability == Availability.AVAILABLE) {
                        // Disable itself.
                        AppComponentSettingUtils.disable(context,
                                OneTimeOnGooglePlayServicesPackageRecoveredReceiver.class);

                        // Try registration.
                        mmLogger.info("Try register after Google Play Services package recovered.");
                        PnsModel.get().register(intent.getAction());
                    } else if (availability == Availability.RECOVERABLE) {
                        mmLogger.info("Trigger error notification to recover Google Play Services");
                        GooglePlayServicesUtil.showErrorNotification(status, this);
                    } else if (availability == Availability.UNRECOVERABLE) {
                        mmLogger.error("Google Play Services is not available and not recoverable.");
                    }
                }
            }
        }
    }

    @Override
    protected Class<? extends HandleBroadcastService> getServiceClass() {
        return HandleBroadcastServiceImpl.class;
    }
}
