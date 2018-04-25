package com.htc.lib1.cs.account;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.htc.lib1.cs.logging.CommLoggerFactory;
import com.htc.lib1.cs.logging.HtcLogger;
import com.htc.lib1.cs.workflow.AsyncWorkflowTask;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * A simplified helper class for application sign-in with HTC account.
 * Currently only provides {@link #isSignedIn}, {@link #signIn},
 * {@link #signOut}, {@link #getAccount}, {@link #getAuthToken},
 * and {@link #invalidateAuthToken} APIs.
 */
public class AppSignInHelper {
    private HtcLogger mLogger = new CommLoggerFactory(this).create();
    private Context mContext;
    private HtcAccountManager mAccountManager;
    private AppSignInPreference mSignInPrefs;
    private String mModuleName;

    private Handler mMainHandler;

    /**
     * Create an instance.
     *
     * @param context Context to operate on. Shouldn't be null.
     * @param moduleName a unique name for identify each sub-module in the integrated app.
     *                     Shouldn't be null.
     */
    public AppSignInHelper(Context context, String moduleName) {
        if (context == null) {
            throw new IllegalArgumentException("context shouldn't be null");
        }
        if (moduleName == null) {
            throw new IllegalArgumentException("moduleName shouldn't be null");
        }
        mContext = context;
        mAccountManager = HtcAccountManagerCreator.get().create(context);
        mModuleName = moduleName;
        mSignInPrefs = new AppSignInPreference(context, mModuleName);
        mMainHandler = new Handler(mContext.getMainLooper());
    }

    /**
     * Check whether the integrated app is signed-in.
     *
     * @return Whether the host application is signed-in.
     */
    public boolean isSignedIn() {
        if (!isAccountExist()) {
            mLogger.debugS("account not exist");
            return false;
        } else if (isLocalAccount() // local account
                || !isSupportAppSignIn()) { // old authenticator
            mLogger.debugS("local or not support app sign-in, return true");
            return true;
        } else {
            if (!mSignInPrefs.isSignedIn(false)) { // app not sign-in
                mLogger.debugS("app not sign-in");
                return false;
            } else {
                String recordedTag = mSignInPrefs.getTag();
                String currentTag = getAccountTag();
                mLogger.debugS("current tag = " + currentTag);
                if (!TextUtils.equals(recordedTag, currentTag)) {
                    // sign out app because account at least sign-out once.
                    setSignIn(false);
                    mLogger.debugS("account once signed-out");
                    return false;
                }
                mLogger.debugS("app is signed-in");
                return true;
            }
        }
    }

    /**
     * Return current HTC account.
     *
     * @return {@code null} if app not signed-in.
     */
    public Account getAccount() {
        if (isSignedIn()) {
            return mAccountManager.getAccounts()[0];
        } else {
            return null;
        }
    }

    /**
     * Gets an auth token of the signed-in HTC account, prompting the user for
     * credentials if necessary. This method is intended for applications running
     * in the foreground where it makes sense to ask the user directly for a password.
     * <p>
     * If a previously generated auth token is cached for this account, then it
     * is returned. Otherwise, if a saved refresh token is available, it
     * is sent to the server to generate a new auth token. Otherwise, the user
     * is prompted to enter credentials.
     * <p>
     * This method may be called from any thread, but the returned
     * {@link AccountManagerFuture} must not be used on the main thread.
     * <p>
     * If targeting your app to work on API level 22 and before, USE_CREDENTIALS
     * permission is needed for those platforms.
     *
     * @param options Currently not in use, may be {@code null} or empty.
     * @param activity The {@link Activity} context to use for launching a new
     *            authenticator-defined sub-Activity to prompt the user for
     *            credentials if necessary; used only to call startActivity();
     *            must not be null.
     * @param callback Callback to invoke when the request completes, null for
     *            no callback
     * @param handler {@link Handler} identifying the callback thread, null for
     *            the main thread
     * @return An {@link AccountManagerFuture} which resolves to a Bundle with
     *         following fields:
     *         <ul>
     *         <li> {@link AccountManager#KEY_ACCOUNT_NAME} - the name of the
     *         account you supplied <li> {@link AccountManager#KEY_ACCOUNT_TYPE}
     *         - the type of the account <li>
     *         {@link AccountManager#KEY_AUTHTOKEN} - the auth token you wanted
     *         <li> {@link HtcAccountDefs#KEY_ACCOUNT_ID} - the account id of
     *         the operating HTC Account. Only available if
     *         {@code authTokenType} is not
     *         {@link HtcAccountDefs#AUTHTOKEN_TYPE_SOCIAL_SINA_ACCOUNT}.
     *         </ul>
     *         If an auth token could not be fetched,
     *         {@link AccountManagerFuture#getResult()} throws:
     *         <ul>
     *         <li> {@link AuthenticatorException} if the authenticator failed
     *         to respond <li> {@link OperationCanceledException} if the
     *         operation is canceled for any reason, including the user
     *         canceling a credential request <li> {@link IOException} if the
     *         authenticator experienced an I/O problem creating a new auth
     *         token, usually because of network trouble
     *         </ul>
     *         If the account is no longer present, the return value is
     *         undefined. The caller should verify the validity of the account
     *         before requesting an auth token.
     */
    public AccountManagerFuture<Bundle> getAuthToken(final Bundle options, final Activity activity,
                                                     final AccountManagerCallback<Bundle> callback,
                                                     final Handler handler) {
        if (isSignedIn()) {
            Account[] accounts = mAccountManager.getAccounts();
            return mAccountManager.getAuthToken(accounts[0], HtcAccountDefs.AUTHTOKEN_TYPE_DEFAULT,
                    options, activity, callback, handler);
        } else {
            // return error for sign-in
            return getNeedsSignInFuture(callback, handler);
        }
    }

    /**
     * Gets an auth token of the signed-in HTC account, optionally raising a
     * notification if the user must enter credentials.
     * This method is intended for background tasks and services where the user
     * should not be immediately interrupted with a password prompt.
     * <p>
     * If a previously generated auth token is cached for this account,
     * then it is returned. Otherwise, if a saved password is available, it is
     * sent to the server to generate a new auth token. Otherwise, an
     * {@link Intent} is returned which, when started, will prompt the user for
     * a password. If the notifyAuthFailure parameter is set, a status bar
     * notification is also created with the same Intent, alerting the user that
     * they need to enter a password at some point.
     * <p>
     * In that case, you may need to wait until the user responds, which could
     * take hours or days or forever. When the user does respond and supply a
     * new password, the account manager will broadcast the
     * {@link AccountManager#LOGIN_ACCOUNTS_CHANGED_ACTION} Intent, which
     * applications can use to try again.
     * <p>
     * If notifyAuthFailure is not set, it is the application's responsibility
     * to launch the returned Intent at some point. Either way, the result from
     * this call will not wait for user action.
     * <p>
     * Some authenticators have auth token <em>types</em>, whose value is
     * authenticator-dependent. Some services use different token types to
     * access different functionality -- for example, Google uses different auth
     * tokens to access Gmail and Google Calendar for the same account.
     * <p>
     * This method may be called from any thread, but the returned
     * {@link AccountManagerFuture} must not be used on the main thread.
     * <p>
     * If targeting your app to work on API level 22 and before,
     * {@link android.Manifest.permission#USE_CREDENTIALS}
     * permission is needed for those platforms.
     *
     * @param options Authenticator-specific options for the request, may be
     *            null or empty
     * @param notifyAuthFailure True to add a notification to prompt the user
     *            for a password if necessary, false to leave that to the caller
     * @param callback Callback to invoke when the request completes, null for
     *            no callback
     * @param handler {@link Handler} identifying the callback thread, null for
     *            the main thread
     * @return An {@link AccountManagerFuture} which resolves to a Bundle with
     *         following fields on success:
     *         <ul>
     *         <li> {@link AccountManager#KEY_ACCOUNT_NAME} - the name of the
     *         account you supplied <li> {@link AccountManager#KEY_ACCOUNT_TYPE}
     *         - the type of the account <li>
     *         {@link AccountManager#KEY_AUTHTOKEN} - the auth token you wanted
     *         <li> {@link HtcAccountDefs#KEY_ACCOUNT_ID} - the account id of
     *         the operating HTC Account. Only available if
     *         {@code authTokenType} is not
     *         {@link HtcAccountDefs#AUTHTOKEN_TYPE_SOCIAL_SINA_ACCOUNT}.
     *         </ul>
     *         If the user must enter credentials, the returned Bundle contains
     *         only {@link AccountManager#KEY_INTENT} with the {@link Intent}
     *         needed to launch a prompt. If an error occurred,
     *         {@link AccountManagerFuture#getResult()} throws:
     *         <ul>
     *         <li> {@link AuthenticatorException} if the authenticator failed
     *         to respond <li> {@link OperationCanceledException} if the
     *         operation is canceled for any reason, including the user
     *         canceling a credential request <li> {@link IOException} if the
     *         authenticator experienced an I/O problem creating a new auth
     *         token, usually because of network trouble
     *         </ul>
     *         If the account is no longer present, the return value is
     *         undefined. The caller should verify the validity of the account
     *         before requesting an auth token.
     */
    public AccountManagerFuture<Bundle> getAuthToken(final Bundle options,
                                                     final boolean notifyAuthFailure,
                                                     final AccountManagerCallback<Bundle> callback,
                                                     final Handler handler) {
        if (isSignedIn()) {
            Account[] accounts = mAccountManager.getAccounts();
            return mAccountManager.getAuthToken(accounts[0], HtcAccountDefs.AUTHTOKEN_TYPE_DEFAULT,
                    options, notifyAuthFailure, callback, handler);
        } else {
            // return error for sign-in
            return getNeedsSignInFuture(callback, handler);
        }
    }

    private AccountManagerFuture<Bundle> getNeedsSignInFuture(
            AccountManagerCallback<Bundle> callback, Handler handler) {
        AsyncAccountTask<Bundle> task = new AsyncAccountTask<Bundle>(null, callback, ensureHandler(handler)) {
            @Override
            public AsyncAccountTask<Bundle> start(Intent intent) {
                // just throw need sign-in exception.
                setException(new AuthenticatorException("Module not sign-in yet."));
                return this;
            }
            @Override
            protected Bundle bundleToResult(Bundle bundle) {
                return null;
            }
        };
        return task.start(null);
    }

    /**
     * Removes an HTC Account auth token from the {@link HtcAccountManager}'s
     * cache. Does nothing if the auth token is not currently in the cache.
     * Applications must call this method when the auth token is found to have
     * expired or otherwise become invalid for authenticating requests. The
     * {@link HtcAccountManager} does not validate or expire cached auth tokens
     * otherwise.
     * <p>
     * In order to be compatible with {@link AccountManager} interface, this
     * method can be invoked from the main thread. However, it involves database
     * operations, and SQLite doesn't support simultaneous accessing from
     * multiple processes. Hence, if responsiveness is critical for your app, or
     * your app needs to call this method in a sub-process, use
     * {@link #asyncInvalidateAuthToken(String, String,
     * com.htc.cs.comm.account.DataServiceFuture.DataServiceCallback, Handler)}
     * instead.
     * <p>
     * NOTE: If targeting your app to work on API level 22 and before,
     * {@link android.Manifest.permission#MANAGE_ACCOUNTS} or
     * {@link android.Manifest.permission#USE_CREDENTIALS} permission is needed
     * for those platforms.
     *
     * @param authToken The auth token to invalidate, may be {@code null}
     */
    public void invalidateAuthToken(final String authToken) {
        if (isSignedIn()) {
            mAccountManager.invalidateAuthToken(authToken);
        }
    }

    /**
     * Asynchronized implementation of {@link #invalidateAuthToken(String)}
     * which guarantees the database operation is in a background thread on main
     * process no matter what the calling process / thread is.
     * <p>
     * This method may be called from any thread, but the returned
     * {@link DataServiceFuture} must not be used on the main thread.
     *
     * @param authToken The auth token to invalidate, may be {@code null}
     * @param callback Callback to invoke when the request completes, null for
     *            no callback
     * @param handler {@link Handler} identifying the callback thread, null for
     *            the main thread
     * @return An {@link DataServiceFuture} which can be used for block waiting.
     */
    public DataServiceFuture<Void> asyncInvalidateAuthToken(final String authToken,
                final DataServiceFuture.DataServiceCallback<Void> callback, final Handler handler) {
        if (isSignedIn()) {
            return mAccountManager.asyncInvalidateAuthToken(authToken, callback, handler);
        }
        DataServiceFutureImpl<Void> future = new DataServiceFutureImpl<>(mContext,
                new DataServiceFuture.DataServiceCallable<Void>() {
                    @Override
                    public Void call() throws IOException {
                        // no-op
                        return null;
                    }
                }, callback, handler);
        AsyncWorkflowTask.THREAD_POOL_EXECUTOR.submit(future);
        return future;
    }

    /**
     * Asks the user to add an HTC Account. The authenticator processes this
     * request with the appropriate user interface. If the user does elect to
     * add an account, either through sign-in or sign-up, the account name is
     * returned.
     * <p>
     * This method may be called from any thread, but the returned
     * {@link AccountManagerFuture} must not be used on the main thread.
     * <p>
     * NOTE: If targeting your app to work on API level 22 and before,
     * {@link android.Manifest.permission#MANAGE_ACCOUNTS} permission
     * is needed for those platforms.
     *
     * @param options For {@link HtcAccountManagerTypes#TYPE_LOCAL}
     *            instance, the options take no effect as WebView sign-in
     *            doesn't support any customization at this moment. For
     *            {@link HtcAccountManagerTypes#TYPE_SYSTEM} instance, the
     *            following options can be supplied:
     *            <ul>
     *            <li> {@link HtcAccountDefs#KEY_REQUEST_TYPE} - type of the add
     *            account request. Can be one of the following options. The
     *            default value is {@link HtcAccountDefs#TYPE_SIGN_IN_SIGN_UP}
     *            if not specified.
     *            <ul>
     *            <li> {@link HtcAccountDefs#TYPE_SIGN_IN_SIGN_UP} - request
     *            user to either sign-in or sign-up an account. All available
     *            login options will be shown. <li>
     *            {@link HtcAccountDefs#TYPE_SIGN_IN} - request user to sign-in
     *            a regular HTC Account directly. All social login and sign-up
     *            options will be disabled. <li>
     *            {@link HtcAccountDefs#TYPE_SIGN_IN_SINA} - request user to
     *            sign-in with Sina account. Supported on all China projects
     *            since Sense 4+ except DLX_U_JB_45. All other login options
     *            will be disabled. On a unsupported device, the behavior is
     *            undefined.<li> {@link HtcAccountDefs#TYPE_SIGN_IN_FACEBOOK} -
     *            request user to sign-in with Facebook account. Supported on
     *            all global projects since Sense 5.0. All other login options
     *            will be disabled. On a unsupported device, the behavior is
     *            undefined. <li> {@link HtcAccountDefs#TYPE_SIGN_IN_GOOGLE} -
     *            request user to sign-in with Google account. Supported on all
     *            global projects since Sense 5+. All other login options will
     *            be disabled. On a unsupported device, the behavior is
     *            undefined. <li> {@link HtcAccountDefs#TYPE_SIGN_IN_QQ} -
     *            request user to sign-in with QQ account. Supported on all
     *            China projects since Sense 5+. All other login options will be
     *            disabled. On a unsupported device, the behavior is undefined.
     *            <li> {@link HtcAccountDefs#TYPE_SIGN_IN_EXT} - request user to
     *            sign-in with one of the social login options. All available
     *            sign-in options except regular HTC Account will be shown. This
     *            option is available since Sense 5.0. <li>
     *            {@link HtcAccountDefs#TYPE_CUSTOM} - use an integer as the
     *            flags to decide what login options should be shown, if
     *            available. The flags must be passed as an integer with key
     *            {@link HtcAccountDefs#KEY_CUSTOM_LOGIN_OPTIONS}. This options
     *            is supported since Sense 6.0.
     *            </ul>
     *            <li> {@link HtcAccountDefs#KEY_CUSTOM_LOGIN_OPTIONS} – an
     *            integer as flags to indicate what login options should be
     *            shown, if available, when the request type is
     *            {@link HtcAccountDefs#TYPE_CUSTOM}. The flags can be
     *            combination of the following values:
     *            <ul>
     *            <li>
     *            {@link HtcAccountDefs#FLAG_LOGIN_OPTION_SIGNIN_HTC_ACCOUNT},
     *            <li>
     *            {@link HtcAccountDefs#FLAG_LOGIN_OPTION_SIGNIN_SINA_ACCOUNT},
     *            <li>
     *            {@link HtcAccountDefs#FLAG_LOGIN_OPTION_SIGNIN_FACEBOOK_ACCOUNT}
     *            ,<li>
     *            {@link HtcAccountDefs#FLAG_LOGIN_OPTION_SIGNIN_QQ_ACCOUNT},
     *            and <li>
     *            {@link HtcAccountDefs#FLAG_LOGIN_OPTION_SIGNIN_GOOGLE_ACCOUNT}
     *            .
     *            </ul>
     *            The names are self-explanatory.</li> <li>
     *            {@link HtcAccountDefs#KEY_ACCOUNT_LIFETIME} - special option
     *            to specify the lifetime of the account to add. The option is
     *            first introduce in Sense 6.0.
     *            <ul>
     *            <li> {@link HtcAccountDefs#ACCOUNT_LIFETIME_ONETIME} -
     *            indicates the account shouldn't be added to the system, but
     *            instead the auth token should be included in the return value
     *            directly for one-time use. On China projects it's supported
     *            since Sense 6.0 K443 MR. <li>
     *            {@link HtcAccountDefs#ACCOUNT_LIFETIME_PERSISTENT} - indicates
     *            the account should be added to the system, and other
     *            applications can later use {@link HtcAccountManager} APIs to
     *            operate on this account. It’s also the default value to use if
     *            not specified.
     *            </ul>
     *            </li> <li> {@link HtcAccountDefs#KEY_TOS_ACCEPTED} -
     *            {@code true} to indicate user has viewed and accepted HTC
     *            Account terms and conditions. The authenticator will then skip
     *            landing page if possible. Use this option with care - if user
     *            didn't accept the terms and conditions but caller passes
     *            {@code true}, it might cause legal issues.
     *            </ul>
     * @param activity The {@link Activity} context to use for launching a new
     *            authenticator-defined sub-Activity to prompt the user to
     *            create an account; used only to call startActivity(); if null,
     *            the prompt will not be launched directly, but the necessary
     *            {@link Intent} will be returned to the caller instead
     * @param callback Callback to invoke when the request completes, null for
     *            no callback
     * @param handler {@link Handler} identifying the callback thread, null for
     *            the main thread
     * @return An {@link AccountManagerFuture} which resolves to a Bundle with
     *         these fields if activity was specified and an account was
     *         created:
     *         <ul>
     *         <li> {@link AccountManager#KEY_ACCOUNT_NAME} - the name of the
     *         account created <li> {@link AccountManager#KEY_ACCOUNT_TYPE} -
     *         the type of the account <li> {@link HtcAccountDefs#KEY_AUTHKEY} -
     *         the authtoken for the caller app. Presents only if the caller
     *         holds {@link android.Manifest.permission#USE_CREDENTIALS} and and
     *         runs on Sense 6.0 or later devices. <li>
     *         {@link HtcAccountDefs#KEY_ACCOUNT_ID} - the account id of the HTC
     *         Account. Presents only if the caller holds
     *         {@link android.Manifest.permission#USE_CREDENTIALS} and runs on
     *         Sense 7.0 or later devices.
     *         </ul>
     *         If no activity was specified, the returned Bundle contains only
     *         {@link AccountManager#KEY_INTENT} with the {@link Intent} needed
     *         to launch the actual account creation process. If an error
     *         occurred, {@link AccountManagerFuture#getResult()} throws:
     *         <ul>
     *         <li> {@link AuthenticatorException} if no authenticator was
     *         registered for this account type or the authenticator failed to
     *         respond <li> {@link OperationCanceledException} if the operation
     *         was canceled for any reason, including the user canceling the
     *         creation process <li> {@link IOException} if the authenticator
     *         experienced an I/O problem creating a new account, usually
     *         because of network trouble
     *         </ul>
     */
    public AccountManagerFuture<Bundle> signIn(Bundle options, final Activity activity,
                                               final AccountManagerCallback<Bundle> callback,
                                               Handler handler) {
        if (isLocalAccount()) {
            return mAccountManager.addAccount(options, activity, callback, handler);
        } else if (isAccountExist() && isSupportAppSignIn()) {
            // launch confirm sign-in page
            Intent intent = new Intent(mContext, SignInHelperActivity.class);
            intent.putExtra(SignInHelperActivity.KEY_CALLING_MODULE, mModuleName);
            intent.setAction(SignInHelperActivity.ACTION_SIGN_IN_INTERMEDIATE);
            return new AsyncAccountTask<Bundle>(activity, callback, ensureHandler(handler)) {
                @Override
                protected Bundle bundleToResult(Bundle bundle) {
                    Intent intent = bundle.getParcelable(AccountManager.KEY_INTENT);
                    if (intent != null && activity != null) {
                        activity.startActivity(intent);
                    }
                    return bundle;
                }
            }.start(intent);
        } else {
            return new SystemAccountAddFutureWrapper(mAccountManager.addAccount(options, activity,
                    new AccountManagerCallback<Bundle>() {
                        @Override
                        public void run(AccountManagerFuture<Bundle> accountManagerFuture) {
                            if (callback != null) {
                                callback.run(new SystemAccountAddFutureWrapper(accountManagerFuture));
                            }
                        }
                    }, handler));
        }
    }

    private class SystemAccountAddFutureWrapper implements AccountManagerFuture<Bundle> {
        private AccountManagerFuture<Bundle> mFuture;
        public SystemAccountAddFutureWrapper(AccountManagerFuture<Bundle> origin) {
            mFuture = origin;
        }

        @Override
        public boolean cancel(boolean b) {
            return mFuture.cancel(b);
        }

        @Override
        public boolean isCancelled() {
            return mFuture.isCancelled();
        }

        @Override
        public boolean isDone() {
            return mFuture.isDone();
        }

        @Override
        public Bundle getResult() throws OperationCanceledException, IOException,
                AuthenticatorException {
            return getResultInternal(null, null);
        }

        @Override
        public Bundle getResult(long l, TimeUnit timeUnit) throws OperationCanceledException,
                IOException, AuthenticatorException {
            return getResultInternal(l, timeUnit);
        }

        private Bundle getResultInternal(Long l, TimeUnit timeUnit) throws
                OperationCanceledException, IOException, AuthenticatorException {
            Bundle result;
            if (l == null) {
                result = mFuture.getResult();
            } else {
                result = mFuture.getResult(l, timeUnit);
            }
            if (result != null) {
                if (result.containsKey(AccountManager.KEY_INTENT)) {
                    // replace the result's intent with intermediate activity, and pack a new intent
                    // for launch the intermediate activity
                    Intent originalIntent = (Intent) result.get(AccountManager.KEY_INTENT);
                    Intent intermediateIntent = new Intent(mContext, SignInHelperActivity.class);
                    intermediateIntent.putExtra(SignInHelperActivity.KEY_CALLING_MODULE, mModuleName);
                    intermediateIntent.putExtra(AccountManager.KEY_INTENT, originalIntent);
                    result.remove(AccountManager.KEY_INTENT);
                    result.putParcelable(AccountManager.KEY_INTENT, intermediateIntent);
                } else {
                    // System account sign-in success, set app as signed-in
                    setSignIn(true);
                    mLogger.debugS("set app signed in");
                }
            }
            return result;
        }
    }

    /**
     * Sign-out the integrated application, does not remove the account from
     * system. Does nothing if the account does not exist. If the HTC account
     * app version is too old which only support system-wild sign-in, will
     * bring user to system settings' account&sync page, to let user remove
     * account from system settings.
     * <p>
     * This method may be called from any thread, but the returned
     * {@link AccountManagerFuture} must not be used on the main thread.
     * <p>
     * NOTE: If targeting your app to work on API level 22 and before,
     * {@link android.Manifest.permission#MANAGE_ACCOUNTS} permission is needed
     * for those platforms.
     *
     * @param activity The {@link Activity} context to use for launching a new
     *            authenticator-defined sub-Activity to prompt the user for
     *            credentials if necessary; used only to call startActivity();
     *            if null, the prompt will not be launched directly, but the
     *            {@link Intent} may be returned to the caller instead.
     * @param callback Callback to invoke when the request completes, null for
     *            no callback
     * @param handler {@link Handler} identifying the callback thread, null for
     *            the main thread
     * @return An {@link AccountManagerFuture} which resolves to a Boolean, true
     *         if the account has been successfully removed, false if the
     *         authenticator forbids deleting this account.
     */
    public AccountManagerFuture<Bundle> signOut(@NonNull final Activity activity,
                                                final AccountManagerCallback<Bundle> callback,
                                                Handler handler) {
        Account accounts[] = mAccountManager.getAccounts();
        if (accounts.length > 0) {
            if (isLocalAccount()) {
                return new LocalAccountRemovalFuture(mAccountManager.removeAccount(accounts[0],
                        new AccountManagerCallback<Boolean>() {
                            @Override
                            public void run(AccountManagerFuture<Boolean> accountManagerFuture) {
                                if (callback != null) {
                                    callback.run(new LocalAccountRemovalFuture(accountManagerFuture));
                                }
                            }
                        }, handler));
            } else if (!isSupportAppSignIn()) { // old account app
                Intent intent = new Intent(mContext, RemoveAccountHelperActivity.class);
                return new AsyncAccountTask<Bundle>(activity, callback, ensureHandler(handler)) {
                    @Override
                    protected Bundle bundleToResult(Bundle bundle) {
                        Bundle result = new Bundle();
                        result.putBoolean(AccountManager.KEY_BOOLEAN_RESULT,
                                bundle.getBoolean(RemoveAccountHelperActivity.KEY_ACCOUNT_REMOVED,
                                        false));
                        return result;
                    }
                }.start(intent);
            } else {
                setSignIn(false);
            }
        }
        // remove success or no account exist
        return new AsyncAccountTask<Bundle>(null, callback, ensureHandler(handler)) {
            @Override
            protected Bundle bundleToResult(Bundle bundle) {
                Bundle result = new Bundle();
                result.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, true);
                return result;
            }
        }.start(null);
    }

    private class LocalAccountRemovalFuture implements AccountManagerFuture<Bundle> {
        private AccountManagerFuture<Boolean> mFuture;
        LocalAccountRemovalFuture(AccountManagerFuture<Boolean> origin) {
            mFuture = origin;
        }

        @Override
        public boolean cancel(boolean b) {
            return mFuture.cancel(b);
        }

        @Override
        public boolean isCancelled() {
            return mFuture.isCancelled();
        }

        @Override
        public boolean isDone() {
            return mFuture.isDone();
        }

        @Override
        public Bundle getResult() throws OperationCanceledException, IOException,
                AuthenticatorException {
            return getResultInternal(null, null);
        }

        @Override
        public Bundle getResult(long l, TimeUnit timeUnit) throws OperationCanceledException,
                IOException, AuthenticatorException {
            return getResultInternal(l, timeUnit);
        }

        private Bundle getResultInternal(Long l, TimeUnit timeUnit)
                throws OperationCanceledException, IOException, AuthenticatorException {
            boolean accountRemoved;
            if (l == null) {
                accountRemoved = mFuture.getResult();
            } else {
                accountRemoved = mFuture.getResult(l, timeUnit);
            }
            Bundle bundle = new Bundle();
            bundle.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, accountRemoved);
            if (!accountRemoved) {
                // this should not happen
                mLogger.error("Local account remove failed.");
            }
            return bundle;
        }
    }

    void setSignIn(boolean signedIn) {
        String tag = null;
        if (signedIn) {
            tag = getAccountTag();
        }
        setSignIn(signedIn, tag);
    }

    void setSignIn(boolean signedIn, String tag) {
        mSignInPrefs.setSignedIn(signedIn);
        mSignInPrefs.setTag(signedIn ? tag : null);
    }

    private Handler ensureHandler(Handler handler) {
        return handler == null ? mMainHandler : handler;
    }

    private boolean isLocalAccount() {
        return HtcAccountManager.HtcAccountManagerTypes.TYPE_LOCAL.equals(mAccountManager.getType());
    }

    private boolean isAccountExist() {
        return mAccountManager.getAccounts().length > 0;
    }

    private boolean isSupportAppSignIn() {
        PackageManager pm = mContext.getPackageManager();
        try {
            ProviderInfo info = pm.getProviderInfo(
                    new ComponentName(HtcAccountDefs.PKG_NAME_IDENTITY_CLIENT,
                            "com.htc.cs.identity.provider.SignInProvider"),
                    0);
            if (info != null &&
                    TextUtils.equals(info.authority, HtcAccountDefs.SIGNIN_PROVIDER_AUTHORITY)) {
                return true;
            }
        } catch (PackageManager.NameNotFoundException e) {
            // old identity client, not support app sign-in
            mLogger.debugS("Cannot found sign-in provider: ", e.getMessage());
        }
        return false;
    }

    private String getAccountTag() {
        if (isSupportAppSignIn()) {
            Cursor cursor =mContext.getContentResolver().query(
                    HtcAccountDefs.CONTENT_URI_SIGNIN_PROVIDER,
                    new String[] {HtcAccountDefs.KEY_SIGNIN_TIMESTAMP}, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                String tag = cursor.getString(0);
                cursor.close();
                return tag;
            }
        }
        return null;
    }
}
