
package com.htc.lib1.cs.push.retrypolicy;

import com.htc.lib1.cs.ConnectivityHelper;
import com.htc.lib1.cs.logging.HtcLogger;
import com.htc.lib1.cs.pns.RegistrationPolicy;
import com.htc.lib1.cs.push.PnsRecords;
import com.htc.lib1.cs.push.PushLoggerFactory;
import com.htc.lib1.cs.push.service.RegistrationService;
import com.htc.lib1.cs.push.service.UnregistrationService;
import com.htc.lib1.cs.push.service.UpdateRegistrationService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

/**
 * {@link DynamicOneTimeOnConnectedReceiver} registers itself as a dynamic
 * broadcast receiver on application context, unregister itself and retry
 * registration / update registration on network connected.
 * 
 * @author samael_wang@htc.com
 */
/* package */class DynamicOneTimeOnConnectedReceiver extends BroadcastReceiver {
    private HtcLogger mLogger = new PushLoggerFactory(this).create();
    private Context mAppContext;

    /**
     * @param context Context to retrieve application context.
     */
    public DynamicOneTimeOnConnectedReceiver(Context context) {
        mAppContext = context.getApplicationContext();

        // Register on application context to avoid context leak.
        mAppContext.registerReceiver(this, new IntentFilter(
                ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        mLogger.debugS("intent = ", intent);

        /*
         * Disable the receiver and retry registration / update on connected.
         */
        if (ConnectivityHelper.get(context).isConnected()) {
            mAppContext.unregisterReceiver(this);
            mLogger.debug("unregister receiver");

            PnsRecords records = PnsRecords.get(context);
            String action = intent.getAction();

            if (records.isRegistered()) {
                if (records.getRegistrationPolicy().equals(RegistrationPolicy.ALWAYS_REGISTER)
                        || records.getUpdateFailCount() != 0) {
                    // Try update.
                    mLogger.info("Try update on network connected.");
                    UpdateRegistrationService.startService(context, action);
                } else {
                    // Try unregister
                    mLogger.info("Try unregister on network connected.");
                    UnregistrationService.startService(context, action);
                }
            } else if (records.isUnregisterFailed()) {
                // Try unregister
                mLogger.info("Try unregister on network connected.");
                UnregistrationService.startService(mAppContext, action);
            } else {
                // Try register.
                mLogger.info("Try register on network connected.");
                RegistrationService.startService(mAppContext, action);
            }
        }
    }
}
