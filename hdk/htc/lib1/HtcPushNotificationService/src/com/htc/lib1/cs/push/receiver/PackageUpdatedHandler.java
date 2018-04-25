
package com.htc.lib1.cs.push.receiver;

import com.htc.lib0.htcdebugflag.HtcWrapHtcDebugFlag;
import com.htc.lib1.cs.logging.HtcLogger;
import com.htc.lib1.cs.push.AlarmHelper;
import com.htc.lib1.cs.push.PnsInternalDefs;
import com.htc.lib1.cs.push.PnsModel;
import com.htc.lib1.cs.push.PnsRecords;
import com.htc.lib1.cs.push.PushLoggerFactory;
import com.htc.lib1.cs.push.utils.SystemPropertiesProxy;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

/**
 * Update registration after FOTA / package update events.
 * 
 * @author samael_wang@htc.com
 */
public class PackageUpdatedHandler implements AbstractIntentServiceBroadcastReceiver.BroadcastHandler {
    /**
     * HTC FOTA completed action. Only appears on HEP.
     */
    private static final String ACTION_HTC_FOTA_COMPLETED = "com.htc.checkin.FOTA_INSTALL_COMPLETE";
    private HtcLogger mLogger = new PushLoggerFactory(this).create();

    @Override
    public void handle(Context context, String action, Uri data, Bundle extras) {
        mLogger.debug(action);

        if (!PnsModel.checkDataUsageAgreement(context)) {
            mLogger.info("No data usage agreement. Abort operation.");
            return;
        }

        if (ACTION_HTC_FOTA_COMPLETED.equals(action)
                || Intent.ACTION_MY_PACKAGE_REPLACED.equals(action)) {

            PnsRecords records = PnsRecords.get(context);

            mLogger.info("The package has been updated and the push provider registration might have been expired. Invalidate PNS registration record now.");
            records.invalidateRegistration();

            if (records.isRegistered()) {
                /*
                // For none-official release SDK bug: send registration event while updating
                // Workaround: always re-register when trying to update
                PnsRecords.get(context).setForceBaiduReRegistered(false);// FIXME: will remove in the next version
                */

                int distributedUpdatePeriod = PnsInternalDefs.DISTRIBUTED_UPDATE_IN_MINUTES;
                if (HtcWrapHtcDebugFlag.Htc_DEBUG_flag) {
                    try {
                        String value = SystemPropertiesProxy.get(context,
                                PnsInternalDefs.KEY_SYSTEM_PROP_DISTRIBUTED_UPDATE_PERIOD);
                        if (!TextUtils.isEmpty(value)) {
                            distributedUpdatePeriod = Integer.parseInt(value);
                        }
                    } catch (NumberFormatException e) {
                        mLogger.error(e);
                    }
                    mLogger.debug("distributed update period from system property = ",
                            distributedUpdatePeriod);
                }
                AlarmHelper.get(context).scheduleUpdateDistributedInPeriod(distributedUpdatePeriod);
            } else {
                int distributedRegisterPeriod = PnsInternalDefs.DISTRIBUTED_REGISTER_PKGUPDATE_IN_MINUTES;
                if (HtcWrapHtcDebugFlag.Htc_DEBUG_flag) {
                    try {
                        String value = SystemPropertiesProxy.get(context,
                                PnsInternalDefs.KEY_SYSTEM_PROP_DISTRIBUTED_REGISTER_PERIOD);
                        if (!TextUtils.isEmpty(value)) {
                            distributedRegisterPeriod = Integer.parseInt(value);
                        }
                    } catch (NumberFormatException e) {
                        mLogger.error(e);
                    }
                    mLogger.debug("distributed register period from system property = ",
                            distributedRegisterPeriod);
                }
                AlarmHelper.get(context).scheduleRegisterDistributedInPeriod(
                        distributedRegisterPeriod);
            }
        }
    }

}
