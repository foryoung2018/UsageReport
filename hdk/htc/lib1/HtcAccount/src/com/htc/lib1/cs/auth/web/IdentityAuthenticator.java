
package com.htc.lib1.cs.auth.web;

import java.io.IOException;
import java.util.Arrays;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.accounts.OperationCanceledException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;

import com.htc.lib1.cs.account.AbstractHtcAccountAuthenticator;
import com.htc.lib1.cs.account.DataServiceFuture;
import com.htc.lib1.cs.account.DataServiceFuture.DataServiceCallback;
import com.htc.lib1.cs.account.HtcAccount;
import com.htc.lib1.cs.account.HtcAccountAuthenticatorResponse;
import com.htc.lib1.cs.account.HtcAccountDefs;
import com.htc.lib1.cs.account.HtcAccountManager;
import com.htc.lib1.cs.account.HtcAccountManagerCreator;
import com.htc.lib1.cs.account.OAuth2ConfigHelper;
import com.htc.lib1.cs.account.OAuth2RestServiceException;
import com.htc.lib1.cs.account.restservice.AuthorizationResource;
import com.htc.lib1.cs.account.restservice.IdentityJsonClasses;
import com.htc.lib1.cs.account.server.HtcAccountServerHelper;
import com.htc.lib1.cs.app.EmailNotificationActivity;
import com.htc.lib1.cs.auth.AuthLoggerFactory;
import com.htc.lib1.cs.auth.client.ClientAuthenticatorHelper;
import com.htc.lib1.cs.httpclient.HttpException;
import com.htc.lib1.cs.logging.HtcLogger;
import com.htc.lib1.cs.security.ProviderInstallerUtils;

/**
 * Authenticator which implements account manager interface. All callbacks work
 * in a background thread as IAccountAuthenticator.aidl is configured as oneway
 * in Android framework.
 * 
 * @author samael_wang@htc.com
 */
public class IdentityAuthenticator extends AbstractHtcAccountAuthenticator {
    private HtcLogger mLogger = new AuthLoggerFactory(this).create();
    private Context mAppContext;

    public IdentityAuthenticator(Context context) {
        super(context);
        if (context == null)
            throw new IllegalArgumentException("'context' is null.");
        mAppContext = context.getApplicationContext();
    }

    @Override
    public Bundle editProperties(HtcAccountAuthenticatorResponse response, String accountType) {
        mLogger.error("Unsupported operation.");
        Bundle result = new Bundle();
        result.putInt(AccountManager.KEY_ERROR_CODE, AccountManager.ERROR_CODE_BAD_REQUEST);
        result.putString(AccountManager.KEY_ERROR_MESSAGE, "Unsupported operation.");

        mLogger.debugS(result);
        return ensureAuthenticatorResponse(result, response);
    }

    @Override
    public Bundle addAccount(HtcAccountAuthenticatorResponse response, String accountType,
            String authTokenType, String[] requiredFeatures, Bundle options)
            throws NetworkErrorException {
        if (response == null)
            throw new IllegalArgumentException("'response' is null.");

        ProviderInstallerUtils.tryInstallIfNeed(mAppContext);

        mLogger.info("Adding account.");
        mLogger.debugS("accountType=", accountType, ", authTokenType=", authTokenType,
                ", requiredFeatures=",
                (requiredFeatures == null ? "[]" : Arrays.toString(requiredFeatures)),
                ", options=", options);

        Bundle result = new Bundle();
        if (HtcAccountManagerCreator.get().create(mAppContext)
                .getAccountsByType(HtcAccountDefs.TYPE_HTC_ACCOUNT).length > 0) {
            mLogger.error("An HTC account already exists in the database.");
            result.putInt(AccountManager.KEY_ERROR_CODE, AccountManager.ERROR_CODE_CANCELED);
            result.putString(AccountManager.KEY_ERROR_MESSAGE,
                    "An HTC account already exists in the database.");
        } else {
            Intent intent = new Intent(mAppContext, AddAccountAuthenticatorActivity.class);
            intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
            intent.putExtra(AddAccountAuthenticatorActivity.EXTRA_KEY_AUTH_TOKEN_TYPE, authTokenType);
            result.putParcelable(AccountManager.KEY_INTENT, intent);
        }

        mLogger.debugS(result);
        return ensureAuthenticatorResponse(result, response);
    }

    @Override
    public Bundle confirmCredentials(HtcAccountAuthenticatorResponse response,
            HtcAccount htcAccount,
            Bundle options) throws NetworkErrorException {
        mLogger.error("Unsupported operation.");
        Bundle result = new Bundle();
        result.putInt(AccountManager.KEY_ERROR_CODE, AccountManager.ERROR_CODE_BAD_REQUEST);
        result.putString(AccountManager.KEY_ERROR_MESSAGE, "Unsupported operation.");

        mLogger.debugS(result);
        return ensureAuthenticatorResponse(result, response);
    }

