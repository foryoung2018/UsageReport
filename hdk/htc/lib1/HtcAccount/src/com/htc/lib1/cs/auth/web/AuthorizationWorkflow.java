
package com.htc.lib1.cs.auth.web;

import android.accounts.Account;
import android.content.Context;
import android.text.TextUtils;

import com.htc.lib1.cs.account.HtcAccountDefs;
import com.htc.lib1.cs.account.HtcAccountManager;
import com.htc.lib1.cs.account.HtcAccountManagerCreator;
import com.htc.lib1.cs.account.restobj.HtcAccountProfile;
import com.htc.lib1.cs.workflow.UnexpectedException;
import com.htc.lib1.cs.workflow.Workflow;

/**
 * Workflow to authorize the client using authorization code, get the profile
 * and add an account to {@link HtcAccountManager}.
 * 
 * @author samael_wang@htc.com
 */
public class AuthorizationWorkflow implements Workflow<Account> {
    private Context mContext;
    private String mAuthCode;
    private String mClientID;
    private String mClientSecret;
    private String mRedirectUrl;

    /**
     * Create an instance.
     * 
     * @param context Context to operate on.
     * @param clientId Client ID.
     * @param clientSecret Client secret.
     * @param authCode Authorization code.
     * @param redirectUrl Redirect URL to use which should be the same as what
     *            specified when retrieving the authorization code.
     */
    public AuthorizationWorkflow(Context context, String clientId, String clientSecret,
            String authCode, String redirectUrl) {
        if (context == null)
            throw new IllegalArgumentException("'context' is null.");
        if (TextUtils.isEmpty(authCode))
            throw new IllegalArgumentException("'authCode' is null or empty.");
        if (TextUtils.isEmpty(clientId))
            throw new IllegalArgumentException("'clientId' is null or empty.");
        if (TextUtils.isEmpty(clientSecret))
            throw new IllegalArgumentException("'clientSecret' is null or empty.");
        if (TextUtils.isEmpty(redirectUrl))
            throw new IllegalArgumentException("'redirectUrl' is null or empty.");

        mContext = context;
        mAuthCode = authCode;
        mClientID = clientId;
        mClientSecret = clientSecret;
        mRedirectUrl = redirectUrl;
    }

    @Override
    public Account execute() throws UnexpectedException {
        WebAuthResource resource = new WebAuthResource(mContext, WebAuthConfig.get(mContext)
                .getBaseUri());

        // Get token and profile.
        Token token;
        HtcAccountProfile profile;
        try {
            token = new Token(resource.getTokenByCode(mClientID, mClientSecret, mAuthCode,
                    mRedirectUrl));
            profile = (HtcAccountProfile) resource.getAccountProfile(token.getAccessToken())
                    .isValidOrThrow();
        } catch (Exception e) {
            throw new UnexpectedException(e.getMessage(), e);
        }

        // Find proper field to be used as the account name.
        String accntName;
        if (!TextUtils.isEmpty(profile.firstName)) {
            accntName = profile.firstName +
                    ((TextUtils.isEmpty(profile.lastName)) ? "" : " " + profile.lastName);
        } else if (!TextUtils.isEmpty(profile.emailAddress)) {
            accntName = profile.emailAddress;
        } else {
            // Use account id as the fallback.
            accntName = profile.accountId;
        }

        // If a local account presents for any reason, remove the existing one.
        HtcAccountManager accountManager =
                HtcAccountManagerCreator.get().createAsAuthenticator(mContext);
        for (Account exisingAccnt : accountManager
                .getAccountsByType(HtcAccountDefs.TYPE_HTC_ACCOUNT)) {
            accountManager.removeAccount(exisingAccnt, null, null);
        }

        // Add the account.
        Account account = new Account(accntName, HtcAccountDefs.TYPE_HTC_ACCOUNT);
        accountManager.addAccountExplicitly(account, null, null);
        accountManager.setAuthToken(account, TokenDefs.TYPE_REFRESH_TOKEN, token.getRefreshToken());
        accountManager.setAuthToken(account, TokenDefs.TYPE_ACCESS_TOKEN, token.getAccessToken());
        accountManager.setUserData(account, HtcAccountDefs.KEY_ACCOUNT_ID, profile.accountId);

        return account;
    }

}
