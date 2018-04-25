
package com.htc.lib1.cs.push;

import android.content.Context;
import android.text.TextUtils;

import com.htc.lib1.cs.app.ProcessUtils;
import com.htc.lib1.cs.logging.HtcLogger;
import com.htc.lib1.cs.pns.RegistrationPolicy;
import com.htc.lib1.cs.pns.RetryPolicy;
import com.htc.lib1.cs.push.dm2.AbstractPnsConfigModel;
import com.htc.lib1.cs.push.dm2.PnsConfig;
import com.htc.lib1.cs.push.exception.ReRegistrationNeeddedException;
import com.htc.lib1.cs.push.exception.RegistrationFailedException;
import com.htc.lib1.cs.push.exception.UnregistrationFailedException;
import com.htc.lib1.cs.push.exception.UnregistrationNeededException;
import com.htc.lib1.cs.push.exception.UpdateRegistrationFailedException;
import com.htc.lib1.cs.push.httputils.PnsServiceUnavailableException;
import com.htc.lib1.cs.push.registrator.Registrator;
import com.htc.lib1.cs.push.registrator.RegistratorBuilder;
import com.htc.lib1.cs.push.registrator.RegistratorUtil;
import com.htc.lib1.cs.push.retrypolicy.LibraryRetryPolicy;
import com.htc.lib1.cs.push.utils.SystemPropertiesProxy;
import com.htc.lib1.cs.security.ProviderInstallerUtils;

import java.util.UUID;

/**
 * The top level class of the PNS client model.
 *
 * @author samael_wang@htc.com
 */
public class PnsModel {
    private static PnsModel sInstance;
    private HtcLogger mLogger = new PushLoggerFactory(this).create();
    private Context mContext;
    private RegistrationPolicy mRegistrationPolicy;
    private RetryPolicy mRetryPolicy;
    private boolean mAllowAlarms;
    private AbstractPnsConfigModel mPnsConfigModel = null;

    /**
     * Initialize the singleton instance of {@link PnsModel}. It must only be
     * invoked once and must on main process.
     *
     * @param context Context used to retrieve application context.
     * @param registrationPolicy Registration policy to use. The available
     *            options are defined in {@link RegistrationPolicy}.
     * @param retryPolicy Retry policy to use.
     * @return {@link PnsModel}
     */
    /**
     * Initialize the singleton instance of {@link PnsModel}. It must only be
     * invoked once and must on main process.
     *
     * @param context            Context used to retrieve application context.
     * @param registrationPolicy Registration policy to use. The available
     *                           options are defined in {@link RegistrationPolicy}.
     * @param retryPolicy        Retry policy to use.
     * @param allowAlarms        Allow PNS library to setup alarms to wake the app up
     *                           periodically and update PNS registration even if user's not
     *                           using the app.
     * @param pnsConfigModel     PnsConfig loader for current region used
     * @return instance of PnsModel
     */
    public static synchronized PnsModel init(Context context,
                                             RegistrationPolicy registrationPolicy,
                                             RetryPolicy retryPolicy,
                                             boolean allowAlarms,
                                             AbstractPnsConfigModel pnsConfigModel) {
        if (context == null)
            throw new IllegalArgumentException("'context' is null.");
        if (registrationPolicy == null)
            throw new IllegalArgumentException("'registrationPolicy' is null.");
        if (retryPolicy == null)
            throw new IllegalArgumentException("'retryPolicy' is null.");
        if (!ProcessUtils.isMainProcess(context))
            throw new IllegalStateException("'PnsModel' is meant to work on main process only.");

        if (sInstance == null) {
            sInstance = new PnsModel(context, registrationPolicy, retryPolicy,
                            allowAlarms, pnsConfigModel);
            return sInstance;
        }

        throw new IllegalStateException(
                "'init' must only be invoked once in the process lifetime.");
    }

    /**
     * Get the singleton instance of {@link PnsModel}. It must only be invoked
     * after {@link #init(Context, RegistrationPolicy, RetryPolicy, boolean, AbstractPnsConfigModel)}.
     *
     * @return {@link PnsModel}
     */
    public static synchronized PnsModel get() {
        if (sInstance == null)
            throw new IllegalStateException("'get' must only be invoked after 'init'.");

        return sInstance;
    }

//    public static synchronized void setAsNull() {
//        sInstance = null;
//    }

