
package com.htc.lib1.cs.push.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.htc.lib1.cs.logging.HtcLogger;
import com.htc.lib1.cs.push.AlarmHelper;
import com.htc.lib1.cs.push.PnsInternalDefs;
import com.htc.lib1.cs.push.PnsModel;
import com.htc.lib1.cs.push.PnsRecords;
import com.htc.lib1.cs.push.PushLoggerFactory;
import com.htc.lib1.cs.push.service.RegistrationService;
import com.htc.lib1.cs.push.service.UnregistrationService;
import com.htc.lib1.cs.push.service.UpdateRegistrationService;
import com.htc.lib1.cs.push.utils.SystemPropertiesProxy;

/**
 * {@link SimStateChangeReceiver} Listen SIM state change events and update SIM
 * MCC/MNC info once state is ready.
 * 
 * @author ted_hsu@htc.com
 */
public class SimStateChangeReceiver extends BroadcastReceiver {
    private HtcLogger mLogger = new PushLoggerFactory(this).create();

    private static final String EXTRA_SIM_STATE = "ss";
    private static final String SIM_STATE_LOADED = "LOADED";

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent == null) {
            mLogger.error("intent is null");
            return;
        }

        if (!PnsModel.checkDataUsageAgreement(context)) {
            mLogger.info("No data usage agreement. Abort operation.");
            return;
        }

        String simState = intent.getStringExtra(EXTRA_SIM_STATE);

        mLogger.debug("SIM state: ", simState);
        mLogger.debug("intent: ", intent.getExtras()); // {phoneName=GSM,
                                                       // reason=null,
                                                       // ss=LOADED, slot=0,
                                                       // phone=0,
                                                       // subscription=1}

        if (SIM_STATE_LOADED.equalsIgnoreCase(simState)) {
            
            // Ignore this event if SIM MCC/MNC is empty.
            String detected_mccMncNumeric = SystemPropertiesProxy.get(context, PnsInternalDefs.KEY_SYSTEM_PROP_SIM_MCC_MNC); // 46692,46692
            if (TextUtils.isEmpty(detected_mccMncNumeric)) {
                mLogger.error("SIM MCC/MNC info is null or empty, ignore this event.");
                return;
            }
            
            PnsRecords records = PnsRecords.get(context);

            String current_mccmnc = records.getMccmnc();
            if (!TextUtils.isEmpty(current_mccmnc) && detected_mccMncNumeric.equalsIgnoreCase(current_mccmnc)) {
                mLogger.info("Detected SIM MCC/MNC is the same as current MCC/MNC, return!");
                return;
            }

            if (records.isRegistered()) {
                // Try update.
                mLogger.info("Try update on SIM loaded.");
                UpdateRegistrationService.startService(context, intent.getAction());
            } else if (records.isUnregisterFailed()) {
                // Try unregister
                mLogger.info("Try unregister on SIM loaded.");
                UnregistrationService.startService(context, intent.getAction());
            } else {
                // Try register.
                mLogger.info("Try register on SIM loaded.");
                AlarmHelper.get(context).scheduleRegisterDistributedInPeriod(
                        PnsInternalDefs.DISTRIBUTED_REGISTER_IN_MINUTES);
            }
        }
    }
}
