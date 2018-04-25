
package com.htc.lib1.cs.account;

import java.io.IOException;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.htc.lib1.cs.logging.CommLoggerFactory;
import com.htc.lib1.cs.logging.HtcLogger;

/**
 * Helper class built on top of {@link HtcAccountManager} to operate on an HTC
 * Account.
 * 
 * @author samael_wang@htc.com
 */
public class HtcAccountHelper {
    protected HtcLogger mLogger = new CommLoggerFactory(this).create();
    protected HtcAccountManager mAccntManager;
    protected Context mContext;

    /**
     * Create an instance.
     * 
     * @param context Context to operate on.
     * @param manager Account manager instance to use.
     */
    public HtcAccountHelper(Context context, HtcAccountManager manager) {
        if (context == null)
            throw new IllegalArgumentException("'context' is null.");
        if (manager == null)
            throw new IllegalArgumentException("'manager' is null.");

        mContext = context;
        mAccntManager = manager;
        mLogger.verbose("context=", context);
    }

    /**
     * Create an instance.
     * 
     * @param context Context to operate on.
     */
    public HtcAccountHelper(Context context) {
        this(context, HtcAccountManagerCreator.get().create(context));
    }

    /**
     * Test if HTC Account exists on the system.
     * 
     * @return True if account exists.
     */
    public boolean accountExists() {
        boolean exist = mAccntManager.getAccountsByType(HtcAccountDefs.TYPE_HTC_ACCOUNT).length > 0;
        mLogger.verbose("accountExists=", exist);
        return exist;
    }

    /**
     * Get the HTC Account from {@link AccountManager}.
     * 
     * @return An {@link Account} representing HTC account or {@code null} if
     *         non exists.
     */
    public Account getAccount() {
        Account[] accounts = mAccntManager.getAccountsByType(HtcAccountDefs.TYPE_HTC_ACCOUNT);

        // Return the primary account.
        Account accnt = accounts.length != 0 ? accounts[0] : null;
        mLogger.verboseS("account=", accnt);
        return accnt;
    }

    /**
     * Get the account name of the signed in HTC Account.
     * 
     * @return Account name displayed in Account & Sync.
     * @throws HtcAccountNotExistsException If no existing HTC Account presents.
     */
    public String getName() throws HtcAccountNotExistsException {
        Account account = getAccount();
        if (account == null)
            throw new HtcAccountNotExistsException();

        mLogger.verboseS("accountName=", account.name);
        return account.name;
    }

    /**
     * Remove the HTC Account from the system. Do nothing if no HTC Account
     * exists in system.
     * 
     * @throws IOException If the operation failed.
     */
    public void removeAccount() throws IOException {
        mLogger.verbose();
        Account account = getAccount();
        if (account != null) {
            try {
                mAccntManager.removeAccount(account, null, null).getResult();
            } catch (Exception e) {
                throw new IOException(e.getMessage(), e);
            }
        }
    }

    /**
     * Add an HTC Account and block waiting for the result. Must not be invoked
     * from main thread.
     * 
     * @param requestType Request type. The supported options are
     *            {@link HtcAccountDefs#TYPE_SIGN_IN_SIGN_UP},
     *            {@link HtcAccountDefs#TYPE_SIGN_IN},
     *            {@link HtcAccountDefs#TYPE_SIGN_IN_EXT},
     *            {@link HtcAccountDefs#TYPE_SIGN_IN_FACEBOOK},
     *            {@link HtcAccountDefs#TYPE_SIGN_IN_GOOGLE},
     *            {@link HtcAccountDefs#TYPE_SIGN_IN_SINA},
     *            {@link HtcAccountDefs#TYPE_SIGN_IN_QQ}.
     * @param email Email address to prefill. Could be {@code null}.
     * @return The name of the created account.
     * @throws IOException If the authenticator experienced an I/O problem
     *             creating a new account, usually because of network trouble.
     * @throws AuthenticatorException If no authenticator was registered for
     *             this account type or the authenticator failed to respond.
     * @throws OperationCanceledException If the operation was canceled for any
     *             reason, including the user canceling the creation process.
     */
    public String addAccount(String requestType, String email) throws OperationCanceledException,
            AuthenticatorException, IOException {
        mLogger.verboseS("addAccount: requestType: ", requestType, ", email: ", email);

        // Check state.
        if (!(mContext instanceof Activity))
            throw new IllegalStateException(
                    "The context must be an activity to invoke addAccount().");

        // Compose options.
        Bundle options = new Bundle();
        options.putString(HtcAccountDefs.KEY_REQUEST_TYPE, requestType);
        if (!TextUtils.isEmpty(email))
            options.putString(HtcAccountDefs.KEY_EMAIL_ADDRESS, email);

        // Add account.
        Bundle result = mAccntManager.addAccount(HtcAccountDefs.TYPE_HTC_ACCOUNT,
                HtcAccountDefs.AUTHTOKEN_TYPE_DEFAULT, null, options, (Activity) mContext, null,
                null).getResult();

        // Extract result.r
        if (result.containsKey(AccountManager.KEY_ACCOUNT_NAME)) {
            return result.getString(AccountManager.KEY_ACCOUNT_NAME);
        } else {
            // The returned result is invalid.
            throw new AuthenticatorException("Unexpected result for AccountManager.addAccount().");
        }
    }

