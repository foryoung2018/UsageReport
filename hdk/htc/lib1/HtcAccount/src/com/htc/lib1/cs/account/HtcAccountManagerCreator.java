
package com.htc.lib1.cs.account;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;

import com.htc.lib1.cs.account.DataServiceFuture.DataServiceCallable;
import com.htc.lib1.cs.account.HtcAccountManager.HtcAccountManagerTypes;
import com.htc.lib1.cs.account.LocalHtcAccountManagerDataService.DataServiceConnection;
import com.htc.lib1.cs.logging.CommLoggerFactory;
import com.htc.lib1.cs.logging.HtcLogger;
import com.htc.lib1.cs.workflow.AsyncWorkflowTask;

/**
 * Creator to create proper {@link HtcAccountManager} instances for integrated
 * clients and authenticator itself according to application and system configs.
 * 
 * @author samael_wang@htc.com
 */
public class HtcAccountManagerCreator {
    /**
     * Interface of creation policy used in
     * {@link HtcAccountManagerCreator#create(Context)} and
     * {@link HtcAccountManagerCreator#asyncCreate(Context, com.htc.lib1.cs.account.DataServiceFuture.DataServiceCallback, Handler)}
     * .
     * 
     * @author samael_wang@htc.com
     */
    public interface CreationPolicy {

        /**
         * Get the type of {@link HtcAccountManager} to create. The method is
         * only invoked at the first call to
         * {@link HtcAccountManagerCreator#create(Context)} or
         * {@link HtcAccountManagerCreator#asyncCreate(Context, com.htc.lib1.cs.account.DataServiceFuture.DataServiceCallback, Handler)}
         * . The result is then cached during the lifetime of
         * {@link HtcAccountManagerCreator} unless
         * {@link HtcAccountManagerCreator#setCreationPolicy(CreationPolicy)} is
         * invoked again.
         * 
         * @param context Context to operate on.
         * @param hasLocalAccount {@code true} if an HTC Account presents in
         *            local database (i.e. user has already signed in through
         *            the WebView implementation before).
         * @return {@link HtcAccountManagerTypes#TYPE_LOCAL} or
         *         {@link HtcAccountManagerTypes#TYPE_SYSTEM}.
         */
        public String getClientAccountManagerTypeToUse(Context context, boolean hasLocalAccount);
    }

    private static HtcAccountManagerCreator sInstance;
    private HtcLogger mLogger = new CommLoggerFactory(this).create();
    private CreationPolicy mCreationPolicy = new DefaultCreationPolicy();
    private String mAuthenticatorAccountManagerTypeToUse;
    private String mClientAccountManagerTypeToUse;

    /**
     * Get the singleton instance.
     * 
     * @return {@link HtcAccountManagerCreator}
     */
    public static synchronized HtcAccountManagerCreator get() {
        if (sInstance == null)
            sInstance = new HtcAccountManagerCreator();

        return sInstance;
    }

    // Private constructor.
    private HtcAccountManagerCreator() {
    }

    /**
     * Specify the policy to create {@link HtcAccountManager} when calling
     * {@link #create(Context)} or
     * {@link #asyncCreate(Context, com.htc.lib1.cs.account.DataServiceFuture.DataServiceCallback, Handler)}
     * .
     * <p>
     * If {@link #create(Context)} or
     * {@link #asyncCreate(Context, com.htc.lib1.cs.account.DataServiceFuture.DataServiceCallback, Handler)}
     * is invoked before, the generated cache from previous used
     * {@link CreationPolicy} will be cleared, and the next call to
     * {@link #create(Context)} or
     * {@link #asyncCreate(Context, com.htc.lib1.cs.account.DataServiceFuture.DataServiceCallback, Handler)}
     * will use the {@code policy} set here.
     * <p>
     * If your app operates HTC Account on multiple processes, ensure setting
     * the same policy on each process so the behaviors are consistent across
     * sub-processes.
     * 
     * @param policy An instance of {@link CreationPolicy}. Must not be
     *            {@code null}.
     */
    public synchronized void setCreationPolicy(CreationPolicy policy) {
        if (policy == null)
            throw new IllegalArgumentException("'policy' is null.");

        mLogger.verbose(policy.getClass().getSimpleName());

        // Set the policy and reset cache.
        mCreationPolicy = policy;
        mClientAccountManagerTypeToUse = null;
    }

