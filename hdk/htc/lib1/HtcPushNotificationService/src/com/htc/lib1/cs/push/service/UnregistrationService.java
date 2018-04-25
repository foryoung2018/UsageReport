
package com.htc.lib1.cs.push.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.htc.lib1.cs.logging.HtcLogger;
import com.htc.lib1.cs.pns.PnsInitializer;
import com.htc.lib1.cs.push.PnsInternalDefs;
import com.htc.lib1.cs.push.PnsModel;
import com.htc.lib1.cs.push.PnsRecords;
import com.htc.lib1.cs.push.PushLoggerFactory;
import com.htc.lib1.cs.push.retrypolicy.LibraryRetryPolicy;

/**
 * Unregisters GCM in a background thread.
 */
public class UnregistrationService extends IntentService {
    private HtcLogger mLogger = new PushLoggerFactory(this).create();

    /**
     * Start {@link UnregistrationService}.
     *
     * @param context Context used to start the service.
     * @param cause   Reason to start the service. Must not be {@code null} or
     *                empty.
     */
    public static void startService(Context context, String cause) {
        if (context == null)
            throw new IllegalArgumentException("'context' is null.");
        if (TextUtils.isEmpty(cause))
            throw new IllegalArgumentException("'cause' is null or empty.");

        Intent intent = new Intent(context, UnregistrationService.class);
        intent.putExtra(PnsInternalDefs.KEY_CAUSE, cause);
        if (context.startService(intent) == null) {
            throw new IllegalStateException(
                    "Unable to start service "
                            + intent.toString()
                            + ". Have you forgot to declared it in AndroidManifest.xml, "
                            + "or set 'manifestmerger.enabled=true' "
                            + "in your project.properties?");
        }
    }

    public UnregistrationService() {
        super(UnregistrationService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        mLogger.verbose(intent);

        if (intent == null) {
            // handle service restart case
            mLogger.error("Intent is null, return!");
            return;
        }

        if (!PnsModel.checkDataUsageAgreement(this)) {
            mLogger.info("No data usage agreement. Abort operation.");
            return;
        }

        String cause = TextUtils.isEmpty(intent.getAction()) ?
                intent.getStringExtra(PnsInternalDefs.KEY_CAUSE) : intent.getAction();

        if (!TextUtils.isEmpty(cause)) {
            try {
                PnsModel.get().unregister(cause);
            } catch (IllegalStateException e) {
                mLogger.error(e);
                PnsRecords records = PnsRecords.get(getApplicationContext());
                records.addUnregistrationEvent("Unregister service call failed", e.getMessage(), false);

                try {

                    // initialize PNS and unregister again
                    PnsInitializer.Builder builder = new PnsInitializer.Builder(getApplicationContext());
                    builder.enableAlarm(records.getAllowAlarm())
                            .enableRegistrationService(records.getEnableRegistrationService())
                            .setRegistrationPolicy(records.getRegistrationPolicy())
                            .setRetryPolicy(new LibraryRetryPolicy(this))
                            .build().init();

                    PnsModel.get().unregister(cause);
                } catch (Exception e2) {
                    mLogger.error(e2);
                    records.addUnregistrationEvent("Unregister service call failed", e2.getMessage(), false);
                }
            }
        } else {
            mLogger.error("Neither '" + PnsInternalDefs.KEY_CAUSE + "' nor action is given.");
        }
    }
}
