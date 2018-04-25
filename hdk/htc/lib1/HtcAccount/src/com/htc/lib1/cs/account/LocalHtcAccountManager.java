
package com.htc.lib1.cs.account;

import java.io.IOException;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;

import com.htc.lib0.htcdebugflag.HtcWrapHtcDebugFlag;
import com.htc.lib1.cs.auth.BuildConfig;
import com.htc.lib1.cs.account.DataServiceFuture.DataServiceCallable;
import com.htc.lib1.cs.account.HtcAccountManagerFuture.HtcAccountManagerCallable;
import com.htc.lib1.cs.account.LocalHtcAccountManagerDataService.DataServiceConnection;
import com.htc.lib1.cs.app.LocalBroadcastService;
import com.htc.lib1.cs.app.ProcessUtils;
import com.htc.lib1.cs.logging.CommLoggerFactory;
import com.htc.lib1.cs.logging.HtcLogger;
import com.htc.lib1.cs.workflow.AsyncWorkflowTask;

/**
 * Local accounts implementation of {@link HtcAccountManager}.
 * 
 * @author samael_wang@htc.com
 */
/* package */class LocalHtcAccountManager extends HtcAccountManager {
    private HtcLogger mLogger = new CommLoggerFactory(this).create();
    private Context mContext;
    private HtcAccountManagerDataSource mUnsafeDataSource;
    private boolean mIsAuthenticatorMode;

    /**
     * Create an instance.
     * 
     * @param context Context to operate on.
     * @param enableAuthenticatorMode True to enable authenticator features.
     * @hide
     */
    /* package */LocalHtcAccountManager(Context context, boolean enableAuthenticatorMode) {
        if (context == null)
            throw new IllegalArgumentException("'context' is null.");

        mContext = context;
        mUnsafeDataSource = HtcAccountManagerDataSource.get(mContext);
        mIsAuthenticatorMode = enableAuthenticatorMode;
    }

    /**
     * Create an instance for regular clients.
     * 
     * @param context Context to operate on.
     */
    public LocalHtcAccountManager(Context context) {
        this(context, false);
    }

    @Override
    public String getPassword(Account account) {
        throwIfNotAuthenticator();

        String password = mUnsafeDataSource.getPassword(account);
        return password;
    }

    @Override
    public String getUserData(Account account, String key) {
        throwIfNotAuthenticator();

        String value = mUnsafeDataSource.getUserData(account, key);
        return value;
    }

    @Override
    public Account[] getAccountsByType(String type) {
        Account[] accounts = mUnsafeDataSource.getAccounts(type);
        return accounts;
    }

    @Override
    public boolean addAccountExplicitly(Account account, String password, Bundle userdata) {
        throwIfNotAuthenticator();

        boolean result = mUnsafeDataSource.addAccount(account);
        if (result) {
            mUnsafeDataSource.setPassword(account, password);
            if (userdata != null && !userdata.isEmpty()) {
                for (String key : userdata.keySet()) {
                    mUnsafeDataSource.addUserData(account, key, String.valueOf(userdata.get(key)));
                }
            }
        }

        // Send account change intent.
        sendLocalBroadcast(new Intent(AccountManager.LOGIN_ACCOUNTS_CHANGED_ACTION));

        return result;
    }

    @Override
    public AccountManagerFuture<Boolean> removeAccount(final Account account,
            AccountManagerCallback<Boolean> callback, Handler handler) {
        // Construct the callable to work.
        HtcAccountManagerCallable<Boolean> callable = new HtcAccountManagerCallable<Boolean>() {

            @Override
            public Boolean call() throws IOException, AuthenticatorException,
                    OperationCanceledException {
                // Future task to check if the account can be removed.
                HtcAccountManagerFuture<Bundle> getAccountRemovalAllowedFuture =
                        new HtcAccountManagerFuture<Bundle>(mContext,
                                new LocalHtcAccountAuthenticatorSession(mContext, false) {

                                    @Override
                                    public void doWork() throws RemoteException {
                                        mAuthenticator.getAccountRemovalAllowed(this,
                                                new HtcAccount(account));
                                    }

                                }, null, null);

                // Get removal allowed.
                getAccountRemovalAllowedFuture.run();
                Bundle removalAllowedBundle = getAccountRemovalAllowedFuture.getResult();

                // Remove the account if allowed.
                if (removalAllowedBundle != null
                        && removalAllowedBundle.getBoolean(AccountManager.KEY_BOOLEAN_RESULT)) {
                    // Remove account.
                    DataServiceConnection conn = new DataServiceConnection(mContext);
                    try {
                        conn.bind().getRemoteDataSource().removeAccount(new HtcAccount(account));
                    } catch (RemoteException e) {
                        /*
                         * Since the data service runs in the main process of
                         * the calling app, it's unlikely that the process is
                         * dead while the calling process is alive. Hence the
                         * exception should usually caused by database operation
                         * failures. Convert to IOException here.
                         */
                        throw new IOException(e.getMessage(), e);
                    } finally {
                        conn.unbind();
                    }

                    // Send account change intent.
                    sendLocalBroadcast(new Intent(
                            AccountManager.LOGIN_ACCOUNTS_CHANGED_ACTION));

                    return true;
                } else {
                    mLogger.warning("The authenticator says removing account is not allowed.");
                    return false;
                }
            }
        };

        // Construct the future to encapsulate the callable.
        HtcAccountManagerFuture<Boolean> future =
                new HtcAccountManagerFuture<Boolean>(mContext, callable, callback, handler);
        AsyncWorkflowTask.THREAD_POOL_EXECUTOR.submit(future);
        return future;
    }

    @Override
    public void invalidateAuthToken(String accountType, String authToken) {

        Account[] accounts = mUnsafeDataSource.getAccounts(accountType);
        for (Account accnt : accounts) {
            mUnsafeDataSource.removeAuthToken(accnt, authToken);
        }

    }

    @Override
    public String peekAuthToken(Account account, String authTokenType) {
        throwIfNotAuthenticator();

        String authToken = mUnsafeDataSource.getAuthToken(account, authTokenType);
        return authToken;
    }

    @Override
    public void setPassword(Account account, String password) {
        throwIfNotAuthenticator();

        mUnsafeDataSource.setPassword(account, password);

        // Send account change intent.
        sendLocalBroadcast(new Intent(AccountManager.LOGIN_ACCOUNTS_CHANGED_ACTION));
    }

    @Override
    public void clearPassword(Account account) {
        setPassword(account, null);
    }

    @Override
    public void setUserData(Account account, String key, String value) {
        throwIfNotAuthenticator();
        mUnsafeDataSource.addUserData(account, key, value);
    }

    @Override
    public void setAuthToken(Account account, String authTokenType, String authToken) {
        throwIfNotAuthenticator();
        mUnsafeDataSource.setAuthToken(account, authTokenType, authToken);
    }

    @Override
    public AccountManagerFuture<Bundle> getAuthToken(final Account account,
            final String authTokenType, final Bundle options, Activity activity,
            AccountManagerCallback<Bundle> callback, Handler handler) {
        mLogger.verboseS("getAuthToken: ", account, ", authTokenType: ", authTokenType,
                ", options: ", options, ", activity: ", activity, ", callback: ", callback,
                ", handler: ", handler);

        // Construct the authenticator session.
        LocalHtcAccountAuthenticatorSession session = new LocalHtcAccountAuthenticatorSession(
                activity != null ? activity : mContext,
                activity != null) {

            @Override
            public void doWork() throws RemoteException {
                // Try local cache.
                DataServiceConnection conn = new DataServiceConnection(mContext);
                String authTokenCache = conn.bind().getRemoteDataSource()
                        .getAuthToken(new HtcAccount(account), authTokenType);
                conn.unbind();

                if (!TextUtils.isEmpty(authTokenCache)) {
                    Bundle result = new Bundle();
                    result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
                    result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
                    result.putString(AccountManager.KEY_AUTHTOKEN, authTokenCache);
                    onResult(result);
                } else {
                    // Ensure passing caller's package name.
                    Bundle authOptions = options != null ? options : new Bundle();
                    authOptions.putString(AccountManager.KEY_ANDROID_PACKAGE_NAME,
                            mContext.getPackageName());

                    mAuthenticator.getAuthToken(this, new HtcAccount(account), authTokenType,
                            authOptions);
                }

            }
        };

        // Construct the future to encapsulate the session.
        HtcAccountManagerFuture<Bundle> future =
                new HtcAccountManagerFuture<Bundle>(mContext, session, callback, handler);
        AsyncWorkflowTask.THREAD_POOL_EXECUTOR.submit(future);
        return future;
    }

    @Override
    public AccountManagerFuture<Bundle> getAuthToken(final Account account,
            final String authTokenType, final Bundle options, boolean notifyAuthFailure,
            AccountManagerCallback<Bundle> callback, Handler handler) {
        mLogger.verboseS("getAuthToken: ", account, ", authTokenType: ", authTokenType,
                ", options: ", options, ", notifyAuthFailure: ", notifyAuthFailure, ", callback: ",
                callback, ", handler: ", handler);

        // Construct the authenticator session.
        LocalHtcAccountAuthenticatorSession session = new LocalHtcAccountAuthenticatorSession(
                mContext, false) {

            @Override
            public void doWork() throws RemoteException {
                // Try local cache.
                DataServiceConnection conn = new DataServiceConnection(mContext);
                String authTokenCache = conn.bind().getRemoteDataSource()
                        .getAuthToken(new HtcAccount(account), authTokenType);
                conn.unbind();

                if (!TextUtils.isEmpty(authTokenCache)) {
                    Bundle result = new Bundle();
                    result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
                    result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
                    result.putString(AccountManager.KEY_AUTHTOKEN, authTokenCache);
                    onResult(result);
                } else {
                    // Ensure passing caller's package name.
                    Bundle authOptions = options != null ? options : new Bundle();
                    authOptions.putString(AccountManager.KEY_ANDROID_PACKAGE_NAME,
                            mContext.getPackageName());

                    mAuthenticator.getAuthToken(this, new HtcAccount(account), authTokenType,
                            authOptions);
                }

            }
        };

        // Construct the future to encapsulate the session.
        HtcAccountManagerFuture<Bundle> future =
                new HtcAccountManagerFuture<Bundle>(mContext, session, callback, handler);
        AsyncWorkflowTask.THREAD_POOL_EXECUTOR.submit(future);
        return future;
    }

    @Override
    public AccountManagerFuture<Bundle> addAccount(final String accountType,
            final String authTokenType, final String[] requiredFeatures,
            final Bundle addAccountOptions, Activity activity,
            AccountManagerCallback<Bundle> callback, Handler handler) {
        // Construct the authenticator session.
        LocalHtcAccountAuthenticatorSession session = new LocalHtcAccountAuthenticatorSession(
                activity != null ? activity : mContext,
                activity != null) {

            @Override
            public void doWork() throws RemoteException {
                // Ensure passing caller's package name.
                Bundle options = addAccountOptions != null ? addAccountOptions : new Bundle();
                options.putString(AccountManager.KEY_ANDROID_PACKAGE_NAME,
                        mContext.getPackageName());

                mAuthenticator.addAccount(this, accountType, authTokenType, requiredFeatures,
                        options);
            }
        };

        // Construct the future to encapsulate the session.
        HtcAccountManagerFuture<Bundle> future =
                new HtcAccountManagerFuture<Bundle>(mContext, session, callback, handler);
        AsyncWorkflowTask.THREAD_POOL_EXECUTOR.submit(future);
        return future;
    }

    @Override
    public AccountManagerFuture<Bundle> confirmCredentials(final Account account,
            final Bundle options, Activity activity, AccountManagerCallback<Bundle> callback,
            Handler handler) {
        // Construct the authenticator session.
        LocalHtcAccountAuthenticatorSession session = new LocalHtcAccountAuthenticatorSession(
                activity != null ? activity : mContext, activity != null) {

            @Override
            public void doWork() throws RemoteException {
                mAuthenticator.confirmCredentials(this, new HtcAccount(account), options);
            }
        };

        // Construct the future to encapsulate the session.
        HtcAccountManagerFuture<Bundle> future =
                new HtcAccountManagerFuture<Bundle>(mContext, session, callback, handler);
        AsyncWorkflowTask.THREAD_POOL_EXECUTOR.submit(future);
        return future;
    }

    @Override
    public AccountManagerFuture<Bundle> updateCredentials(final Account account,
            final String authTokenType, final Bundle options, Activity activity,
            AccountManagerCallback<Bundle> callback, Handler handler) {
        // Construct the authenticator session.
        LocalHtcAccountAuthenticatorSession session = new LocalHtcAccountAuthenticatorSession(
                activity != null ? activity : mContext,
                activity != null) {

            @Override
            public void doWork() throws RemoteException {
                mAuthenticator.updateCredentials(this, new HtcAccount(account), authTokenType,
                        options);
            }
        };

        // Construct the future to encapsulate the session.
        HtcAccountManagerFuture<Bundle> future =
                new HtcAccountManagerFuture<Bundle>(mContext, session, callback, handler);
        AsyncWorkflowTask.THREAD_POOL_EXECUTOR.submit(future);
        return future;
    }

    @Override
    public String getType() {
        return HtcAccountManagerTypes.TYPE_LOCAL;
    }

    @Override
    public DataServiceFuture<String> asyncGetPassword(final Account account,
            DataServiceFuture.DataServiceCallback<String> callback, Handler handler) {
        throwIfNotAuthenticator();

        // Construct the callable to do the work.
        DataServiceCallable<String> callable = new DataServiceCallable<String>() {

            @Override
            public String call() throws IOException {
                DataServiceConnection conn = new DataServiceConnection(mContext);
                String password;
                try {
                    password = conn.bind().getRemoteDataSource()
                            .getPassword(new HtcAccount(account));
                } catch (RemoteException e) {
                    throw new IOException(e.getMessage(), e);
                } finally {
                    conn.unbind();
                }

                return password;
            }

        };

        // Construct the future to encapsulate the callable.
        DataServiceFutureImpl<String> future = new DataServiceFutureImpl<String>(
                mContext, callable, callback, handler);
        AsyncWorkflowTask.THREAD_POOL_EXECUTOR.submit(future);
        return future;
    }

    @Override
    public DataServiceFuture<String> asyncGetUserData(final Account account,
            final String key,
            DataServiceFuture.DataServiceCallback<String> callback, Handler handler) {
        // Construct the callable to do the work.
        DataServiceCallable<String> callable = new DataServiceCallable<String>() {

            @Override
            public String call() throws IOException {
                DataServiceConnection conn = new DataServiceConnection(mContext);
                String value;
                try {
                    value = conn.bind().getRemoteDataSource()
                            .getUserData(new HtcAccount(account), key);
                } catch (RemoteException e) {
                    throw new IOException(e.getMessage(), e);
                } finally {
                    conn.unbind();
                }

                return value;
            }
        };

        // Construct the future to encapsulate the callable.
        DataServiceFutureImpl<String> future = new DataServiceFutureImpl<String>(
                mContext, callable, callback, handler);
        AsyncWorkflowTask.THREAD_POOL_EXECUTOR.submit(future);
        return future;
    }

    @Override
    public DataServiceFuture<Account[]> asyncGetAccountsByType(final String type,
            DataServiceFuture.DataServiceCallback<Account[]> callback, Handler handler) {
        // Construct the callable to do the work.
        DataServiceCallable<Account[]> callable = new DataServiceCallable<Account[]>() {

            @Override
            public Account[] call() throws IOException {
                DataServiceConnection conn = new DataServiceConnection(mContext);
                HtcAccount[] htcAccounts;
                try {
                    htcAccounts = conn.bind().getRemoteDataSource().getAccounts(type);
                } catch (RemoteException e) {
                    throw new IOException(e.getMessage(), e);
                } finally {
                    conn.unbind();
                }

                Account[] accounts = new Account[htcAccounts.length];
                for (int i = 0; i < htcAccounts.length; i++) {
                    accounts[i] = htcAccounts[i].toAccount();
                }

                return accounts;
            }
        };

        // Construct the future to encapsulate the callable.
        DataServiceFutureImpl<Account[]> future = new DataServiceFutureImpl<Account[]>(
                mContext, callable, callback, handler);
        AsyncWorkflowTask.THREAD_POOL_EXECUTOR.submit(future);
        return future;
    }

    @Override
    public DataServiceFuture<Void> asyncInvalidateAuthToken(final String accountType,
            final String authToken, DataServiceFuture.DataServiceCallback<Void> callback,
            Handler handler) {
        // Construct the callable to do the work.
        DataServiceCallable<Void> callable = new DataServiceCallable<Void>() {

            @Override
            public Void call() throws IOException {
                DataServiceConnection conn = new DataServiceConnection(mContext);
                IHtcAccountManagerDataSource remoteDataSource;

                try {
                    remoteDataSource = conn.bind().getRemoteDataSource();
                } catch (RemoteException e) {
                    // Binding failure.
                    throw new IOException(e.getMessage(), e);
                }

                HtcAccount[] accounts;
                try {
                    accounts = remoteDataSource.getAccounts(accountType);
                    for (HtcAccount accnt : accounts) {
                        remoteDataSource.removeAuthToken(remoteDataSource.getId(accnt), authToken);
                    }
                } catch (RemoteException e) {
                    throw new IOException(e.getMessage(), e);
                } finally {
                    conn.unbind();
                }

                return null;
            }

        };

        // Construct the future to encapsulate the callable.
        DataServiceFutureImpl<Void> future = new DataServiceFutureImpl<Void>(mContext,
                callable, callback, handler);
        AsyncWorkflowTask.THREAD_POOL_EXECUTOR.submit(future);
        return future;
    }

    @Override
    public DataServiceFuture<String> asyncPeekAuthToken(final Account account,
            final String authTokenType, DataServiceFuture.DataServiceCallback<String> callback,
            Handler handler) {
        throwIfNotAuthenticator();

        // Construct the callable to do the work.
        DataServiceCallable<String> callable = new DataServiceCallable<String>() {

            @Override
            public String call() throws IOException {
                DataServiceConnection conn = new DataServiceConnection(mContext);
                String value;
                try {
                    value = conn.bind().getRemoteDataSource()
                            .getAuthToken(new HtcAccount(account), authTokenType);
                } catch (RemoteException e) {
                    throw new IOException(e.getMessage(), e);
                } finally {
                    conn.unbind();
                }

                return value;
            }
        };

        // Construct the future to encapsulate the callable.
        DataServiceFutureImpl<String> future = new DataServiceFutureImpl<String>(
                mContext, callable, callback, handler);
        AsyncWorkflowTask.THREAD_POOL_EXECUTOR.submit(future);
        return future;
    }

    @Override
    public DataServiceFuture<Void> asyncSetPassword(final Account account,
            final String password,
            DataServiceFuture.DataServiceCallback<Void> callback, Handler handler) {
        throwIfNotAuthenticator();

        // Construct the callable to do the work.
        DataServiceCallable<Void> callable = new DataServiceCallable<Void>() {

            @Override
            public Void call() throws IOException {
                DataServiceConnection conn = new DataServiceConnection(mContext);
                try {
                    conn.bind().getRemoteDataSource()
                            .setPassword(new HtcAccount(account), password);
                } catch (RemoteException e) {
                    throw new IOException(e.getMessage(), e);
                } finally {
                    conn.unbind();
                }

                // Send account change intent.
                sendLocalBroadcast(new Intent(AccountManager.LOGIN_ACCOUNTS_CHANGED_ACTION));

                return null;
            }

        };

        // Construct the future to encapsulate the callable.
        DataServiceFutureImpl<Void> future = new DataServiceFutureImpl<Void>(mContext,
                callable, callback, handler);
        AsyncWorkflowTask.THREAD_POOL_EXECUTOR.submit(future);
        return future;
    }

    @Override
    public DataServiceFuture<Void> asyncClearPassword(Account account,
            DataServiceFuture.DataServiceCallback<Void> callback, Handler handler) {
        return asyncSetPassword(account, null, callback, handler);
    }

    @Override
    public DataServiceFuture<Void> asyncSetUserData(final Account account,
            final String key,
            final String value, DataServiceFuture.DataServiceCallback<Void> callback,
            Handler handler) {
        // Construct the callable to do the work.
        DataServiceCallable<Void> callable = new DataServiceCallable<Void>() {

            @Override
            public Void call() throws IOException {
                DataServiceConnection conn = new DataServiceConnection(mContext);
                try {
                    conn.bind().getRemoteDataSource()
                            .addUserData(new HtcAccount(account), key, value);
                } catch (RemoteException e) {
                    throw new IOException(e.getMessage(), e);
                } finally {
                    conn.unbind();
                }

                return null;
            }

        };

        // Construct the future to encapsulate the callable.
        DataServiceFutureImpl<Void> future = new DataServiceFutureImpl<Void>(mContext,
                callable, callback, handler);
        AsyncWorkflowTask.THREAD_POOL_EXECUTOR.submit(future);
        return future;
    }

    @Override
    public DataServiceFuture<Void> asyncSetAuthToken(final Account account,
            final String authTokenType,
            final String authToken, DataServiceFuture.DataServiceCallback<Void> callback,
            Handler handler) {
        // Construct the callable to do the work.
        DataServiceCallable<Void> callable = new DataServiceCallable<Void>() {

            @Override
            public Void call() throws IOException {
                DataServiceConnection conn = new DataServiceConnection(mContext);
                try {
                    conn.bind().getRemoteDataSource()
                            .setAuthToken(new HtcAccount(account), authTokenType, authToken);
                } catch (RemoteException e) {
                    throw new IOException(e.getMessage(), e);
                } finally {
                    conn.unbind();
                }

                return null;
            }

        };

        // Construct the future to encapsulate the callable.
        DataServiceFutureImpl<Void> future = new DataServiceFutureImpl<Void>(
                mContext, callable, callback, handler);
        AsyncWorkflowTask.THREAD_POOL_EXECUTOR.submit(future);
        return future;
    }

    @Override
    public DataServiceFuture<Boolean> asyncAddAccountExplicitly(final Account account,
            final String password, final Bundle userdata,
            DataServiceFuture.DataServiceCallback<Boolean> callback,
            Handler handler) {
        throwIfNotAuthenticator();

        // Construct the callable to do the work.
        DataServiceCallable<Boolean> callable = new DataServiceCallable<Boolean>() {

            @Override
            public Boolean call() throws IOException {
                DataServiceConnection conn = new DataServiceConnection(mContext);
                IHtcAccountManagerDataSource remoteDataSource;

                try {
                    remoteDataSource = conn.bind().getRemoteDataSource();
                } catch (RemoteException e) {
                    // Binding failure.
                    throw new IOException(e.getMessage(), e);
                }

                boolean result;
                HtcAccount htcAccount = new HtcAccount(account);
                try {
                    result = remoteDataSource.addAccount(htcAccount);
                    if (result) {
                        remoteDataSource.setPassword(htcAccount, password);
                        if (userdata != null && !userdata.isEmpty()) {
                            for (String key : userdata.keySet()) {
                                remoteDataSource.addUserData(htcAccount, key,
                                        String.valueOf(userdata.get(key)));
                            }
                        }
                    }
                } catch (RemoteException e) {
                    throw new IOException(e.getMessage(), e);
                } finally {
                    conn.unbind();
                }

                // Send account change intent.
                sendLocalBroadcast(new Intent(AccountManager.LOGIN_ACCOUNTS_CHANGED_ACTION));

                return result;
            }

        };

        // Construct the future to encapsulate the callable.
        DataServiceFutureImpl<Boolean> future = new DataServiceFutureImpl<Boolean>(
                mContext, callable, callback,
                handler);
        AsyncWorkflowTask.THREAD_POOL_EXECUTOR.submit(future);
        return future;
    }

    /**
     * Send local broadcast.
     * 
     * @param intent
     */
    private void sendLocalBroadcast(Intent intent) {
        // Debug resolution only if one of the debug flags is true.
        if (HtcWrapHtcDebugFlag.Htc_SECURITY_DEBUG_flag
                || HtcWrapHtcDebugFlag.Htc_DEBUG_flag
                || BuildConfig.DEBUG) {
            intent.addFlags(Intent.FLAG_DEBUG_LOG_RESOLUTION);
        }

        if (ProcessUtils.isMainProcess(mContext)) {
            // We're working on main process. Send the local broadcast directly.
            mLogger.verbose("broadcast delivered: ",
                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent));
        } else {
            /*
             * We're not working on main process. Let the service working on
             * main process to send the broadcast.
             */
            Intent serviceIntent = new Intent(mContext, LocalBroadcastService.class);
            serviceIntent.putExtra(LocalBroadcastService.KEY_BROADCAST_INTENT, intent);
            mContext.startService(serviceIntent);
        }
    }

    /**
     * Throws a runtime exception if calling authenticator method from
     * non-authenticator-mode callers.
     */
    private void throwIfNotAuthenticator() {
        if (!mIsAuthenticatorMode)
            throw new IllegalStateException(
                    "The method is intented to be used by authenticators only.");
    }

    @Override
    public void registerReceiver(BroadcastReceiver receiver) {
        if (!ProcessUtils.isMainProcess(mContext))
            throw new IllegalStateException(
                    "The receiver should only be registered on main process.");

        IntentFilter filter = new IntentFilter();
        filter.addAction(HtcAccountBroadcasts.ACTION_ADD_ACCOUNT_COMPLETED);
        filter.addAction(HtcAccountBroadcasts.ACTION_ADD_ACCOUNT_CANCELED);
        filter.addAction(HtcAccountBroadcasts.ACTION_AUTH_TOKEN_RENEWED);
        filter.addAction(HtcAccountBroadcasts.ACTION_REMOVE_ACCOUNT_COMPLETED);

        LocalBroadcastManager.getInstance(mContext).registerReceiver(receiver,
                filter);
    }

    @Override
    public String getAccountAppVersionName() {
        // Not available
        return VERSION_NOT_AVAILABLE;
    }
}
