
package com.htc.lib1.cs.pns;

import java.io.IOException;

import com.baidu.android.pushservice.PushSettings;
import com.htc.lib0.htcdebugflag.HtcWrapHtcDebugFlag;
import com.htc.lib1.cs.account.DataServiceFuture;
import com.htc.lib1.cs.account.DataServiceFuture.DataServiceCallback;
import com.htc.lib1.cs.account.HtcAccountDefs;
import com.htc.lib1.cs.account.HtcAccountManager;
import com.htc.lib1.cs.account.HtcAccountManagerCreator;
import com.htc.lib1.cs.app.ProcessUtils;
import com.htc.lib1.cs.logging.HtcLogger;
import com.htc.lib1.cs.push.AlarmHelper;
import com.htc.lib1.cs.push.BuildConfig;
import com.htc.lib1.cs.push.PnsInternalDefs;
import com.htc.lib1.cs.push.PnsModel;
import com.htc.lib1.cs.push.PnsRecords;
import com.htc.lib1.cs.push.PushLoggerFactory;
import com.htc.lib1.cs.push.dm2.AbstractPnsConfigModel;
import com.htc.lib1.cs.push.dm2.PnsConfigModelNoDMImpl;
import com.htc.lib1.cs.push.retrypolicy.LibraryRetryPolicy;
import com.htc.lib1.cs.push.service.UnregistrationService;
import com.htc.lib1.cs.push.service.UpdateRegistrationService;
import com.htc.lib1.cs.push.utils.SystemPropertiesProxy;

import android.accounts.Account;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;

/**
 * General initializer for push notification service library.
 * 
 * @author samael_wang
 */
public class PnsInitializer {
    private HtcLogger mLogger = new PushLoggerFactory(this).create();
    private Context mAppContext;
    private RegistrationPolicy mRegistrationPolicy;
    private RetryPolicy mRetryPolicy;
    private boolean mAllowAlarms;
    private boolean mEnableRegistrationSerivce;
    private AbstractPnsConfigModel mPnsConfigModel = null;
    private boolean mStartRegistrationASAP = false;

    /**
     * Create an instance with {@link RegistrationPolicy#ALWAYS_REGISTER}
     * and {@link LibraryRetryPolicy}. Do not allow to setup alarms.
     * 
     * @param context Context used to retrieve application context.
     */
    public PnsInitializer(Context context) {
        this(context, RegistrationPolicy.ALWAYS_REGISTER,
                new LibraryRetryPolicy(context.getApplicationContext()), true, null, true);
    }

    /**
     * @param context Context used to retrieve application context.
     * @param registrationPolicy Policy to use for registration. The values are
     *            defined in {@link RegistrationPolicy}.
     * @param retryPolicy {@link RetryPolicy} to use when PNS operation fails.
     *            {@code null} to use the default implementation.
     * @param allowAlarms Allow PNS library to setup alarms to wake the app up
     *            periodically and update PNS registration even if user's not
     *            using the app.
     * @param pnsConfigModel PnsConfig loader for current region used
     * @param enableRegistrationSerivce Enable register
     *            registration/unregistration/update services or not
     */
    public PnsInitializer(Context context, RegistrationPolicy registrationPolicy,
            RetryPolicy retryPolicy, boolean allowAlarms, AbstractPnsConfigModel pnsConfigModel,
            boolean enableRegistrationSerivce) {
        if (context == null)
            throw new IllegalArgumentException("'context' is null.");
        if (registrationPolicy == null)
            throw new IllegalArgumentException("'registrationPolicy' is null.");
        if (retryPolicy == null)
            throw new IllegalArgumentException("'retryPolicy' is null.");

        mAppContext = context.getApplicationContext();
        if (mAppContext == null) {
            mAppContext = context;
        }
        mRegistrationPolicy = registrationPolicy;
        mRetryPolicy = retryPolicy;
        mAllowAlarms = allowAlarms;

        // use PnsConfigModelNoDMImpl if pns config model does not set before
        mPnsConfigModel = pnsConfigModel == null ? PnsConfigModelNoDMImpl.get(mAppContext)
                : pnsConfigModel;

        mEnableRegistrationSerivce = enableRegistrationSerivce;

        PnsRecords recorder = PnsRecords.get(mAppContext);
        recorder.setRegistrationPolicy(mRegistrationPolicy);
        recorder.setAllowAlarm(mAllowAlarms);
        recorder.setEnableRegistrationService(mEnableRegistrationSerivce);
    }