    /**
     * Create an {@link HtcAccountManager} instance for clients. By default, it
     * uses {@link DefaultCreationPolicy} to decide what kind of types to
     * create. If the default creation policy doesn't fulfill the requirement of
     * your app, create your own {@link CreationPolicy} and set the policy
     * through {@link #setCreationPolicy(CreationPolicy)} before invoking
     * {@link #create(Context)} or
     * {@link #asyncCreate(Context, com.htc.lib1.cs.account.DataServiceFuture.DataServiceCallback, Handler)}
     * .
     * <p>
     * This method can be invoked from the main thread. However, it involves
     * database operations, and SQLite doesn't support simultaneous accessing
     * from multiple processes. Hence, if responsiveness is critical for your
     * app, or your app needs to call this method in a sub-process, use
     * {@link #asyncCreate(Context, com.htc.lib1.cs.account.DataServiceFuture.DataServiceCallback, Handler)}
     * instead.
     * 
     * @param context Context to operate on.
     * @return {@link HtcAccountManager}
     */
    public HtcAccountManager create(Context context) {
        if (context == null)
            throw new IllegalArgumentException("'context' is null.");

        // Check the type cache.
        if (mClientAccountManagerTypeToUse == null) {

            // Check if local account presents.
            boolean hasLocalAccount;
            try {
                hasLocalAccount = HtcAccountManagerDataSource.get(context).getAccounts(
                        HtcAccountDefs.TYPE_HTC_ACCOUNT).length > 0;
            } catch (Exception e) {
                /*
                 * If somehow the database access failed, ignore it and treat as
                 * no existing local accounts.
                 */
                mLogger.error(e);
                hasLocalAccount = false;
            }

            // Use the creation policy to set the type.
            String className = mCreationPolicy.getClass().getSimpleName();
            mLogger.debug("Calling '", !TextUtils.isEmpty(className) ? className : "Anonymous",
                    "' to find the proper type to use.");

            /*
             * Set account manager type. Use double-checked locking to avoid
             * deadlock.
             */
            synchronized (this) {
                if (mClientAccountManagerTypeToUse == null) {
                    mClientAccountManagerTypeToUse = mCreationPolicy
                            .getClientAccountManagerTypeToUse(context, hasLocalAccount);
                }
            }
        }

        // Create instance.
        return create(context, mClientAccountManagerTypeToUse);
    }

    /**
     * Asynchronized implementation of {@link #create(Context)} which guarantees
     * the database operation is in a background thread on main process no
     * matter what the calling process / thread is.
     * <p>
     * This method may be called from any thread, but the returned
     * {@link DataServiceFuture} must not be used on the main thread.
     * 
     * @param context Context to operate on.
     * @param callback Callback to invoke when the request completes, null for
     *            no callback
     * @param handler {@link Handler} identifying the callback thread, null for
     *            the main thread
     * @return An {@link DataServiceFuture} which resolves to an
     *         {@link HtcAccountManager}.
     */
    public DataServiceFuture<HtcAccountManager> asyncCreate(final Context context,
            final DataServiceFuture.DataServiceCallback<HtcAccountManager> callback,
            final Handler handler) {
        if (context == null)
            throw new IllegalArgumentException("'context' is null.");

        // Construct the callable to do the work.
        DataServiceCallable<HtcAccountManager> callable = new DataServiceCallable<HtcAccountManager>() {

            @Override
            public HtcAccountManager call() {
                // Check the type cache.
                if (mClientAccountManagerTypeToUse == null) {

                    // Check if local account presents.
                    boolean hasLocalAccount;
                    DataServiceConnection conn = new DataServiceConnection(context);
                    try {
                        hasLocalAccount = conn.bind().getRemoteDataSource()
                                .getAccounts(HtcAccountDefs.TYPE_HTC_ACCOUNT).length > 0;
                    } catch (Exception e) {
                        /*
                         * If somehow the remote access failed, ignore it and
                         * treat as no existing local accounts.
                         */
                        mLogger.error(e);
                        hasLocalAccount = false;
                    } finally {
                        conn.unbind();
                    }

                    // Use the creation policy to set the type.
                    mLogger.debug("Calling '", mCreationPolicy.getClass().getSimpleName(),
                            "' to find the proper type to use.");

                    /*
                     * Set account manager type. Use double-checked locking to
                     * avoid deadlock.
                     */
                    synchronized (HtcAccountManagerCreator.this) {
                        if (mClientAccountManagerTypeToUse == null) {
                            mClientAccountManagerTypeToUse = mCreationPolicy
                                    .getClientAccountManagerTypeToUse(context, hasLocalAccount);
                        }
                    }
                }

                // Create instance.
                return create(context, mClientAccountManagerTypeToUse);
            }
        };

        // Construct the future to encapsulate the callable.
        DataServiceFutureImpl<HtcAccountManager> future = new DataServiceFutureImpl<HtcAccountManager>(
                context, callable, callback, handler);
        AsyncWorkflowTask.THREAD_POOL_EXECUTOR.submit(future);
        return future;
    }

    /**
     * Create an {@link HtcAccountManager} instance for clients according to the
     * type given.
     * 
     * @param context Context to operate on.
     * @param type {@link HtcAccountManagerTypes#TYPE_LOCAL} or
     *            {@link HtcAccountManagerTypes#TYPE_SYSTEM}.
     * @return {@link HtcAccountManager}
     */
    private HtcAccountManager create(Context context, String type) {
        if (context == null)
            throw new IllegalArgumentException("'context' is null.");
        if (TextUtils.isEmpty(type))
            throw new IllegalArgumentException("'type' is null or empty.");

        // Create HtcAccountManager.
        if (HtcAccountManagerTypes.TYPE_SYSTEM.equals(type)) {
            mLogger.debug("Create SystemHtcAccountManager.");
            return new SystemHtcAccountManager(context);
        } else if (HtcAccountManagerTypes.TYPE_LOCAL.equals(type)) {
            mLogger.debug("Create LocalHtcAccountManager.");
            return new LocalHtcAccountManager(context);
        } else {
            throw new IllegalArgumentException("Unrecognized type '" + type + "'");
        }
    }