    // Private constructor.
    private PnsModel(Context context, RegistrationPolicy registrationPolicy,
                     RetryPolicy retryPolicy, boolean allowAlarms, AbstractPnsConfigModel pnsConfigModel) {
        mContext = context.getApplicationContext();
        if (mContext == null) {
            mContext = context;
        }
        mRegistrationPolicy = registrationPolicy;
        mRetryPolicy = retryPolicy;
        mAllowAlarms = allowAlarms;
        mPnsConfigModel = pnsConfigModel;
    }

    /**
     * Register PNS.
     *
     * @param cause The cause of this registration. Use the intent action if
     *              available, or use a custom description.
     * @return {@code true} if success.
     */
    public synchronized boolean register(String cause) {
        ProcessUtils.ensureNotOnMainThread();
        if (TextUtils.isEmpty(cause))
            throw new IllegalArgumentException("'cause' is null or empty.");

        mLogger.info("Registration caused by: ", cause);
        ProviderInstallerUtils.tryInstallIfNeed(mContext);

        /*
         * Build registrator. If dm-config specifies preferred push provider,
         * ask RegistratorBuilder to use it if possible. Otherwise the builder
         * will find a proper provider automatically.
         */
        PnsConfig config = mPnsConfigModel.getConfig();
        RegistratorBuilder builder = new RegistratorBuilder(mContext, config);
        PushProvider preferredProvider = PushProvider.fromString(config.pushProvider);
        mLogger.debug("Preferred push provider: ", preferredProvider);

        builder.setPreferedProvider(preferredProvider);
        Registrator registrator = builder.build();
        PnsRecords records = PnsRecords.get(mContext);

        if (!records.isRegistered()) {
            /*
             * Always disable Baidu component before registration so if Baidu is
             * not chosen it won't make connections.
             */
            RegistratorUtil.disableBaiduPushComponents(mContext);

            // Generate client UUID if not available.
            if (records.getUuid() == null)
                records.setUuid(UUID.randomUUID());

            try {
                // Register.
                RegistrationCredentials credentials = registrator.register(mRegistrationPolicy);

                // Save registration info.
                records.setPushProvider(registrator.getProvider());
                records.setRegCredentials(credentials);
                records.setRegistered(true);

                if (mAllowAlarms) {
                    /*
                     * cancel retry alarm if any.
                     */
                    AlarmHelper.get(mContext).cancelNextRegistration();
                    AlarmHelper.get(mContext).cancelNextUnregistration();
                }

                // Clear retry record.
                records.setNextRegistration(0);

                // Add record and send broadcast.
                records.addRegistrationEvent(cause, null /* resultCause */, true /* success */);
                BroadcastUtils.registrationSuccessed(mContext);
                mLogger.info("Registration successed.");

                return true;
            } catch (RegistrationFailedException e) {
                mLogger.error(e);

                // Add record and send broadcast.
                records.addRegistrationEvent(cause, e.getMessage(), false /* success */);
                BroadcastUtils.registrationFailed(mContext, e.getMessage());

                // Schedule retry.
                getRetryPolicy().scheduleRegistrationRetry(e, mRegistrationPolicy, mAllowAlarms, PnsDefs.DEFAULT_RETRY_AFTER_VALUE_IN_SEC);

                return false;
            } catch (PnsServiceUnavailableException e) {
                // Schedule retry.
                int retryAfterInSec = e.getRetryAfterValueInSec();
                getRetryPolicy().scheduleRegistrationRetry(e, mRegistrationPolicy, mAllowAlarms, retryAfterInSec);

                return false;
            }
        } else {
            // Add record and send broadcast.
            records.addRegistrationEvent(cause, "device has already registered", true /* success */);
            BroadcastUtils.registrationSuccessed(mContext);
            mLogger.info("Device has already registered.");

            return true;
        }
    }