    @Override
    public Bundle getAuthToken(HtcAccountAuthenticatorResponse response, HtcAccount htcAccount,
            String authTokenType, Bundle options) throws NetworkErrorException {
        mLogger.info("Getting authToken.");
        mLogger.debugS("htcAccount=", htcAccount, ", authTokenType=", authTokenType, ", options=",
                options);
        ProviderInstallerUtils.tryInstallIfNeed(mAppContext);

        /*
         * For webauth client, the token type is ignored as it provides only one
         * type of access token.
         */

        // Try to get token from cache.
        Account account = htcAccount.toAccount();
        HtcAccountManager accntManager =
                HtcAccountManagerCreator.get().createAsAuthenticator(mAppContext);

        String authToken;
        boolean tokenRenewed = false;
        try {
            if (ClientAuthenticatorHelper.isLocalAccountAuthorizedByClient(
                    accntManager, account)) {
                authToken = accntManager.peekAuthToken(account, authTokenType);
                if (TextUtils.isEmpty(authToken)) {
                    authToken = ClientAuthenticatorHelper.getAuthToken(
                            mAppContext, accntManager, account, authTokenType);
                    tokenRenewed = true;
                }
            } else {
                authToken = accntManager.peekAuthToken(account, TokenDefs.TYPE_ACCESS_TOKEN);

                if (TextUtils.isEmpty(authToken)) {
                    // Try to refresh access token.
                    String refreshToken = accntManager.peekAuthToken(account, TokenDefs.TYPE_REFRESH_TOKEN);
                    if (!TextUtils.isEmpty(refreshToken)) {
                        mLogger.debug("Refreshing access token...");
                        WebAuthResource resource = new WebAuthResource(mAppContext,
                                WebAuthConfig.get(mAppContext).getBaseUri());

                        // Try to refresh token.
                        Token token = new Token(resource.refreshToken(refreshToken));
                        authToken = token.getAccessToken();
                        refreshToken = token.getRefreshToken();

                        // Update cache.
                        accntManager.setAuthToken(account, TokenDefs.TYPE_ACCESS_TOKEN, authToken);
                        accntManager.setAuthToken(account, TokenDefs.TYPE_REFRESH_TOKEN, refreshToken);

                        tokenRenewed = true;
                    } else {
                        // Re-signin.
                        mLogger.debug("Refresh token not available. Try re-signin instead.");
                        return triggerReSignIn(response, htcAccount);
                    }
                } else {
                    mLogger.debug("Cache hit. Return immeidately");
                }
            }
        } catch (OAuth2RestServiceException.OAuth2UnverifiedUserException e) {
            mLogger.warning("Need verify user email");
            return triggerEmailVerification(response, htcAccount);
        } catch (HttpException e) {
            // Re-signin.
            mLogger.error(e);
            mLogger.debug("Server returns an error. Try re-signin instead.");
            return triggerReSignIn(response, htcAccount);
        } catch (Exception e) {
            mLogger.error(e);
            Bundle result = new Bundle();
            result.putInt(AccountManager.KEY_ERROR_CODE,
                    AccountManager.ERROR_CODE_REMOTE_EXCEPTION);
            result.putString(AccountManager.KEY_ERROR_MESSAGE, e.getMessage());
            return ensureAuthenticatorResponse(result, response);
        }

        if (tokenRenewed) {
            // Send broadcast.
            BroadcastUtils.authTokenRenewed(mAppContext,
                    HtcAccountDefs.AUTHTOKEN_TYPE_DEFAULT);
        }

        // Return the authtoken.
        Bundle result = new Bundle();
        result.putString(AccountManager.KEY_AUTHTOKEN, authToken);
        result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
        result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
        result.putString(HtcAccountDefs.KEY_ACCOUNT_ID,
                accntManager.getUserData(account, HtcAccountDefs.KEY_ACCOUNT_ID));

        mLogger.debugS(result);
        return result;
    }

    /**
     * Trigger the re-signin flow.
     * 
     * @param response {@code response} object from
     *            {@link #getAuthToken(HtcAccountAuthenticatorResponse, HtcAccount, String, Bundle)}
     *            call.
     * @return {@link Bundle} to use as the return value of
     *         {@link #getAuthToken(HtcAccountAuthenticatorResponse, HtcAccount, String, Bundle)}
     *         .
     */
    private Bundle triggerReSignIn(HtcAccountAuthenticatorResponse response, HtcAccount htcAccount) {

        Bundle result;
        try {
            // Generate re-signin intent.
            Intent intent = new Intent(mAppContext, AddAccountAuthenticatorActivity.class);
            intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
            result = new Bundle();
            result.putParcelable(AccountManager.KEY_INTENT, intent);

        } catch (RuntimeException e) {
            mLogger.error(e);
            Bundle bundle = new Bundle();
            bundle.putInt(AccountManager.KEY_ERROR_CODE, AccountManager.ERROR_CODE_CANCELED);
            bundle.putString(AccountManager.KEY_ERROR_MESSAGE, e.getMessage());
            result = ensureAuthenticatorResponse(bundle, response);
        }

        mLogger.debugS(result);
        return result;
    }