    /**
     * Add an HTC Account and block waiting for the result.Must not be invoked
     * from main thread.
     * 
     * @param requestType Request type. The supported options are
     *            {@link HtcAccountDefs#TYPE_SIGN_IN_SIGN_UP},
     *            {@link HtcAccountDefs#TYPE_SIGN_IN},
     *            {@link HtcAccountDefs#TYPE_SIGN_IN_EXT},
     *            {@link HtcAccountDefs#TYPE_SIGN_IN_FACEBOOK},
     *            {@link HtcAccountDefs#TYPE_SIGN_IN_GOOGLE},
     *            {@link HtcAccountDefs#TYPE_SIGN_IN_SINA},
     *            {@link HtcAccountDefs#TYPE_SIGN_IN_QQ}.
     * @return The name of the created account.
     * @throws IOException If the authenticator experienced an I/O problem
     *             creating a new account, usually because of network trouble.
     * @throws AuthenticatorException If no authenticator was registered for
     *             this account type or the authenticator failed to respond.
     * @throws OperationCanceledException If the operation was canceled for any
     *             reason, including the user canceling the creation process.
     */
    public String addAccount(String requestType) throws OperationCanceledException,
            AuthenticatorException, IOException {
        return addAccount(requestType, null);
    }

    /**
     * Get the authtoken and block waiting for the result. Must not be invoked
     * from main thread.
     * 
     * @param authTokenType Type of the authtoken. Usually
     *            {@link HtcAccountDefs#AUTHTOKEN_TYPE_DEFAULT}.
     * @param notifyAuthFailure True to generate a notification if the caller is
     *            not operated on an activity and the authenticator requires to
     *            initiate UI flows to recover an error.
     * @return The asked authtoken.
     * @throws IOException If the authenticator returned an error response that
     *             indicates that it encountered an IOException while
     *             communicating with the authentication server.
     * @throws AuthenticatorException If no authenticator was registered for
     *             this account type or the authenticator failed to respond.
     * @throws OperationCanceledException If the operation was canceled for any
     *             reason, including the user canceling the creation process.
     * @throws RecoverableAuthenticationFailureException If authenticator
     *             indicates a sign-in error occurs and user needs to initiate
     *             UI flows to recover.
     * @throws HtcAccountNotExistsException If no existing HTC Account presents.
     */
    public String getAuthToken(String authTokenType, boolean notifyAuthFailure) throws IOException,
            RecoverableAuthenticationFailureException, OperationCanceledException,
            AuthenticatorException,
            HtcAccountNotExistsException {
        mLogger.verboseS("getAuthToken: authTokenType: ", authTokenType, ", notifyAuthFailure",
                notifyAuthFailure);

        // Test account.
        Account account = getAccount();
        if (account == null)
            throw new HtcAccountNotExistsException();

        // Pass activity whenever possible.
        AccountManagerFuture<Bundle> future;
        if (mContext instanceof Activity) {
            future = mAccntManager.getAuthToken(account, authTokenType, null,
                    (Activity) mContext, null, null);
        } else {
            future = mAccntManager.getAuthToken(account, authTokenType, null,
                    notifyAuthFailure, null, null);
        }

        Bundle result = future.getResult();
        if (result.containsKey(AccountManager.KEY_AUTHTOKEN)) {
            return result.getString(AccountManager.KEY_AUTHTOKEN);
        } else if (result.containsKey(AccountManager.KEY_INTENT)) {
            Intent intent = result.getParcelable(AccountManager.KEY_INTENT);
            throw new RecoverableAuthenticationFailureException(intent);
        } else {
            // The returned result is invalid.
            throw new AuthenticatorException("Unexpected result from getAuthToken().");
        }
    }

    /**
     * Get the authtoken and block waiting for the result. Must not be invoked
     * from main thread.
     * 
     * @param authTokenType Type of the authtoken. Usually
     *            {@link HtcAccountDefs#AUTHTOKEN_TYPE_DEFAULT}.
     * @return The asked authtoken.
     * @throws IOException If the authenticator returned an error response that
     *             indicates that it encountered an IOException while
     *             communicating with the authentication server.
     * @throws AuthenticatorException If no authenticator was registered for
     *             this account type or the authenticator failed to respond.
     * @throws OperationCanceledException If the operation was canceled for any
     *             reason, including the user canceling the creation process.
     * @throws RecoverableAuthenticationFailureException If authenticator
     *             indicates a sign-in error occurs and user needs to initiate
     *             UI flows to recover.
     * @throws HtcAccountNotExistsException If no existing HTC Account presents.
     */
    public String getAuthToken(String authTokenType) throws IOException,
            RecoverableAuthenticationFailureException, OperationCanceledException,
            AuthenticatorException, HtcAccountNotExistsException {
        return getAuthToken(authTokenType, true);
    }

    /**
     * Invalidate the given authtoken. Do nothing if no HTC Account presents on
     * the system.
     * 
     * @param token Authtoken.
     */
    public void invalidateAuthToken(String token) {
        mLogger.verbose();
        Account account = getAccount();
        if (account != null)
            mAccntManager.invalidateAuthToken(HtcAccountDefs.TYPE_HTC_ACCOUNT, token);
    }

    public void setAuthToken(String authTokenType, String authToken)
            throws HtcAccountNotExistsException {
        mLogger.verboseS("setAuthToken: authTokenType: ", authTokenType, ", authToken: ", authToken);

        // Test account.
        Account account = getAccount();
        if (account == null)
            throw new HtcAccountNotExistsException();

        // Set auth-token.
        mAccntManager.setAuthToken(account, authTokenType, authToken);
    }

}
