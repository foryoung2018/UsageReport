
package com.htc.lib1.cs.push;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateUtils;

import com.htc.lib1.cs.logging.HtcLogger;
import com.htc.lib1.cs.push.service.RegistrationService;
import com.htc.lib1.cs.push.service.UnregistrationService;
import com.htc.lib1.cs.push.service.UpdateRegistrationService;

import java.util.Date;
import java.util.Random;

/**
 * Helper class of Alarm.
 * <p>
 * The naming follows a general rule: if a helper class includes only static
 * methods it's named as {@code Utils}, and if it needs an instance to work
 * (including singleton implementation) it's named as {@code Helper}.
 * </p>
 */
public class AlarmHelper {
    private static AlarmHelper sInstance = null;

    private HtcLogger mLogger = new PushLoggerFactory(this).create();
    private Context mContext;
    private AlarmManager mAlarmManager;
    private Intent mRegIntent;
    private Intent mUpdateIntent;
    private Intent mUnregIntent;

    /**
     * Get the singleton instance of {@link AlarmHelper}.
     * 
     * @param context Context used to get application context.
     * @return {@link AlarmHelper}
     */
    public static synchronized AlarmHelper get(Context context) {
        if (context == null)
            throw new IllegalArgumentException("'context' is null.");

        if (sInstance == null)
            sInstance = new AlarmHelper(context.getApplicationContext());
        return sInstance;
    }

    /**
     * Private constructor.
     * 
     * @param context Context to operate on.
     */
    private AlarmHelper(Context context) {
        mContext = context;
        mAlarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);

