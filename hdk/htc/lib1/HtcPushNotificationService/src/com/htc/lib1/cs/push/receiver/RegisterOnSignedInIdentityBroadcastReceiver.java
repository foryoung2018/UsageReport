
package com.htc.lib1.cs.push.receiver;

import android.content.Context;
import android.content.Intent;

import com.htc.lib1.cs.account.HtcAccountBroadcasts;
import com.htc.lib1.cs.pns.PnsInitializer;
import com.htc.lib1.cs.pns.RegistrationPolicy;
import com.htc.lib1.cs.push.PnsInternalDefs;
import com.htc.lib1.cs.push.PnsModel;
import com.htc.lib1.cs.push.PnsRecords;
import com.htc.lib1.cs.push.retrypolicy.LibraryRetryPolicy;

/**
 * Receiver for {@link RegistrationPolicy#REGISTER_ON_SIGNED_IN} policy to
 * update registration if HTC Account changed.
 *
 * @author samael_wang@htc.com
 */
public class RegisterOnSignedInIdentityBroadcastReceiver extends
        AbstractIntentServiceBroadcastReceiver {

    public static class HandleBroadcastServiceImpl extends HandleBroadcastService {

        public HandleBroadcastServiceImpl() {
            super(RegisterOnSignedInIdentityBroadcastReceiver.class.getSimpleName());
        }

        @Override
        protected void handleBroadcast(Context context, final Intent intent) {
            if (RegistrationPolicy.ALWAYS_REGISTER.equals(PnsRecords.get(context).getRegistrationPolicy())) {
                mmLogger.debug("RegisterOnSignedInIdentityBroadcastReceiver: Ignore intent due to current registration policay is ALWAYS_REGISTER.");
                return;
            }

            if (!PnsModel.checkDataUsageAgreement(context)) {
                mmLogger.info("No data usage agreement. Abort operation.");
                return;
            }

            if (HtcAccountBroadcasts.ACTION_ADD_ACCOUNT_COMPLETED.equals(intent.getAction())) {
                try {
                    mmLogger.info("Try register on HTC Account added.");
                    PnsModel.get().register(intent.getAction());
                } catch (IllegalStateException e) {
                    mmLogger.error(e);

                    try {
                        PnsRecords records = PnsRecords.get(getApplicationContext());

                        // initialize PNS and register again
                        PnsInitializer.Builder builder = new PnsInitializer.Builder(getApplicationContext());
                        builder.enableAlarm(records.getAllowAlarm())
                                .enableRegistrationService(records.getEnableRegistrationService())
                                .setRegistrationPolicy(records.getRegistrationPolicy())
                                .setRetryPolicy(new LibraryRetryPolicy(this))
                                .build().init();

                        PnsModel.get().register(intent.getAction());
                    } catch (Exception e2) {
                        mmLogger.error(e2);
                        PnsRecords.get(getApplicationContext()).addRegistrationEvent("RegisterOnSignedInIdentityBroadcastReceiver call failed", PnsInternalDefs.KEY_CAUSE, false);
                    }
                }
            } else if (HtcAccountBroadcasts.ACTION_REMOVE_ACCOUNT_COMPLETED.equals(intent.getAction())) {
                try {
                    mmLogger.info("Try unregister on HTC Account removed.");
                    PnsModel.get().unregister(intent.getAction());
                } catch (IllegalStateException e) {
                    mmLogger.error(e);

                    try {
                        PnsRecords records = PnsRecords.get(getApplicationContext());

                        // initialize PNS and unregister again
                        PnsInitializer.Builder builder = new PnsInitializer.Builder(getApplicationContext());
                        builder.enableAlarm(records.getAllowAlarm())
                                .enableRegistrationService(records.getEnableRegistrationService())
                                .setRegistrationPolicy(records.getRegistrationPolicy())
                                .setRetryPolicy(new LibraryRetryPolicy(this))
                                .build().init();

                        PnsModel.get().unregister(intent.getAction());
                    } catch (Exception e2) {
                        mmLogger.error(e2);
                        PnsRecords.get(getApplicationContext()).addUnregistrationEvent("RegisterOnSignedInIdentityBroadcastReceiver call failed", PnsInternalDefs.KEY_CAUSE, false);
                    }

                }
            } else if (HtcAccountBroadcasts.ACTION_AUTH_TOKEN_RENEWED.equals(intent.getAction())) {
                mmLogger.info("HTC Account authtoken has been renewed.");
            }
        }

    }

    @Override
    protected Class<? extends HandleBroadcastService> getServiceClass() {
        return HandleBroadcastServiceImpl.class;
    }
}