    private Bundle triggerEmailVerification(HtcAccountAuthenticatorResponse response, HtcAccount htcAccount) {
        Bundle result;
        try {
            Intent intent = new Intent(mAppContext, EmailNotificationActivity.class);
            intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
            result = new Bundle();
            result.putParcelable(AccountManager.KEY_INTENT, intent);

        } catch (RuntimeException e) {
            mLogger.error(e);
            Bundle bundle = new Bundle();
            bundle.putInt(AccountManager.KEY_ERROR_CODE, AccountManager.ERROR_CODE_CANCELED);
            bundle.putString(AccountManager.KEY_ERROR_MESSAGE, e.getMessage());
            result = ensureAuthenticatorResponse(bundle, response);
        }

        mLogger.debugS(result);
        return result;
    }

    @Override
    public String getAuthTokenLabel(String authTokenType) {
        mLogger.verbose("authTokenType: ", authTokenType);
        return authTokenType;
    }

    @Override
    public Bundle updateCredentials(HtcAccountAuthenticatorResponse response,
            HtcAccount htcAccount,
            String authTokenType, Bundle options) throws NetworkErrorException {
        mLogger.error("Unsupported operation.");
        Bundle result = new Bundle();
        result.putInt(AccountManager.KEY_ERROR_CODE, AccountManager.ERROR_CODE_BAD_REQUEST);
        result.putString(AccountManager.KEY_ERROR_MESSAGE, "Unsupported operation.");

        mLogger.debugS(result);
        return ensureAuthenticatorResponse(result, response);
    }

    @Override
    public Bundle hasFeatures(HtcAccountAuthenticatorResponse response, HtcAccount htcAccount,
            String[] features) throws NetworkErrorException {
        mLogger.error("Unsupported operation.");
        Bundle result = new Bundle();
        result.putInt(AccountManager.KEY_ERROR_CODE, AccountManager.ERROR_CODE_BAD_REQUEST);
        result.putString(AccountManager.KEY_ERROR_MESSAGE, "Unsupported operation.");

        mLogger.debugS(result);
        return ensureAuthenticatorResponse(result, response);
    }

    /**
     * Ensure the {@link HtcAccountAuthenticatorResponse} is set with given
     * result so errors are not only returned but also passed to the response
     * object.
     * 
     * @param result Result bundle.
     * @param response Response to set.
     * @return The given result bundle.
     */
    private Bundle ensureAuthenticatorResponse(Bundle result,
            HtcAccountAuthenticatorResponse response) {
        // Pass the error code / message.
        if (result.containsKey(AccountManager.KEY_ERROR_CODE)) {
            response.onError(result.getInt(AccountManager.KEY_ERROR_CODE),
                    result.getString(AccountManager.KEY_ERROR_MESSAGE));
        }

        return result;
    }

    @Override
    public Bundle getAccountRemovalAllowed(HtcAccountAuthenticatorResponse response,
            HtcAccount htcAccount)
            throws NetworkErrorException {
        mLogger.verbose();

        // Register receiver and wait for LOGIN_ACCOUNTS_CHANGED_ACTION.
        LocalBroadcastManager.getInstance(mAppContext).registerReceiver(
                new OnAccountRemovedReceiver(),
                new IntentFilter(AccountManager.LOGIN_ACCOUNTS_CHANGED_ACTION));

        return super.getAccountRemovalAllowed(response, htcAccount);
    }

    /**
     * Receiver for HTC Account removal broadcast.
     * 
     * @author samael_wang@htc.com
     */
    private static class OnAccountRemovedReceiver extends BroadcastReceiver {
        private HtcLogger mmLogger = new AuthLoggerFactory(this).create();

        @Override
        public void onReceive(final Context context, Intent intent) {
            mmLogger.verboseS(intent);

            // Create the callback.
            DataServiceCallback<Account[]> callback = new DataServiceCallback<Account[]>() {

                @Override
                public void run(DataServiceFuture<Account[]> future) {
                    try {
                        if (future.getResult().length == 0) {
                            mmLogger.info("HTC Account is removed from LocalAccountManager.");

                            LocalBroadcastManager.getInstance(context)
                                    .unregisterReceiver(
                                            OnAccountRemovedReceiver.this);
                            BroadcastUtils.removeAccountCompleted(context);
                        }
                    } catch (OperationCanceledException e) {
                        mmLogger.error(e);
                    } catch (IOException e) {
                        mmLogger.error(e);
                    }
                }
            };

            // Start running...
            HtcAccountManagerCreator.get().create(context)
                    .asyncGetAccountsByType(HtcAccountDefs.TYPE_HTC_ACCOUNT, callback, null);
        }
    }
}