    /**
     * Create an {@link HtcAccountManager} instance for the authenticator to use
     * according to application configurations. The creation policy of this
     * method is not configurable and is intended for use by authenticator, not
     * directly by applications.
     * 
     * @param context Context to operate on.
     * @return {@link HtcAccountManager}
     */
    public synchronized HtcAccountManager createAsAuthenticator(Context context) {
        if (context == null)
            throw new IllegalArgumentException("'context' is null.");

        // Check authenticator type only on the first call.
        if (mAuthenticatorAccountManagerTypeToUse == null) {
            final SystemAuthenticatorHelper helper = SystemAuthenticatorHelper.get(context);
            mAuthenticatorAccountManagerTypeToUse =
                    helper.isSystemAuthenticator(HtcAccountDefs.TYPE_HTC_ACCOUNT) ?
                            HtcAccountManagerTypes.TYPE_SYSTEM :
                            HtcAccountManagerTypes.TYPE_LOCAL;
        }

        // Create HtcAccountManager.
        if (HtcAccountManagerTypes.TYPE_SYSTEM.equals(mAuthenticatorAccountManagerTypeToUse)) {
            mLogger.debug("Create SystemHtcAccountManager for authenticator.");
            return new SystemHtcAccountManager(context);
        } else {
            mLogger.debug("Create LocalHtcAccountManager for authenticator.");
            return new LocalHtcAccountManager(context, true);
        }
    }

    /**
     * The default creation policy to use. It checks the following conditions in
     * order:
     * <p>
     * 1. If local account presents (i.e. user has already signed in through the
     * WebView implementation before), always use
     * {@link HtcAccountManagerTypes#TYPE_LOCAL} no matter system authenticator
     * presents or not.<br>
     * 2. If system authenticator presents, use
     * {@link HtcAccountManagerTypes#TYPE_SYSTEM}; otherwise, use
     * {@link HtcAccountManagerTypes#TYPE_LOCAL}.
     * 
     * @author samael_wang@htc.com
     */
    public static class DefaultCreationPolicy implements CreationPolicy {
        private HtcLogger mmLogger = new CommLoggerFactory(this).create();
        private String mmClientAccountManagerTypeToUse;

        @Override
        public synchronized String getClientAccountManagerTypeToUse(Context context,
                boolean hasLocalAccount) {
            if (mmClientAccountManagerTypeToUse == null) {
                // Always use TYPE_LOCAL if local account exists.
                if (hasLocalAccount) {
                    mmLogger.info("Local HTC Account exists in database. Use TYPE_LOCAL.");
                    return mmClientAccountManagerTypeToUse = HtcAccountManagerTypes.TYPE_LOCAL;
                }

                // Check the existence of system authenticator.
                String authenticator = SystemAuthenticatorHelper.get(context)
                        .getSystemAuthenticator(HtcAccountDefs.TYPE_HTC_ACCOUNT);

                if (HtcAccountDefs.PKG_NAME_LEGACY_IDENTITY_CLIENT.equals(authenticator)) {
                    mmLogger.info("Legacy identity client detected. Use TYPE_SYSTEM");
                    mmClientAccountManagerTypeToUse = HtcAccountManagerTypes.TYPE_SYSTEM;
                } else if (HtcAccountDefs.PKG_NAME_IDENTITY_CLIENT.equals(authenticator)) {
                    mmLogger.info("Identity client detected. Use TYPE_SYSTEM.");
                    mmClientAccountManagerTypeToUse = HtcAccountManagerTypes.TYPE_SYSTEM;
                } else { // Authenticator not exists, or package not recognized.
                    mmLogger.debug("Identity client doesn't exist. Use TYPE_LOCAL.");
                    mmClientAccountManagerTypeToUse = HtcAccountManagerTypes.TYPE_LOCAL;
                }
            }

            return mmClientAccountManagerTypeToUse;
        }
    }

    /**
     * {@link SystemOnlyCreationPolicy} always uses
     * {@link HtcAccountManagerTypes#TYPE_SYSTEM}.
     * 
     * @author samael_wang
     */
    public static class SystemOnlyCreationPolicy implements CreationPolicy {

        @Override
        public String getClientAccountManagerTypeToUse(Context context, boolean hasLocalAccount) {
            return HtcAccountManagerTypes.TYPE_SYSTEM;
        }

    }

    /**
     * {@link LocalOnlyCreationPolicy} always uses
     * {@link HtcAccountManagerTypes#TYPE_LOCAL}.
     * 
     * @author samael_wang
     */
    public static class LocalOnlyCreationPolicy implements CreationPolicy {

        @Override
        public String getClientAccountManagerTypeToUse(Context context, boolean hasLocalAccount) {
            return HtcAccountManagerTypes.TYPE_LOCAL;
        }

    }

}