        // Registration / update intents.
        mRegIntent = new Intent(context, RegistrationService.class);
        mRegIntent.putExtra(PnsInternalDefs.KEY_CAUSE, Context.ALARM_SERVICE);
        mUpdateIntent = new Intent(context, UpdateRegistrationService.class);
        mUpdateIntent.putExtra(PnsInternalDefs.KEY_CAUSE, Context.ALARM_SERVICE);
        mUnregIntent = new Intent(context, UnregistrationService.class);
        mUnregIntent.putExtra(PnsInternalDefs.KEY_CAUSE, Context.ALARM_SERVICE);
    }

    /**
     * Schedule an alarm to trigger registration.
     * 
     * @param timestamp Unix time in milliseconds.
     */
    public void scheduleNextRegistration(long timestamp) {
        scheduleNextRegistration(timestamp, 0);
    }

    public void scheduleNextRegistration(long timestamp, int periodInMinutes) {
        Intent tmpIntent = new Intent(mRegIntent);
        tmpIntent.putExtra(RegistrationService.EXTRA_ALARM_TIME, timestamp);
        tmpIntent.putExtra(RegistrationService.EXTRA_ALARM_PERIOD_IN_MINUTES, periodInMinutes);

        // Set alarm.
        PendingIntent pendingIntent = PendingIntent.getService(mContext, 0, tmpIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        mAlarmManager.set(AlarmManager.RTC, timestamp, pendingIntent);
        mLogger.verbose("Schedule on ", new Date(timestamp));

        // Record the schedule.
        PnsRecords.get(mContext).setNextRegistration(timestamp);
    }

    /**
     * Schedule an alarm to trigger registration distributedly.
     *
     * @param periodInMinutes Unix time in milliseconds.
     */
    public void scheduleRegisterDistributedInPeriod(int periodInMinutes) {
        long timestamp;
        // Set previous scheduled timestamp instead if it's not execute before for some reasons.
        long previousNextRegistrationTimestamp = PnsRecords.get(mContext).getNextRegistration();
        if (previousNextRegistrationTimestamp != 0) {
            // Not executed before, reset it
            mLogger.debug("reset next registration timestamp");
            timestamp = previousNextRegistrationTimestamp;
        } else {
            long period = getRandomPeriodMillis(
                    periodInMinutes * DateUtils.MINUTE_IN_MILLIS,
                    DateUtils.MINUTE_IN_MILLIS);
            mLogger.verbose("Random period = ", periodInMinutes, ", random seconds = ", period / DateUtils.SECOND_IN_MILLIS);
            timestamp = System.currentTimeMillis() + period;
        }

        scheduleNextRegistration(timestamp, periodInMinutes);
    }

    private long getRandomPeriodMillis(long periodInMillis, long minMillis) {
        if (periodInMillis < minMillis) {
            return periodInMillis;
        }
        return Math.abs(new Random().nextLong()) % (periodInMillis - minMillis) + minMillis;
    }

    /**
     * Schedule an alarm to trigger update registration.
     * 
     * @param timestamp Unix time in milliseconds.
     */
    public void scheduleNextUpdate(long timestamp) {
        // Set alarm.
        PendingIntent pendingIntent = PendingIntent.getService(mContext, 0, mUpdateIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        mAlarmManager.set(AlarmManager.RTC, timestamp, pendingIntent);
        mLogger.verbose("Schedule on ", new Date(timestamp));

        // Record the schedule.
        PnsRecords.get(mContext).setNextUpdate(timestamp);
    }

    /**
     * Schedule an alarm to trigger update registration distributedly.
     * 
     * @param periodInMinutes Unix time in milliseconds.
     */
    public void scheduleUpdateDistributedInPeriod(int periodInMinutes) {
        long timestamp;
        // Set previous scheduled timestamp instead if it's not execute before for some reasons.
        long previousNextUpdateTimestamp = PnsRecords.get(mContext).getNextUpdate();
        if (previousNextUpdateTimestamp != 0) {
            // Not executed before, reset it
            mLogger.debug("reset next update timestamp");
            timestamp = previousNextUpdateTimestamp;
        } else {
            long period = getRandomPeriodMillis(
                    periodInMinutes * DateUtils.MINUTE_IN_MILLIS,
                    DateUtils.MINUTE_IN_MILLIS);
            mLogger.verbose("Random period = ", periodInMinutes, ", random seconds = ", period / DateUtils.SECOND_IN_MILLIS);
            timestamp = System.currentTimeMillis() + period;
        }
        scheduleNextUpdate(timestamp);
    }

    /**
     * Schedule an alarm to trigger unregistration.
     * 
     * @param timestamp Unix time in milliseconds.
     */
    public void scheduleNextUnregistration(long timestamp) {

        // Always cancel the existing one before set another one.
        cancelNextUnregistration();

        // Set alarm.
        PendingIntent pendingIntent = PendingIntent.getService(mContext, 0, mUnregIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        mAlarmManager.set(AlarmManager.RTC, timestamp, pendingIntent);
        mLogger.verbose("Schedule on ", new Date(timestamp));

        // Record the schedule.
        PnsRecords.get(mContext).setNextUnregistration(timestamp);
    }
    
    /**
     * Cancel the scheduled registration alarm.
     */
    public void cancelNextRegistration() {
        mLogger.verbose();

        PendingIntent pendingIntent = PendingIntent.getService(mContext, 0, mRegIntent,
                PendingIntent.FLAG_NO_CREATE);
        if (pendingIntent != null) {
            mLogger.debug("Found existing pending intent. Cancel it now.");
            mAlarmManager.cancel(pendingIntent);
        }

        // Clear the schedule record.
        PnsRecords.get(mContext).clearNextRegistration();
    }

    /**
     * Cancel the scheduled update registration alarm.
     */
    public void cancelNextUpdate() {
        mLogger.verbose();

        PendingIntent pendingIntent = PendingIntent.getService(mContext, 0, mUpdateIntent,
                PendingIntent.FLAG_NO_CREATE);
        if (pendingIntent != null) {
            mLogger.debug("Found existing pending intent. Cancel it now.");
            mAlarmManager.cancel(pendingIntent);
        }

        // Clear the schedule record.
        PnsRecords.get(mContext).clearNextUpdate();
    }
    
    /**
     * Cancel the scheduled unregistration alarm.
     */
    public void cancelNextUnregistration() {
        mLogger.verbose();

        PendingIntent pendingIntent = PendingIntent.getService(mContext, 0, mUnregIntent,
                PendingIntent.FLAG_NO_CREATE);
        if (pendingIntent != null) {
            mLogger.debug("Found existing pending intent. Cancel it now.");
            mAlarmManager.cancel(pendingIntent);
        }

        // Clear the schedule record.
        PnsRecords.get(mContext).clearNextUnregistration();
    }
}