    /**
     * Initialize PNS.
     */
    public final void init() {
        if (PnsInternalDefs.PROCESS_BAIDU.equals(ProcessUtils.getSubProcessName(mAppContext))) {
            mLogger.debug("Initializing Baidu subprocess.");
            initBaiduProcess();
        }

        // Operate PNSModel only on main process.
        if (ProcessUtils.isMainProcess(mAppContext)) {
            mLogger.debug("Initializing main process.");
            PnsRecords records = PnsRecords.get(mAppContext);
            records.setHasDataUsageAgreement(true);
            if (mRegistrationPolicy == RegistrationPolicy.ALWAYS_REGISTER) {
                records.setUnregisterFailed(false);
                records.clearNextUnregistration();
            }

            initMainProcess();

            if (mEnableRegistrationSerivce && mStartRegistrationASAP && !records.isRegistered()) {
                AlarmHelper.get(mAppContext).scheduleNextRegistration(
                        System.currentTimeMillis() + 5000);
            }
        }
    }

    public static void revokeDataUsageAgreement(Context context) {
        PnsModel.revokeDataUsageAgreement(context);
    }

    public static void checkDataUsageAgreement(Context context) {
        if (ProcessUtils.isMainProcess(context) ||
                PnsInternalDefs.PROCESS_BAIDU.equals(ProcessUtils.getSubProcessName(context))) {
            PnsModel.checkDataUsageAgreement(context);
        }
    }

    /**
     * Perform Baidu subprocess specific initialization. The default
     * implementation overrides default {@code UncaughtExceptionHandler}.
     */
    private void initBaiduProcess() {
        // Set exception handler for baidu.
        mLogger.debug("Setting BaiduUncaughtExceptionHandler.");

        if (HtcWrapHtcDebugFlag.Htc_SECURITY_DEBUG_flag
                || HtcWrapHtcDebugFlag.Htc_DEBUG_flag
                || BuildConfig.DEBUG) {
            mLogger.debug("Enable baidu push's debugger info.");
            PushSettings.enableDebugMode(mAppContext, true);
        }

        Thread.setDefaultUncaughtExceptionHandler(new BaiduUncaughtExceptionHandler());
    }

    /**
     * An exception handler for Baidu subprocess.
     */
    private static class BaiduUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
        private HtcLogger mmLogger = new PushLoggerFactory(this).create();

