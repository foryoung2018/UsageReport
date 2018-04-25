
package com.htc.lib1.cs.push.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.htc.lib1.cs.logging.HtcLogger;
import com.htc.lib1.cs.pns.PnsInitializer;
import com.htc.lib1.cs.push.AlarmHelper;
import com.htc.lib1.cs.push.PnsInternalDefs;
import com.htc.lib1.cs.push.PnsModel;
import com.htc.lib1.cs.push.PnsRecords;
import com.htc.lib1.cs.push.PushLoggerFactory;
import com.htc.lib1.cs.push.retrypolicy.LibraryRetryPolicy;

/**
 * Registers push notification service.
 */
public class RegistrationService extends IntentService {
    private HtcLogger mLogger = new PushLoggerFactory(this).create();

    public static final String EXTRA_ALARM_TIME = "com.htc.lib1.cs.push.service.EXTRA_ALARM_TIME";
    public static final String EXTRA_ALARM_PERIOD_IN_MINUTES =
            "com.htc.lib1.cs.push.service.EXTRA_ALARM_PERIOD_IN_MINUTES";

    private static final long MANY_DAYS = 14 * 24 * 60 * 60 * 1000;

    /**
     * Start {@link RegistrationService}.
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

        Intent intent = new Intent(context, RegistrationService.class);
        intent.putExtra(PnsInternalDefs.KEY_CAUSE, cause);
        if (context.startService(intent) == null) {
            throw new IllegalStateException("Unable to start service " + intent.toString()
                    + ". Have you forgot to declared it in AndroidManifest.xml, "
                    + "or set 'manifestmerger.enabled=true' "
                    + "in your project.properties?");
        }
    }

    public RegistrationService() {
        super(RegistrationService.class.getName());
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

        String cause = intent.hasExtra(PnsInternalDefs.KEY_CAUSE) ?
                intent.getStringExtra(PnsInternalDefs.KEY_CAUSE) : intent.getAction();

        if (!TextUtils.isEmpty(cause)) {
            long alarmTime = intent.getLongExtra(EXTRA_ALARM_TIME, 0);
            int periodInMinutes = intent.getIntExtra(EXTRA_ALARM_PERIOD_IN_MINUTES, 0);
            if (alarmTime != 0 && periodInMinutes != 0) {
                long currentTime = System.currentTimeMillis();
                if (currentTime > alarmTime + MANY_DAYS) {
                    // The alarm has expired more than one week. This could be caused by:
                    // 1. User changed system time manually for a unimaginable usage, or
                    // 2. The case we're interesting: the alarm was set before system time
                    //     being fixed by network/user at the first booting stage.
                    // Since registration only needs to be executed once, ignore case #1.
                    //
                    // For case #2, reset alarm time with the same period from current time.
                    mLogger.info("Rescheduling for system time change");
                    PnsRecords.get(this).setNextRegistration(0);
                    AlarmHelper.get(this).scheduleRegisterDistributedInPeriod(periodInMinutes);
                    return;
                }
            }

            try {
                PnsModel.get().register(cause);
            } catch (IllegalStateException e) {
                mLogger.error(e);

                PnsRecords records = PnsRecords.get(getApplicationContext());
                records.addRegistrationEvent("Register service call failed", e.getMessage(), false);

                try {
                    // initialize PNS and register again
                    PnsInitializer.Builder builder = new PnsInitializer.Builder(getApplicationContext());
                    builder.enableAlarm(records.getAllowAlarm())
                            .enableRegistrationService(records.getEnableRegistrationService())
                            .setRegistrationPolicy(records.getRegistrationPolicy())
                            .setRetryPolicy(new LibraryRetryPolicy(this))
                            .build().init();

                    PnsModel.get().register(cause);
                } catch (Exception e2) {
                    mLogger.error(e2);
                    records.addRegistrationEvent("Register service call failed", e2.getMessage(), false);
                }
            }
        } else {
            mLogger.error("Neither '" + PnsInternalDefs.KEY_CAUSE + "' nor action is given.");
        }
    }
}
