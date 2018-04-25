
package com.htc.lib1.cs.account;

import java.io.IOException;
import java.util.List;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.text.TextUtils;

import com.htc.lib1.cs.account.DataServiceFuture.DataServiceCallable;
import com.htc.lib1.cs.account.HtcAccountManagerFuture.HtcAccountManagerCallable;
import com.htc.lib1.cs.account.LocalHtcAccountManagerDataService.DataServiceConnection;
import com.htc.lib1.cs.account.OAuth2RestServiceException.OAuth2InvalidAuthKeyException;
import com.htc.lib1.cs.app.ApplicationInfoHelper;
import com.htc.lib1.cs.app.ProcessUtils;
import com.htc.lib1.cs.httpclient.ConnectionException;
import com.htc.lib1.cs.httpclient.ConnectivityException;
import com.htc.lib1.cs.httpclient.HttpException;
import com.htc.lib1.cs.logging.CommLoggerFactory;
import com.htc.lib1.cs.logging.HtcLogger;
import com.htc.lib1.cs.workflow.AsyncWorkflowTask;

/**
 * System accounts implementation of {@link HtcAccountManager}.
 * 
 * @author samael_wang@htc.com
 */
/* package */class SystemHtcAccountManager extends HtcAccountManager {
    private HtcLogger mLogger = new CommLoggerFactory(this).create();
    private Context mContext;
    private AccountManager mAccntManager;

    /**
     * Create an instance.
     * 
     * @param context Context to operate on.
     */
    public SystemHtcAccountManager(Context context) {
        if (context == null)
            throw new IllegalArgumentException("'context' is null.");

        mContext = context;
        mAccntManager = AccountManager.get(context);
    }

    @Override
    public String getPassword(Account account) {
        return mAccntManager.getPassword(account);
    }

    @Override
    public String getUserData(Account account, String key) {
        return mAccntManager.getUserData(account, key);
    }

    @Override
    public Account[] getAccountsByType(String type) {
        return mAccntManager.getAccountsByType(type);
    }

    @Override
    public boolean addAccountExplicitly(Account account, String password, Bundle userdata) {
        return mAccntManager.addAccountExplicitly(account, password, userdata);
    }

    @Override
    public AccountManagerFuture<Boolean> removeAccount(Account account,
            AccountManagerCallback<Boolean> callback, Handler handler) {
        return mAccntManager.removeAccount(account, callback, handler);
    }

    @Override
    public void invalidateAuthToken(String accountType, String authToken) {
        mAccntManager.invalidateAuthToken(accountType, authToken);
    }

    @Override
    public String peekAuthToken(Account account, String authTokenType) {
        return mAccntManager.peekAuthToken(account, authTokenType);
    }

    @Override
    public void setPassword(Account account, String password) {
        mAccntManager.setPassword(account, password);
    }

    @Override
    public void clearPassword(Account account) {
        mAccntManager.clearPassword(account);
    }

    @Override
    public void setUserData(Account account, String key, String value) {
        mAccntManager.setUserData(account, key, value);
    }

    @Override
    public void setAuthToken(Account account, String authTokenType, String authToken) {
        mAccntManager.setAuthToken(account, authTokenType, authToken);
    }

    @Override
    public AccountManagerFuture<Bundle> getAuthToken(final Account account,
            final String authTokenType, final Bundle options, final Activity activity,
            AccountManagerCallback<Bundle> callback, Handler handler) {
        // Get the account manager future.
        AccountManagerFuture<Bundle> accountManagerFuture = mAccntManager.getAuthToken(account,
                authTokenType, options, activity, null, null);

        // Wrap it with a callable to append account id if necessary.
        CustomizibleAccountManagerCallable callable = new CustomizibleAccountManagerCallable(
                accountManagerFuture) {

            @Override
            protected Bundle customizeResult(Bundle result) throws IOException,
                    AuthenticatorException, OperationCanceledException {
                return blockingAppendAccountId(account, authTokenType, options, activity, result);
            }

        };

        // Return the future with the wrapping callable.
        HtcAccountManagerFuture<Bundle> future = new HtcAccountManagerFuture<Bundle>(mContext,
                callable, callback, handler);
        AsyncWorkflowTask.THREAD_POOL_EXECUTOR.submit(future);
        return future;
    }

    @Override
    public AccountManagerFuture<Bundle> getAuthToken(final Account account,
            final String authTokenType, final Bundle options, final boolean notifyAuthFailure,
            AccountManagerCallback<Bundle> callback, Handler handler) {
        // Get the account manager future.
        AccountManagerFuture<Bundle> accountManagerFuture = mAccntManager.getAuthToken(account,
                authTokenType, options, notifyAuthFailure, null, null);

        // Wrap it with a callable to append account id if necessary.
        CustomizibleAccountManagerCallable callable = new CustomizibleAccountManagerCallable(
                accountManagerFuture) {

            @Override
            protected Bundle customizeResult(Bundle result) throws IOException,
                    AuthenticatorException, OperationCanceledException {
                return blockingAppendAccountId(account, authTokenType, options, notifyAuthFailure,
                        result);
            }

        };

        // Return the future with the wrapping callable.
        HtcAccountManagerFuture<Bundle> future = new HtcAccountManagerFuture<Bundle>(mContext,
                callable, callback, handler);
        AsyncWorkflowTask.THREAD_POOL_EXECUTOR.submit(future);
        return future;
    }

    @Override
    public AccountManagerFuture<Bundle> addAccount(String accountType, String authTokenType,
            String[] requiredFeatures, Bundle addAccountOptions, Activity activity,
            AccountManagerCallback<Bundle> callback, Handler handler) {
        return mAccntManager.addAccount(accountType, authTokenType, requiredFeatures,
                addAccountOptions, activity, callback, handler);
    }

    @Override
    public AccountManagerFuture<Bundle> confirmCredentials(Account account, Bundle options,
            Activity activity, AccountManagerCallback<Bundle> callback, Handler handler) {
        return mAccntManager.confirmCredentials(account, options, activity, callback, handler);
    }

    @Override
    public AccountManagerFuture<Bundle> updateCredentials(Account account, String authTokenType,
            Bundle options, Activity activity, AccountManagerCallback<Bundle> callback,
            Handler handler) {
        return mAccntManager.updateCredentials(account, authTokenType, options, activity, callback,
                handler);
    }

    @Override
    public String getType() {
        return HtcAccountManagerTypes.TYPE_SYSTEM;
    }

    @Override
    public DataServiceFuture<String> asyncGetPassword(final Account account,
            final DataServiceFuture.DataServiceCallback<String> callback, final Handler handler) {
        DataServiceFutureImpl<String> future = new DataServiceFutureImpl<String>(
                mContext, new DataServiceCallable<String>() {

                    @Override
                    public String call() throws IOException {
                        return getPassword(account);
                    }
                }, callback, handler);
        AsyncWorkflowTask.THREAD_POOL_EXECUTOR.submit(future);
        return future;
    }

    @Override
    public DataServiceFuture<String> asyncGetUserData(final Account account,
            final String key,
            DataServiceFuture.DataServiceCallback<String> callback, Handler handler) {
        DataServiceFutureImpl<String> future = new DataServiceFutureImpl<String>(
                mContext, new DataServiceCallable<String>() {

                    @Override
                    public String call() throws IOException {
                        return getUserData(account, key);
                    }
                }, callback, handler);
        AsyncWorkflowTask.THREAD_POOL_EXECUTOR.submit(future);
        return future;
    }

    @Override
    public DataServiceFuture<Account[]> asyncGetAccountsByType(final String type,
            DataServiceFuture.DataServiceCallback<Account[]> callback, Handler handler) {
        DataServiceFutureImpl<Account[]> future = new DataServiceFutureImpl<Account[]>(
                mContext, new DataServiceCallable<Account[]>() {

                    @Override
                    public Account[] call() throws IOException {
                        return getAccountsByType(type);
                    }
                }, callback, handler);
        AsyncWorkflowTask.THREAD_POOL_EXECUTOR.submit(future);
        return future;
    }

    @Override
    public DataServiceFuture<Void> asyncInvalidateAuthToken(final String accountType,
            final String authToken, DataServiceFuture.DataServiceCallback<Void> callback,
            Handler handler) {
        DataServiceFutureImpl<Void> future = new DataServiceFutureImpl<Void>(mContext,
                new DataServiceCallable<Void>() {

                    @Override
                    public Void call() throws IOException {
                        invalidateAuthToken(accountType, authToken);
                        return null;
                    }
                }, callback, handler);
        AsyncWorkflowTask.THREAD_POOL_EXECUTOR.submit(future);
        return future;
    }

    @Override
    public DataServiceFuture<String> asyncPeekAuthToken(final Account account,
            final String authTokenType, DataServiceFuture.DataServiceCallback<String> callback,
            Handler handler) {
        DataServiceFutureImpl<String> future = new DataServiceFutureImpl<String>(
                mContext, new DataServiceCallable<String>() {

                    @Override
                    public String call() throws IOException {
                        return peekAuthToken(account, authTokenType);
                    }
                }, callback, handler);
        AsyncWorkflowTask.THREAD_POOL_EXECUTOR.submit(future);
        return future;
    }

    @Override
    public DataServiceFuture<Void> asyncSetPassword(final Account account,
            final String password, DataServiceFuture.DataServiceCallback<Void> callback,
            Handler handler) {
        DataServiceFutureImpl<Void> future = new DataServiceFutureImpl<Void>(mContext,
                new DataServiceCallable<Void>() {

                    @Override
                    public Void call() throws IOException {
                        setPassword(account, password);
                        return null;
                    }
                }, callback, handler);
        AsyncWorkflowTask.THREAD_POOL_EXECUTOR.submit(future);
        return future;
    }

    @Override
    public DataServiceFuture<Void> asyncClearPassword(final Account account,
            DataServiceFuture.DataServiceCallback<Void> callback, Handler handler) {
        DataServiceFutureImpl<Void> future = new DataServiceFutureImpl<Void>(mContext,
                new DataServiceCallable<Void>() {

                    @Override
                    public Void call() throws IOException {
                        clearPassword(account);
                        return null;
                    }
                }, callback, handler);
        AsyncWorkflowTask.THREAD_POOL_EXECUTOR.submit(future);
        return future;
    }

    @Override
    public DataServiceFuture<Void> asyncSetUserData(final Account account,
            final String key,
            final String value, DataServiceFuture.DataServiceCallback<Void> callback,
            Handler handler) {
        DataServiceFutureImpl<Void> future = new DataServiceFutureImpl<Void>(mContext,
                new DataServiceCallable<Void>() {

                    @Override
                    public Void call() throws IOException {
                        setUserData(account, key, value);
                        return null;
                    }
                }, callback, handler);
        AsyncWorkflowTask.THREAD_POOL_EXECUTOR.submit(future);
        return future;
    }

    @Override
    public DataServiceFuture<Void> asyncSetAuthToken(final Account account,
            final String authTokenType,
            final String authToken, DataServiceFuture.DataServiceCallback<Void> callback,
            Handler handler) {
        DataServiceFutureImpl<Void> future = new DataServiceFutureImpl<Void>(mContext,
                new DataServiceCallable<Void>() {

                    @Override
                    public Void call() throws IOException {
                        setAuthToken(account, authTokenType, authToken);
                        return null;
                    }
                }, callback, handler);
        AsyncWorkflowTask.THREAD_POOL_EXECUTOR.submit(future);
        return future;
    }

    @Override
    public DataServiceFuture<Boolean> asyncAddAccountExplicitly(final Account account,
            final String password, final Bundle userdata,
            DataServiceFuture.DataServiceCallback<Boolean> callback, Handler handler) {
        DataServiceFutureImpl<Boolean> future = new DataServiceFutureImpl<Boolean>(
                mContext, new DataServiceCallable<Boolean>() {

                    @Override
                    public Boolean call() throws IOException {
                        return addAccountExplicitly(account, password, userdata);
                    }
                }, callback, handler);
        AsyncWorkflowTask.THREAD_POOL_EXECUTOR.submit(future);
        return future;
    }

    /**
     * Blocking get account id from local database.
     * 
     * @param account Account to find account id with.
     * @param authToken Account auth token to matches.
     * @return Account id or {@code null} if not found in database.
     * @throws RemoteException
     */
    private String blockingGetAccountIdFromCache(Account account, String authToken)
            throws RemoteException {
        ProcessUtils.ensureNotOnMainThread();
        DataServiceConnection conn = new DataServiceConnection(mContext);
        String accountId = conn.bind().getRemoteDataSource()
            .getGuid(new HtcAccount(account), authToken);
        conn.unbind();

        mLogger.debugS("Found account id: ", accountId);
        return accountId;
    }

    /**
     * Blocking store account id to local database.
     * 
     * @param account Account with the account id.
     * @param accountId Account id to store.
     * @param authToken Account auth token to store.
     * @throws RemoteException
     */
    private void blockingSetAccountIdCache(Account account, String accountId, String authToken)
            throws RemoteException {
        mLogger.verboseS(account, ": ", accountId);

        ProcessUtils.ensureNotOnMainThread();
        DataServiceConnection conn = new DataServiceConnection(mContext);
        conn.bind().getRemoteDataSource()
            .setGuid(new HtcAccount(account), accountId, authToken);
        conn.unbind();
    }

    /**
     * Blocking get account id from user data provider.
     * 
     * @param account Account to find account id with.
     * @return Account id or {@code null} if not available.
     * @throws RemoteException
     */
    private String blockingGetAccountIdFromProvider(Account account) throws RemoteException {
        UserDataProviderHelper udHelper = new UserDataProviderHelper(mContext);
        ContentProviderClient client = udHelper.acquireUserDataProviderClient();
        String accountId = null;

        if (client != null) {
            Cursor c = client.query(udHelper.getUserDataProviderUri(),
                    new String[] {
                            HtcAccountDefs.COLUMN_KEY, HtcAccountDefs.COLUMN_VALUE
                    }, null, null, null);

            while (c.moveToNext()) {
                if (HtcAccountDefs.KEY_ACCOUNT_ID.equals(c.getString(
                        c.getColumnIndexOrThrow(HtcAccountDefs.COLUMN_KEY)))) {
                    accountId = c.getString(
                            c.getColumnIndexOrThrow(HtcAccountDefs.COLUMN_VALUE));
                    mLogger.debugS("Found account id: ", accountId);
                    break;
                }
            }
            c.close();
        } else {
            // Acquisition failed.
            mLogger.warning("Not able to aquire user data provider "
                    + "while working on SystemHtcAccountManager. "
                    + "It could happen if HTC Account client package is updating. "
                    + "Please try again later.");
        }

        return accountId;
    }

    /**
     * Blocking get account id through the {@code verifyToken} REST API.
     * 
     * @param authToken Access token to use.
     * @return The account id.
     * @throws InterruptedException
     * @throws ConnectivityException
     * @throws ConnectionException
     * @throws IOException
     * @throws HttpException
     */
    private String blockingGetAccountIdFromServer(String authToken) throws InterruptedException,
            ConnectivityException, ConnectionException, IOException, HttpException {
        HtcAccountResource resource = new HtcAccountResource(mContext);
        String accountId = resource.verifyToken(authToken, null /* callback */, null /* handler */)
                .getResult().accountId;

        mLogger.debugS("Found account id: ", accountId);
        return accountId;
    }

    /**
     * Append account id in the {@code result} {@link Bundle} if not included
     * yet.
     * 
     * @param account
     * @param authTokenType
     * @param options
     * @param result Returning {@link Bundle} from {@link AccountManager} calls.
     * @return Updated {@link Bundle} which includes account id.
     * @throws IOException If database accessing failed, or the acquisition to
     *             the user data provider succeeded but the query failed, or
     *             verify token returns unexpected result.
     * @throws OAuth2InvalidAuthKeyException If the access token has to be
     *             refreshed to continue.
     */
    private Bundle blockingAppendAccountId(Account account, String authTokenType, Bundle result)
            throws IOException, OAuth2InvalidAuthKeyException {
        if (result == null)
            throw new IllegalArgumentException("'result' is null.");

        if (!result.containsKey(HtcAccountDefs.KEY_ACCOUNT_ID)
                && result.containsKey(AccountManager.KEY_AUTHTOKEN)
                && HtcAccountDefs.AUTHTOKEN_TYPE_SOCIAL_SINA_ACCOUNT != authTokenType) {
            mLogger.debug("Append account id since it's not included in the result Bundle.");

            String accountId = null;
            String authToken = result.getString(AccountManager.KEY_AUTHTOKEN);

            // First try local database cache.
            try {
                accountId = blockingGetAccountIdFromCache(account, authToken);
                if (!TextUtils.isEmpty(accountId)) {
                    result.putString(HtcAccountDefs.KEY_ACCOUNT_ID, accountId);
                    return result;
                }
            } catch (RemoteException e) {
                /*
                 * Database accessing failed. Convert to IOException.
                 */
                throw new IOException(e.getMessage(), e);
            }

            // Try user data provider.
            try {
                accountId = blockingGetAccountIdFromProvider(account);
                if (!TextUtils.isEmpty(accountId)) {
                    // Update cache and append account id.
                    blockingSetAccountIdCache(account, accountId, authToken);
                    result.putString(HtcAccountDefs.KEY_ACCOUNT_ID, accountId);
                    return result;
                }
            } catch (SecurityException e) {
                // No proper permission.
                mLogger.warning("The caller has no proper permission to access user data provider.");
            } catch (RemoteException e) {
                /*
                 * Provider accessing failed. Convert to IOException.
                 */
                throw new IOException(e.getMessage(), e);
            }

            // Try verify token.
            try {
                authToken = result.getString(AccountManager.KEY_AUTHTOKEN);
                accountId = blockingGetAccountIdFromServer(authToken);
                if (!TextUtils.isEmpty(accountId)) {
                    // Update cache and append account id.
                    blockingSetAccountIdCache(account, accountId, authToken);
                    result.putString(HtcAccountDefs.KEY_ACCOUNT_ID, accountId);
                    return result;
                }
            } catch (OAuth2InvalidAuthKeyException e) {
                // Invalidate the expired token and rethrow.
                invalidateAuthToken(authToken);
                throw e;
            } catch (Exception e) {
                /*
                 * Verify token failed. Convert to IOException.
                 */
                throw new IOException(e.getMessage(), e);
            }
        } else {
            mLogger.debug("No need to append account id.");
        }

        return result;
    }

    /**
     * Overloaded method to make retry on access token expires.
     */
    private Bundle blockingAppendAccountId(Account account, String authTokenType, Bundle options,
            Activity activity, Bundle result)
            throws OperationCanceledException, AuthenticatorException, IOException {
        try {
            return blockingAppendAccountId(account, authTokenType, result);
        } catch (OAuth2InvalidAuthKeyException e) {
            // The token has been invalidated. Just retry.
            mLogger.debug("Token expires. Make a retry.");
            return getAuthToken(account, authTokenType, options, activity, null, null)
                    .getResult();
        }
    }

    /**
     * Overloaded method to make retry on access token expires.
     */
    private Bundle blockingAppendAccountId(Account account, String authTokenType, Bundle options,
            boolean notifyAuthFailure, Bundle result)
            throws OperationCanceledException, AuthenticatorException, IOException {
        try {
            return blockingAppendAccountId(account, authTokenType, result);
        } catch (OAuth2InvalidAuthKeyException e) {
            // The token has been invalidated. Just retry.
            mLogger.debug("Token expires. Make a retry.");
            return getAuthToken(account, authTokenType, options, notifyAuthFailure, null, null)
                    .getResult();
        }
    }

    /**
     * Wrapper class operates on a {@link AccountManagerFuture<Bundle>}. The
     * result can be customized by override {@link #customizeResult(Bundle)}.
     * 
     * @author samael_wang@htc.com
     */
    private class CustomizibleAccountManagerCallable implements HtcAccountManagerCallable<Bundle> {
        private AccountManagerFuture<Bundle> mmFuture;

        public CustomizibleAccountManagerCallable(AccountManagerFuture<Bundle> future) {
            mmFuture = future;
        }

        @Override
        public Bundle call() throws IOException, AuthenticatorException, OperationCanceledException {
            mLogger.verbose("Calling AccountManagerFuture<Bundle>.getResult() and make customization...");
            return customizeResult(mmFuture.getResult());
        }

        /**
         * Customize on the result of {@link AccountManagerFuture<Bundle>}. The
         * default implementation returns the {@code result} directly with no
         * modification.
         * 
         * @param result Result {@link Bundle}.
         * @return Customized result {@link Bundle}.
         */
        protected Bundle customizeResult(Bundle result) throws IOException, AuthenticatorException,
                OperationCanceledException {
            return result;
        }
    }

    @Override
    public void registerReceiver(BroadcastReceiver receiver) {
        if (!new ApplicationInfoHelper(mContext).hasPermission(
                HtcAccountBroadcasts.PERMISSION_RECEIVE)) {
            mLogger.warning("The caller has no permission '"
                    + HtcAccountBroadcasts.PERMISSION_RECEIVE
                    + "', it might not be able to receive HTC Account broadcasts.");
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(HtcAccountBroadcasts.ACTION_ADD_ACCOUNT_COMPLETED);
        filter.addAction(HtcAccountBroadcasts.ACTION_ADD_ACCOUNT_CANCELED);
        filter.addAction(HtcAccountBroadcasts.ACTION_AUTH_TOKEN_RENEWED);
        filter.addAction(HtcAccountBroadcasts.ACTION_REMOVE_ACCOUNT_COMPLETED);

        mContext.registerReceiver(receiver, filter,
                HtcAccountBroadcasts.PERMISSION_SEND, null);
    }

    @Override
    public String getAccountAppVersionName() {
        PackageManager pm = mContext.getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo(HtcAccountDefs.PKG_NAME_IDENTITY_CLIENT, 0);
            return info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            mLogger.warning(e.getMessage());
        }
        // Not available
        return VERSION_NOT_AVAILABLE;
    }
}
