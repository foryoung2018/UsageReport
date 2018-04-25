
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
 * Receiver for {@link RegistrationPolicy#ALWAYS_REGISTER} policy to update
 * registration if HTC Account changed.
 * 
 * @author samael_wang@htc.com
 */
public class AlwaysRegisterIdentityBroadcastReceiver extends AbstractIntentServiceBroadcastReceiver {

    public static class HandleBroadcastServiceImpl extends HandleBroadcastService {

        public HandleBroadcastServiceImpl() {
            super(AlwaysRegisterIdentityBroadcastReceiver.class.getSimpleName());
        }

        @Override
        protected void handleBroadcast(Context context, Intent intent) {
            if (RegistrationPolicy.REGISTER_ON_SIGNED_IN.equals(PnsRecords.get(context).getRegistrationPolicy())) {
                mmLogger.debug("AlwaysRegisterIdentityBroadcastReceiver: Ignore intent due to current registration policay is REGISTER_ON_SIGNED_IN.");
                return;
            }

            if (!PnsModel.checkDataUsageAgreement(context)) {
                mmLogger.info("No data usage agreement. Abort operation.");
                return;
            }

            PnsRecords records = PnsRecords.get(context);

            if (HtcAccountBroadcasts.ACTION_ADD_ACCOUNT_COMPLETED.equals(intent.getAction())) {

                if (records.isRegistered()) {
                    try {
                        mmLogger.info("Try update on HTC Account added.");
                        PnsModel.get().update(intent.getAction(), true /* force */);
                    } catch (IllegalStateException e) {
                        mmLogger.error(e);

                        try {
                            // initialize PNS and update again
                            PnsInitializer.Builder builder = new PnsInitializer.Builder(getApplicationContext());
                            builder.enableAlarm(records.getAllowAlarm())
                                    .enableRegistrationService(records.getEnableRegistrationService())
                                    .setRegistrationPolicy(records.getRegistrationPolicy())
                                    .setRetryPolicy(new LibraryRetryPolicy(this))
                                    .build().init();

                            PnsModel.get().update(intent.getAction(), true /* force */);
                        } catch (Exception e2) {
                            mmLogger.error(e2);
                            records.addUpdateEvent("AlwaysRegisterIdentityBroadcastReceiver service call failed", PnsInternalDefs.KEY_CAUSE, false);
                        }
                    }
                } else {
                    try {
                        mmLogger.info("Try register on HTC Account added.");
                        PnsModel.get().register(intent.getAction());
                    } catch (IllegalStateException e) {
                        mmLogger.error(e);

                        try {
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
                            records.addRegistrationEvent("AlwaysRegisterIdentityBroadcastReceiver service call failed", PnsInternalDefs.KEY_CAUSE, false);
                        }
                    }
                }

            } else if (HtcAccountBroadcasts.ACTION_REMOVE_ACCOUNT_COMPLETED.equals(intent
                    .getAction())) {

                if (records.isRegistered()) {
                    try {
                        mmLogger.info("Try update on HTC Account added.");
                        PnsModel.get().update(intent.getAction(), true /* force */);
                    } catch (IllegalStateException e) {
                        mmLogger.error(e);

                        try {
                            // initialize PNS and update again
                            PnsInitializer.Builder builder = new PnsInitializer.Builder(getApplicationContext());
                            builder.enableAlarm(records.getAllowAlarm())
                                    .enableRegistrationService(records.getEnableRegistrationService())
                                    .setRegistrationPolicy(records.getRegistrationPolicy())
                                    .setRetryPolicy(new LibraryRetryPolicy(this))
                                    .build().init();

                            PnsModel.get().update(intent.getAction(), true /* force */);
                        } catch (Exception e2) {
                            mmLogger.error(e2);
                            records.addUpdateEvent("AlwaysRegisterIdentityBroadcastReceiver service call failed", PnsInternalDefs.KEY_CAUSE, false);
                        }
                    }
                } else {
                    try {
                        mmLogger.info("Try register on HTC Account added.");
                        PnsModel.get().register(intent.getAction());
                    } catch (IllegalStateException e) {
                        mmLogger.error(e);

                        try {
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
                            records.addRegistrationEvent("AlwaysRegisterIdentityBroadcastReceiver service call failed", PnsInternalDefs.KEY_CAUSE, false);
                        }                    }
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