    /**
     * Update PNS registration.
     *
     * @param cause The cause of this update registration. Use the intent action
     *              if available, or use a custom description.
     * @param force {@code true} to force update. Otherwise it only updates if
     *              the registration record has expired.
     * @return {@code true} if success.
     */
    public synchronized boolean update(String cause, boolean force) {
        ProcessUtils.ensureNotOnMainThread();
        if (TextUtils.isEmpty(cause))
            throw new IllegalArgumentException("'cause' is null or empty.");

        mLogger.info("Update registration caused by: ", cause);
        ProviderInstallerUtils.tryInstallIfNeed(mContext);

        PnsRecords records = PnsRecords.get(mContext);
        if (records.isRegistered()) {
            /*
             * Build registrator. If dm-config specifies preferred push
             * provider, ask RegistratorBuilder to use it if possible; if
             * dm-config doesn't specify preferred push provider, ask the
             * builder to use whatever has been used to register if possible.
             */
            PnsConfig config = mPnsConfigModel.getConfig();
            RegistratorBuilder builder = new RegistratorBuilder(mContext, config);
            PushProvider preferredProvider = PushProvider.fromString(config.pushProvider);
            if (preferredProvider == null)
                preferredProvider = records.getPushProvider();
            mLogger.debug("Preferred push provider: ", preferredProvider);

            builder.setPreferedProvider(preferredProvider);
            Registrator registrator = builder.build();

            /*
             * Log the reason of update. If it's consider not necessary to
             * update, skip the update and treat it as a successful result.
             */
            PushProvider provider = registrator.getProvider();
            if (records.getPushProvider() != provider) {
                mLogger.info("Start updating since the push provider is changed.");
                if (provider.equals(PushProvider.GCM)) {
                    RegistratorUtil.disableBaiduPushComponents(mContext);
                } else if (provider.equals(PushProvider.BAIDU)) {
                    RegistratorUtil.enableBaiduPushComponents(mContext);
                } else {
                    mLogger.error("Unhandled push provider: ", provider);
                }
            } else if (force) {
                mLogger.info("Start updating since caller requested force update.");
            }

            try {
                registrator.update(mRegistrationPolicy);

                // Add record and send broadcast.
                records.addUpdateEvent(cause, null /* resultCause */, true /* success */);
                BroadcastUtils.updateSuccessed(mContext);
                mLogger.info("Update registration succeeded.");

                // Save current MCC/MNC
                String detected_mccMncNumeric = SystemPropertiesProxy.get(mContext, PnsInternalDefs.KEY_SYSTEM_PROP_SIM_MCC_MNC); // 46692,46692
                PnsRecords.get(mContext).setMccmnc(detected_mccMncNumeric);

                return true;
            } catch (ReRegistrationNeeddedException e) {
                mLogger.warning("Re-registration needed: ", e.getMessage());

                /*
                 * Try re-registration.
                 */
                records.setRegistered(false);
                if (register(cause)) {
                    // Add record and send broadcast.
                    records.addUpdateEvent(cause, "re-registration succeeded", true /* success */);
                    BroadcastUtils.updateSuccessed(mContext);
                    mLogger.info("Re-registration succeeded.");

                    // Save current MCC/MNC
                    String detected_mccMncNumeric = SystemPropertiesProxy.get(mContext, PnsInternalDefs.KEY_SYSTEM_PROP_SIM_MCC_MNC); // 46692,46692
                    PnsRecords.get(mContext).setMccmnc(detected_mccMncNumeric);

                    /*
                    // For none-official release SDK bug: send registration event while updating
                    // Workaround: always re-register when trying to update
                    // FIXME: will remove in the next version
                    if (records.getPushProvider().equals(PushProvider.BAIDU)) {
                        records.setForceBaiduReRegistered(true);
                    }
                    */
                    return true;
                } else {
                    // Add record and send broadcast.
                    records.addUpdateEvent(cause, "re-registration failed", false /* success */);
                    BroadcastUtils.updateFailed(mContext, "Re-registration failed.");
                    mLogger.warning("Re-registration failed.");

                    return false;
                }
            } catch (UnregistrationNeededException e) {
                mLogger.error(e);

                // Add record and send broadcast.
                records.addUpdateEvent(cause, e.getMessage(), false /* success */);
                BroadcastUtils.updateFailed(mContext, e.getMessage());

                // Unregister.
                unregister(cause);

                return false;
            } catch (UpdateRegistrationFailedException e) {
                mLogger.error(e);

                // Add record and send broadcast.
                records.addUpdateEvent(cause, e.getMessage(), false /* success */);
                BroadcastUtils.updateFailed(mContext, e.getMessage());

                // Schedule retry.
                getRetryPolicy().scheduleUpdateRetry(e, mRegistrationPolicy, mAllowAlarms, PnsDefs.DEFAULT_RETRY_AFTER_VALUE_IN_SEC);

                return false;
            } catch (PnsServiceUnavailableException e) {
                mLogger.error(e);

                // Add record.
                records.addUpdateEvent(cause, e.getMessage(), false /* success */);

                // Schedule retry.
                int retryAfterInSec = e.getRetryAfterValueInSec();
                getRetryPolicy().scheduleUpdateRetry(e, mRegistrationPolicy, mAllowAlarms, retryAfterInSec);

                return false;
            }

        } else {
            // Add record and send broadcast.
            records.addUpdateEvent(cause, "device was not registered", false /* success */);
            BroadcastUtils.updateFailed(mContext, "Device was not registered.");
            mLogger.warning("Device was not registered.");

            return false;
        }
    }

