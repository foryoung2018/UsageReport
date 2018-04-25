package com.htc.lib1.cs.auth.client;

import android.accounts.Account;
import android.content.Context;
import android.text.TextUtils;

import com.htc.lib1.cs.account.HtcAccountDefs;
import com.htc.lib1.cs.account.HtcAccountManager;
import com.htc.lib1.cs.account.OAuth2ConfigHelper;
import com.htc.lib1.cs.account.OAuth2RestServiceException;
import com.htc.lib1.cs.account.restservice.AuthorizationResource;
import com.htc.lib1.cs.account.restservice.IdentityJsonClasses;
import com.htc.lib1.cs.account.restservice.TokenResource;
import com.htc.lib1.cs.account.server.HtcAccountServerHelper;
import com.htc.lib1.cs.auth.AuthLoggerFactory;
import com.htc.lib1.cs.auth.web.TokenDefs;
import com.htc.lib1.cs.auth.web.WebAuthConfig;
import com.htc.lib1.cs.httpclient.ConnectionException;
import com.htc.lib1.cs.httpclient.ConnectivityException;
import com.htc.lib1.cs.httpclient.HttpException;
import com.htc.lib1.cs.logging.HtcLogger;

import java.io.IOException;

/**
 * Created by leohsu on 2017/4/8.
 */

public class ClientAuthenticatorHelper {
    private static HtcLogger sLogger = new AuthLoggerFactory(ClientAuthenticatorHelper.class).create();

    public static boolean isLocalAccountAuthorizedByClient(HtcAccountManager accntManager,
                                                           Account account) {
        return Boolean.valueOf(
                accntManager.getUserData(account, HtcAccountDefs.KEY_LOCAL_ACCOUNT_AUTH_BY_CLIENT))
                .booleanValue();
    }

    public static String getAuthToken(Context context,
                                      HtcAccountManager accntManager,
                                      Account account,
                                      String authTokenType)
            throws HttpException, InterruptedException, ConnectivityException,
            ConnectionException, IOException {

        // For local account authorized by client app rather than WebView
        OAuth2ConfigHelper.AuthClient client = WebAuthConfig.get(context).getAuthClient();

        String serverUri = accntManager.getUserData(account, HtcAccountDefs.KEY_SERVER_URI);
        if (TextUtils.isEmpty(serverUri)) {
            sLogger.warning("No server uri specified. Use default uri.");
            serverUri = HtcAccountServerHelper.getDefaultServiceUri(context);
        }

        String appId = context.getPackageName();

        boolean isAuthKeyRenewed = false;
        String authKey = accntManager.peekAuthToken(account, TokenDefs.TYPE_LOCAL_ACCOUNT_TOKEN);
        if (TextUtils.isEmpty(authKey)) {
            authKey = renewLocalAccountToken(context, accntManager, account, appId, serverUri);
            isAuthKeyRenewed = true;
        }

        SignatureHelper sigHelper = new SignatureHelper(context, appId);
        String sig = sigHelper.getHexSha1HashString();

        IdentityJsonClasses.WAccessToken token;
        try {
            AuthorizationResource res =
                    new AuthorizationResource(context, serverUri, authKey, appId);
            token = res.grantAccessToken(client.id, appId, sig, client.scopes);
        } catch (OAuth2RestServiceException.OAuth2InvalidAuthKeyException e) {
            sLogger.debug("Master token may expired. Refresh token now.");
            accntManager.invalidateAuthToken(authKey);
            if (isAuthKeyRenewed) {
                throw e;
            }
            // Renew local account token and try again.
            authKey = renewLocalAccountToken(context, accntManager, account, appId, serverUri);
            AuthorizationResource res =
                    new AuthorizationResource(context, serverUri, authKey, appId);
            token = res.grantAccessToken(client.id, appId, sig, client.scopes);
        }

        accntManager.setAuthToken(account, authTokenType, token.access_token);
        return token.access_token;
    }

    private static String renewLocalAccountToken(Context context,
                                       HtcAccountManager accntManager,
                                       Account account,
                                       String appId,
                                       String serverUri)
            throws HttpException, InterruptedException, ConnectivityException,
            ConnectionException, IOException {
        TokenResource res = new TokenResource(context, serverUri, appId);
        String mRefreshToken = accntManager.peekAuthToken(account, TokenDefs.TYPE_REFRESH_TOKEN);
        IdentityJsonClasses.WMasterToken token = res.refreshAuthkey(mRefreshToken);

        accntManager.setAuthToken(account, TokenDefs.TYPE_REFRESH_TOKEN, token.refresh_token);
        accntManager.setAuthToken(account, TokenDefs.TYPE_LOCAL_ACCOUNT_TOKEN, token.access_token);

        return token.access_token;
    }
}