        @Override
        public void uncaughtException(Thread thread, Throwable ex) {
            // Find the root cause.
            Throwable rootCause = ex;
            while (ex.getCause() != null && rootCause != ex.getCause()) {
                rootCause = ex.getCause();
            }

            // Log the exception and root cause in very fatal level.
            mmLogger.fatal("Uncaught exception: ", Log.getStackTraceString(ex));
            if (rootCause != ex)
                mmLogger.fatal("Root cause: ", Log.getStackTraceString(rootCause));

            // Try everything to kill the process.
            Process.killProcess(Process.myPid());
            System.exit(10);
        }
    }

    /**
     * Perform main process initialization. The default implementation inits
     * {@link PnsModel}.
     */
    private void initMainProcess() {
        // Init PnsModel.
        PnsModel.init(mAppContext, mRegistrationPolicy, mRetryPolicy, mAllowAlarms, mPnsConfigModel);

        if (mEnableRegistrationSerivce) {
            startRegistrationServices();
        }
    }

    /**
     * Start registration services for Pns library used
     */
    private void startRegistrationServices() {
        /*
         * Uses static receiver instead.
         */
        HtcAccountManagerCreator.get().asyncCreate(mAppContext,
                new DataServiceCallback<HtcAccountManager>() {

                    @Override
                    public void run(DataServiceFuture<HtcAccountManager> future) {
                        try {
                            HtcAccountManager manager = future.getResult();

                            /*
                             * Make registration / update / unregistration when
                             * running in an integrate app; If it's running as a
                             * stand alone client, let the client implemented
                             * logic to handle registration / update
                             * registration.
                             */
                            PnsRecords records = PnsRecords.get(mAppContext);
                            if (records.isRegistered()) {
                                Account[] accounts = manager
                                        .getAccountsByType(HtcAccountDefs.TYPE_HTC_ACCOUNT);

                                if (accounts != null && accounts.length == 0) {
                                    // Try unregister
                                    if (mRegistrationPolicy == RegistrationPolicy.REGISTER_ON_SIGNED_IN) {
                                        mLogger.info("Try unregister on initialization if user has already logged out.");
                                        UnregistrationService.startService(mAppContext,
                                                PnsInitializer.class.getSimpleName());
                                    } else if (mRegistrationPolicy == RegistrationPolicy.ALWAYS_REGISTER) {
                                        String accountId = records.getAccountId();
                                        if (!TextUtils.isEmpty(accountId)) {
                                            mLogger.info("Try update on initialization if user has already logged out.");
                                            UpdateRegistrationService.startService(mAppContext,
                                                    PnsInitializer.class.getSimpleName());

                                            // clear account id to avoid call it
                                            // again
                                            records.setAccountId(null);
                                        }
                                    } else {
                                        mLogger.info("Unhandled registration policy: ",
                                                mRegistrationPolicy);
                                    }
                                } else if (accounts != null && accounts.length > 0) {
                                    getAccountIdAndUpdate(manager, accounts[0]);

                                    mLogger.info("Do not update on initialization if registered.");
                                }
                            } else if (records.isUnregisterFailed()) {
                                // Try unregister
                                mLogger.info("Try unregister on initialization.");
                                UnregistrationService.startService(mAppContext,
                                        PnsInitializer.class.getSimpleName());
                            } else {
                                // Try register.
                                mLogger.info("Try register on initialization.");
                                int distributedRegisterPeriod = PnsInternalDefs.DISTRIBUTED_REGISTER_IN_MINUTES;
                                if (HtcWrapHtcDebugFlag.Htc_DEBUG_flag) {
                                    try {
                                        String value = SystemPropertiesProxy
                                                .get(mAppContext,
                                                        PnsInternalDefs.KEY_SYSTEM_PROP_DISTRIBUTED_REGISTER_PERIOD);
                                        if (!TextUtils.isEmpty(value)) {
                                            distributedRegisterPeriod = Integer.parseInt(value);
                                        }
                                    } catch (NumberFormatException e) {
                                        mLogger.error(e);
                                    }
                                    mLogger.debug(
                                            "distributed register period from system property = ",
                                            distributedRegisterPeriod);
                                }
                                AlarmHelper.get(mAppContext).scheduleRegisterDistributedInPeriod(
                                        distributedRegisterPeriod);
                            }
                        } catch (OperationCanceledException | IOException e) {
                            mLogger.error(e);
                        }
                    }
                }, null);
    }

    /**
     * get Htc account id and check if account updated.
     * 
     * @param manager instance of HtcAccountManager
     * @param account Signed-in HTC account
     */
    private void getAccountIdAndUpdate(HtcAccountManager manager, Account account) {
        // Pass activity whenever possible.
        manager.getAuthToken(account,
                HtcAccountDefs.AUTHTOKEN_TYPE_DEFAULT, null,
                false, new AccountManagerCallback<Bundle>() {

                    @Override
                    public void run(AccountManagerFuture<Bundle> authTokenFuture) {
                        Bundle result;
                        try {
                            result = authTokenFuture.getResult();

                            mLogger.debugS("authToken result = ", result);

                            String accountId;
                            if (result.containsKey(HtcAccountDefs.KEY_ACCOUNT_ID)) {
                                accountId = result.getString(HtcAccountDefs.KEY_ACCOUNT_ID);
                            } else {
                                // The returned result is invalid.
                                throw new AuthenticatorException(
                                        "Unexpected result from getAuthToken().");
                            }

                            // save account id
                            PnsRecords records = PnsRecords.get(mAppContext);
                            String savedAccountId = records.getAccountId();
                            records.setAccountId(accountId);

                            if (savedAccountId != null
                                    && !savedAccountId.equalsIgnoreCase(accountId)) {
                                mLogger.info("Try update on initialization if user has already switched user.");
                                UpdateRegistrationService.startService(mAppContext,
                                        PnsInitializer.class.getSimpleName());
                            } else {
                                mLogger.info("User logged in the same account, do nothing");
                            }
                        } catch (Exception e) {
                            mLogger.error(e);
                        }
                    }
                }, null);
    }

    /**
     * The for push notification service library. Most app should use
     * {@link PnsInitializer} instead.
     *
     * @author ted_hsu@htc.com
     */
    public static class Builder {
        private HtcLogger mLogger = new PushLoggerFactory(this).create();
        private Context mAppContext = null;
        private RegistrationPolicy mRegistrationPolicy = null;
        private RetryPolicy mRetryPolicy = null;
        private boolean mAllowAlarms = true;
        private boolean mEnableRegisterService = true;
        private AbstractPnsConfigModel mPnsConfigModel = null;
        private boolean mStartRegistrationASAP = false;

        /**
         * Create a builder.
         * 
         * @param context Context to operate on.
         */
        public Builder(Context context) {
            if (context == null)
                throw new IllegalArgumentException("'context' is null.");

            mAppContext = context;
        }

        /**
         * Set the preferred RegistrationPolicy to use.
         * 
         * @param policy Preferred RegistrationPolicy.
         * @return The builder instance.
         */
        public Builder setRegistrationPolicy(RegistrationPolicy policy) {
            mLogger.verbose("policy=", policy);

            mRegistrationPolicy = policy;
            return this;
        }

        /**
         * Set the preferred RetryPolicy to use.
         * 
         * @param policy Preferred RetryPolicy.
         * @return The builder instance.
         */
        public Builder setRetryPolicy(RetryPolicy policy) {
            mLogger.verbose("policy=", policy);

            mRetryPolicy = policy;
            return this;
        }

        /**
         * @param pnsConfigModel Preferred PnsConfig to access push notification
         *            service
         * @return The builder instance
         */
        public Builder setPnsConfigModel(AbstractPnsConfigModel pnsConfigModel) {
            mLogger.verbose("pnsConfigModel=", pnsConfigModel);

            mPnsConfigModel = pnsConfigModel;
            return this;
        }

        /**
         * Enable Alarm
         * 
         * @param isAllowAlarm allow alarm or not.
         * @return The builder instance.
         */
        public Builder enableAlarm(boolean isAllowAlarm) {
            mLogger.verbose("isAllowAlarm=", isAllowAlarm);

            mAllowAlarms = isAllowAlarm;
            return this;
        }

        /**
         * Enable registration services
         * 
         * @param enableRegistrationService Enable registration services or not.
         * @return The builder instance.
         */
        public Builder enableRegistrationService(boolean enableRegistrationService) {
            mLogger.verbose("enableRegistrationService=", enableRegistrationService);

            mEnableRegisterService = enableRegistrationService;
            return this;
        }

        public Builder startRegistrationASAP() {
            mLogger.verbose("enableRegistrationService=true");
            mStartRegistrationASAP =true;
            return this;
        }

        /**
         * @return PnsInitializer instance
         */
        public PnsInitializer build() {
            if (mRegistrationPolicy == null) {
                mRegistrationPolicy = RegistrationPolicy.ALWAYS_REGISTER;
            }

            if (mRetryPolicy == null) {
                mRetryPolicy = new LibraryRetryPolicy(mAppContext);
            }

            if (mPnsConfigModel == null) {
                mPnsConfigModel = PnsConfigModelNoDMImpl.get(mAppContext);
            }

            PnsInitializer initializer = new PnsInitializer(mAppContext, mRegistrationPolicy, mRetryPolicy, mAllowAlarms,
                    mPnsConfigModel, mEnableRegisterService);
            initializer.mStartRegistrationASAP = mStartRegistrationASAP;
            return initializer;
        }
    }
}