    /**
     * Unregister from PNS.
     *
     * @param cause The cause of this unregistration. Use the intent action if
     *              available, or use a custom description.
     * @return {@code true} if success.
     */
    public synchronized boolean unregister(String cause) {
        ProcessUtils.ensureNotOnMainThread();
        if (TextUtils.isEmpty(cause))
            throw new IllegalArgumentException("'cause' is null or empty.");

        mLogger.info("Unregistration caused by: ", cause);
        ProviderInstallerUtils.tryInstallIfNeed(mContext);

        PnsRecords records = PnsRecords.get(mContext);
        if (records.isRegistered() || records.isUnregisterFailed()) {
            /*
             * For unregistration, always ask the builder to use whatever has
             * been used to register.
             */
            Registrator registrator = new RegistratorBuilder(mContext, mPnsConfigModel.getConfig())
                    .setPreferedProvider(records.getPushProvider()).build();

            try {
                registrator.unregister();

                // Add record and send broadcast.
                records.addUnregistrationEvent(cause, null /* resultCause */, true /* success */);
                BroadcastUtils.unregistrationSuccessed(mContext);
                mLogger.info("Unregistration successed.");

                return true;
            } catch (UnregistrationFailedException e) {
                mLogger.error(e);

                if (!records.isUnregisterFailed()) {
                    records.setUnregisterFailed(true);
                }

                // Add record and send broadcast.
                records.addUnregistrationEvent(cause, e.getMessage(), false /* success */);
                BroadcastUtils.unregistrationFailed(mContext, e.getMessage());

                // Schedule retry.
                getRetryPolicy().scheduleUnregistrationRetry(e, mRegistrationPolicy, mAllowAlarms, PnsDefs.DEFAULT_RETRY_AFTER_VALUE_IN_SEC);

                return false;
            } catch (PnsServiceUnavailableException e) {
                // Schedule retry.
                int retryAfterInSec = e.getRetryAfterValueInSec();
                getRetryPolicy().scheduleUnregistrationRetry(e, mRegistrationPolicy, mAllowAlarms, retryAfterInSec);

                return false;
            } finally {
                /*
                 * Clear registration record no matter the action succeed or
                 * not.
                 */
                records.setRegistered(false);
            }
        } else {
            // Add record and send broadcast.
            records.addUnregistrationEvent(cause, "device has already unregistered", true /* success */);
            BroadcastUtils.unregistrationSuccessed(mContext);
            mLogger.info("Device has already unregistered.");

            return true;
        }
    }

    /**
     * Update PNS registration.
     *
     * @param cause The cause of this update registration. Use the intent action
     *              if available, or use a custom description.
     * @return {@code true} if success.
     */
    public boolean update(String cause) {
        return update(cause, false);
    }

    /**
     * Get retry policy to use.
     *
     * @return {@link RetryPolicy}
     */
    private synchronized RetryPolicy getRetryPolicy() {
        if (mRetryPolicy == null)
            mRetryPolicy = new LibraryRetryPolicy(mContext);

        mLogger.debug("Using retry policy '", mRetryPolicy.getClass().getSimpleName(), "'...");
        return mRetryPolicy;
    }

    public static synchronized boolean checkDataUsageAgreement(Context context) {
        PnsRecords records = PnsRecords.get(context);
        if (records.hasDataUsageAgreement()) {
            return true;
        }

        if (records.isRegistered()) {
            new PushLoggerFactory(PnsModel.class).create()
                    .warning("Forced clear registration because of missing data usage agreement.");
            records.setRegistered(false);
            records.setUnregisterFailed(false);
            records.clearNextUnregistration();
            records.setRegCredentials(null);
            records.setMccmnc(null);
            records.addUpdateEvent(
                    "MissingDataUsageAgreement", "Force clear registration",
                    true /* success */);
        }

        RegistratorUtil.disableBaiduPushComponents(context, true);

        return false;
    }

    public static synchronized void revokeDataUsageAgreement(Context context) {
        PnsRecords records = PnsRecords.get(context);

        if (records.isRegistered()) {
            new PushLoggerFactory(PnsModel.class).create()
                    .warning("Forced clear registration because of missing data usage agreement.");
            records.setRegistered(false);
            records.setUnregisterFailed(false);
            records.clearNextUnregistration();
            records.setRegCredentials(null);
            records.setMccmnc(null);
            records.addUpdateEvent(
                    "MissingDataUsageAgreement", "Force clear registration",
                    true /* success */);
        }

        records.setHasDataUsageAgreement(false);

        RegistratorUtil.disableBaiduPushComponents(context, true);
    }
}
