
package com.htc.lib1.cs.push.retrypolicy;

import android.content.Context;
import android.text.TextUtils;
import android.text.format.DateUtils;

import com.htc.lib0.htcdebugflag.HtcWrapHtcDebugFlag;
import com.htc.lib1.cs.ConnectivityHelper;
import com.htc.lib1.cs.logging.HtcLogger;
import com.htc.lib1.cs.pns.RegistrationPolicy;
import com.htc.lib1.cs.pns.RetryPolicy;
import com.htc.lib1.cs.push.AlarmHelper;
import com.htc.lib1.cs.push.PnsDefs;
import com.htc.lib1.cs.push.PnsInternalDefs;
import com.htc.lib1.cs.push.PnsRecords;
import com.htc.lib1.cs.push.PushLoggerFactory;
import com.htc.lib1.cs.push.exception.GooglePlayServicesAvailabilityException;
import com.htc.lib1.cs.push.exception.HtcAccountAvailabilityException;
import com.htc.lib1.cs.push.google.GooglePlayServicesAvailabilityUtils.Availability;
import com.htc.lib1.cs.push.receiver.OneTimeOnGooglePlayServicesPackageRecoveredReceiver;
import com.htc.lib1.cs.push.utils.AppComponentSettingUtils;
import com.htc.lib1.cs.push.utils.SystemPropertiesProxy;

/**
 * Retry policy to use when the program code works as library inside the
 * integrated app.
 *
 * @author samael_wang@htc.com
 */
public class LibraryRetryPolicy implements RetryPolicy {
    private static final int EXPONENTIAL_BACKOFF_RETRY_THREADSHOLD = 5;
    private HtcLogger mLogger = new PushLoggerFactory(this).create();
    private Context mContext;
    private DynamicOneTimeOnConnectedReceiver mReceiver = null;

    /**
     * Create an instance.
     *
     * @param context Context to operate on.
     */
    public LibraryRetryPolicy(Context context) {
        if (context == null)
            throw new IllegalArgumentException("'context' is null.");
        mContext = context;
    }

    @Override
    public void scheduleRegistrationRetry(Exception cause,
            RegistrationPolicy registrationPolicy, boolean allowAlarms, int retryAfterInSec) {
        if (cause instanceof HtcAccountAvailabilityException) {
            mLogger.info("HTC Account is not present but required. We don't make any retry in this case.");

        } else if (cause instanceof GooglePlayServicesAvailabilityException
                && ((GooglePlayServicesAvailabilityException) cause).getAvailability() == Availability.RECOVERABLE) {
            mLogger.info("Waiting for Google Play Services package recovering...");

            // Wait for Google Play Services package recovering.
            AppComponentSettingUtils.enable(mContext,
                    OneTimeOnGooglePlayServicesPackageRecoveredReceiver.class);

        } else if (!ConnectivityHelper.get(mContext).isConnected()) {
            mLogger.info("Waiting for network connectivity...");

            dynamicallyRegisterNetworkCallback();

            // Still schedule a daily retry just in case.
            AlarmHelper.get(mContext).scheduleNextRegistration(
                    getExponentialBackoffRetryTimestamp(EXPONENTIAL_BACKOFF_RETRY_THREADSHOLD + 1));
        } else {
            mLogger.info("Schedule alarm retry.");

            PnsRecords records = PnsRecords.get(mContext);
            long registerFailCount = records.getRegistrationFailCount();

            long nextRegisterTimestamp = getExponentialBackoffRetryTimestamp(registerFailCount);
            if (retryAfterInSec != PnsDefs.DEFAULT_RETRY_AFTER_VALUE_IN_SEC) {
                nextRegisterTimestamp = System.currentTimeMillis() + retryAfterInSec * 1000;
            }
            AlarmHelper.get(mContext).scheduleNextRegistration(nextRegisterTimestamp);
        }
    }

