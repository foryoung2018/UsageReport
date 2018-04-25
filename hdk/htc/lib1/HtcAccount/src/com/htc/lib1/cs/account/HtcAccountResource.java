
package com.htc.lib1.cs.account;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;

import com.htc.lib1.cs.StringUtils;
import com.htc.lib1.cs.account.HtcAccountRestServiceException.TokenExpiredException;
import com.htc.lib1.cs.account.OAuth2RestServiceException.OAuth2InvalidAuthKeyException;
import com.htc.lib1.cs.account.restobj.HtcAccountProfile;
import com.htc.lib1.cs.account.restobj.TokenInfo;
import com.htc.lib1.cs.httpclient.HttpClient;
import com.htc.lib1.cs.httpclient.HttpConnectionCallback;
import com.htc.lib1.cs.httpclient.HttpConnectionFuture;
import com.htc.lib1.cs.httpclient.JsonInputStreamReader;
import com.htc.lib1.cs.logging.CommLoggerFactory;
import com.htc.lib1.cs.logging.HtcLogger;

/**
 * REST resource for HTC Account.
 * 
 * @author samael_wang@htc.com
 */
public class HtcAccountResource {
    private HtcLogger mLogger = new CommLoggerFactory(this).create();
    private String mServerUri;
    private HttpClient mHttpClient;

    /**
     * Construct a {@link HtcAccountResource}
     * 
     * @param context Context to operate on.
     * @param serverUri Identity server URI to use.
     */
    public HtcAccountResource(Context context, String serverUri) {
        // Test arguments.
        if (context == null)
            throw new IllegalArgumentException("'context' is null.");
        if (TextUtils.isEmpty(serverUri))
            throw new IllegalArgumentException("'serverUri' is null or empty.");

        mServerUri = StringUtils.ensureTrailingSlash(serverUri);

        Map<String, String> header = new HashMap<String, String>();
        header.put("Accept", "application/json");
        mHttpClient = new HttpClient(context, header);
    }

    /**
     * Construct a {@link HtcAccountResource} with default server URI.
     * 
     * @param context Context to operate on.
     */
    public HtcAccountResource(Context context) {
        this(context, HtcAccountDefs.DEFAULT_SERVER_URI);
    }

    /**
     * Get the account profile.
     * 
     * @param accessToken Access token.
     * @param callback Callback to invoke when the task completes.
     * @param handler Handler to run the callback on. If not given, the callback
     *            will be run in main thread.
     * @return {@link HttpConnectionFuture} which resolves to an
     *         {@link HtcAccountProfile}.
     *         <p>
     *         If the {@code accessToken} has expired,
     *         {@link HttpConnectionFuture#getResult()} will throw an
     *         {@link TokenExpiredException}. Caller should invoke
     *         {@link HtcAccountManager#invalidateAuthToken(String)} to
     *         invalidate the expired token and retrieve a new token through
     *         {@link HtcAccountManager#getAuthToken(android.accounts.Account, String, android.os.Bundle, android.app.Activity, android.accounts.AccountManagerCallback, Handler)}
     *         .
     */
    public HttpConnectionFuture<HtcAccountProfile> getAccountProfile(String accessToken,
            HttpConnectionCallback<HtcAccountProfile> callback, Handler handler) {
        mLogger.verbose();

        if (TextUtils.isEmpty(accessToken))
            throw new IllegalArgumentException("'accessToken' is null or empty.");

        // Compose URL.
        URL url;
        try {
            url = new URL(mServerUri + "Services/AccountsExtn.svc/Accounts/RetrieveAccountProfile/");
        } catch (MalformedURLException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }

        // Make REST call.
        return mHttpClient.get(
                mHttpClient.getRequestBuilder(url, new JsonInputStreamReader<HtcAccountProfile>() {
                }).setErrorReader(new HtcAccountRestErrorStreamReader())
                        .addRequestProperty("AuthKey", accessToken).build(), callback, handler);
    }

    /**
     * Verify the access token.
     * 
     * @param accessToken Access token.
     * @param callback Callback to invoke when the task completes.
     * @param handler Handler to run the callback on. If not given, the callback
     *            will be run in main thread.
     * @return {@link HttpConnectionFuture} which resolves to a
     *         {@link TokenInfo}.
     *         <p>
     *         If the {@code accessToken} has expired,
     *         {@link HttpConnectionFuture#getResult()} will throw an
     *         {@link OAuth2InvalidAuthKeyException}. Caller should invoke
     *         {@link HtcAccountManager#invalidateAuthToken(String)} to
     *         invalidate the expired token and retrieve a new token through
     *         {@link HtcAccountManager#getAuthToken(android.accounts.Account, String, android.os.Bundle, android.app.Activity, android.accounts.AccountManagerCallback, Handler)}
     *         .
     */
    public HttpConnectionFuture<TokenInfo> verifyToken(String accessToken,
            HttpConnectionCallback<TokenInfo> callback, Handler handler) {
        mLogger.verbose();

        if (TextUtils.isEmpty(accessToken))
            throw new IllegalArgumentException("'accessToken' is null or empty.");

        // Compose URL.
        URL url;
        try {
            url = new URL(mServerUri + "Services/OAuth/VerifyToken");
        } catch (MalformedURLException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }

        // Make REST call.
        return mHttpClient.get(
                mHttpClient.getRequestBuilder(url, new JsonInputStreamReader<TokenInfo>() {
                }).setErrorReader(new OAuth2ErrorStreamReader())
                        .addRequestProperty("AuthKey", accessToken).build(), callback, handler);
    }
}