    /**
     * Get exponential back off retry timestamp
     *
     * @param retryCount retry count
     * @return system timestamp
     */
    private long getExponentialBackoffRetryTimestamp(long retryCount) {
        long timestamp = System.currentTimeMillis();
        if (retryCount <= EXPONENTIAL_BACKOFF_RETRY_THREADSHOLD) {
            int retryTimeRatio = PnsInternalDefs.RETRY_TIME_RATIO;
            if (HtcWrapHtcDebugFlag.Htc_DEBUG_flag) {
                try {
                    String value = SystemPropertiesProxy.get(mContext,
                            PnsInternalDefs.KEY_SYSTEM_PROP_RETRY_TIME_RATIO);
                    if (!TextUtils.isEmpty(value)) {
                        retryTimeRatio = Integer.parseInt(value);
                    }
                } catch (NumberFormatException e) {
                    mLogger.error(e);
                }
                mLogger.debug("retry time ratio from system property = ", retryTimeRatio);
            }

            // Schedule exponential backoff.
            timestamp = timestamp + Math.round(Math.pow(2, retryCount - 1)) * retryTimeRatio
                    * DateUtils.MINUTE_IN_MILLIS;
        } else {
            // Schedule daily retry.
            timestamp = timestamp + DateUtils.DAY_IN_MILLIS;
        }
        return timestamp;
    }

    /**
     * dynamically register receiver to listen network changed event
     */
    private void dynamicallyRegisterNetworkCallback() {
        mLogger.debug();

        // renew receiver
        if (mReceiver != null) {
            mReceiver = null;
        }

        // Wait for network being connected.
        mReceiver = new DynamicOneTimeOnConnectedReceiver(mContext);
    }

    @Override
    public void scheduleUpdateRetry(Exception cause,
            RegistrationPolicy registrationPolicy, boolean allowAlarms, int retryAfterInSec) {
        if (!ConnectivityHelper.get(mContext).isConnected()) {
            mLogger.info("Waiting for network connectivity...");

            dynamicallyRegisterNetworkCallback();

            // Still schedule a daily retry just in case.
            AlarmHelper.get(mContext).scheduleNextUpdate(
                    getExponentialBackoffRetryTimestamp(EXPONENTIAL_BACKOFF_RETRY_THREADSHOLD + 1));

        } else {
            mLogger.info("Schedule alarm retry.");

            PnsRecords records = PnsRecords.get(mContext);
            long updateFailCount = records.getUpdateFailCount();

            long nextUpdateTimestamp = getExponentialBackoffRetryTimestamp(updateFailCount);
            if (retryAfterInSec != PnsDefs.DEFAULT_RETRY_AFTER_VALUE_IN_SEC) {
                nextUpdateTimestamp = System.currentTimeMillis() + retryAfterInSec * 1000;
            }
            AlarmHelper.get(mContext).scheduleNextUpdate(nextUpdateTimestamp);
        }
    }

    @Override
    public void scheduleUnregistrationRetry(Exception cause,
            RegistrationPolicy registrationPolicy, boolean allowAlarms, int retryAfterInSec) {
        if (!ConnectivityHelper.get(mContext).isConnected()) {
            mLogger.info("Waiting for network connectivity...");

            dynamicallyRegisterNetworkCallback();

            // Still schedule a daily retry just in case.
            AlarmHelper.get(mContext).scheduleNextUnregistration(
                    getExponentialBackoffRetryTimestamp(EXPONENTIAL_BACKOFF_RETRY_THREADSHOLD + 1));
        } else {
            mLogger.info("Schedule alarm retry.");

            PnsRecords records = PnsRecords.get(mContext);
            long unregisterFailCount = records.getUnregistrationFailCount();

            long nextUnregisterTimestamp = getExponentialBackoffRetryTimestamp(unregisterFailCount);
            if (retryAfterInSec != PnsDefs.DEFAULT_RETRY_AFTER_VALUE_IN_SEC) {
                nextUnregisterTimestamp = System.currentTimeMillis() + retryAfterInSec * 1000;
            }
            AlarmHelper.get(mContext).scheduleNextUnregistration(nextUnregisterTimestamp);
        }
    }
}
