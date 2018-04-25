
package com.htc.lib1.cs.auth.web;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.text.TextUtils;

import com.htc.lib1.cs.StringUtils;
import com.htc.lib1.cs.account.restobj.HtcAccountProfile;
import com.htc.lib1.cs.auth.AuthLoggerFactory;
import com.htc.lib1.cs.googleads.GoogleAdvertiseUtils;
import com.htc.lib1.cs.httpclient.ConnectionException;
import com.htc.lib1.cs.httpclient.ConnectivityException;
import com.htc.lib1.cs.httpclient.HttpClient;
import com.htc.lib1.cs.httpclient.HttpException;
import com.htc.lib1.cs.httpclient.JsonInputStreamReader;
import com.htc.lib1.cs.httpclient.StringOutputStreamWriter;
import com.htc.lib1.cs.httpclient.URLEncodedUtils;
import com.htc.lib1.cs.httpclient.URLEncodedUtils.NameValuePair;
import com.htc.lib1.cs.logging.HtcLogger;

/**
 * REST resource for webview authorization.
 * 
 * @author samael_wang@htc.com
 */
/* package */class WebAuthResource {
    private static final int HTTP_TIMEOUT_SECONDS = 10;
    private HtcLogger mLogger = new AuthLoggerFactory(this).create();
    private String mServerUri;
    private HttpClient mHttpClient;

    /**
     * Construct a {@link WebAuthResource}
     * 
     * @param context Application context.
     * @param serverUri Sense server URI.
     */
    public WebAuthResource(Context context, String serverUri) {
        // Test arguments.
        if (context == null)
            throw new IllegalArgumentException("'context' is null.");
        if (TextUtils.isEmpty(serverUri))
            throw new IllegalArgumentException("'serverUri' is null or empty.");

        mServerUri = StringUtils.ensureTrailingSlash(serverUri);
        Map<String, String> header = new HashMap<String, String>();
        header.put("Accept", "application/json");
        header.put("X-HTC-GA-ID", GoogleAdvertiseUtils.getAdvertisingId(context));
        mHttpClient = new HttpClient(context, header);
    }

    /**
     * Get the access token and refresh token by using authorization code.
     * 
     * @param clientId Client ID.
     * @param clientSecret Client secret.
     * @param authCode Authorization code.
     * @param redirectUrl Redirect URL to use which should be the same as what
     *            specified when retrieving the authorization code.
     * @return {@link OAuth2TokenResponse}
     * @throws HttpException If server returns an error.
     * @throws IOException If server URI was malformed or other I/O error occurs
     *             when trying to send the data or read the response.
     * @throws ConnectionException When network error occurs when trying to make
     *             connection.
     * @throws ConnectivityException Data network is not available.
     * @throws InterruptedException If the thread is interrupted before
     *             complete.
     */
    public OAuth2TokenResponse getTokenByCode(String clientId, String clientSecret,
            String authCode, String redirectUrl) throws InterruptedException,
            ConnectivityException, ConnectionException, IOException, HttpException {
        mLogger.verbose();

        // Test arguments.
        if (TextUtils.isEmpty(clientId))
            throw new IllegalArgumentException("'clientId' is null or empty.");
        if (TextUtils.isEmpty(clientSecret))
            throw new IllegalArgumentException("'clientSecret' is null or empty.");
        if (TextUtils.isEmpty(authCode))
            throw new IllegalArgumentException("'authCode' is null or empty.");
        if (TextUtils.isEmpty(redirectUrl))
            throw new IllegalArgumentException("'redirectUrl' is null or empty.");

        // Compose URL.
        StringBuilder urlBuilder = new StringBuilder(mServerUri)
                .append("Services/OAuth/Token");
        URL url = new URL(urlBuilder.toString());

        // Compose payload.
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new NameValuePair(WebAuthDefs.PARAM_GRANT_TYPE,
                WebAuthDefs.GRANT_TYPE_AUTH_CODE));
        params.add(new NameValuePair(WebAuthDefs.PARAM_AUTH_CODE, authCode));
        params.add(new NameValuePair(WebAuthDefs.PARAM_CLIENT_ID, clientId));
        params.add(new NameValuePair(WebAuthDefs.PARAM_CLIENT_SECRET, clientSecret));
        params.add(new NameValuePair(WebAuthDefs.PARAM_REDIRECT_URL, redirectUrl));
        String payload = URLEncodedUtils.format(params);

        // Make REST call.
        return mHttpClient.post(mHttpClient.getRequestBuilder(url,
                new JsonInputStreamReader<OAuth2TokenResponse>() {
                }).setDataWriter(new StringOutputStreamWriter(payload))
                .addRequestProperty("Content-Type", "application/x-www-form-urlencoded")
                .build(), null, null).getResult(HTTP_TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }

    /**
     * Refresh the access token and refresh token.
     * 
     * @param refreshToken Refresh token used to refresh new tokens.
     * @return {@link OAuth2TokenResponse}
     * @throws HttpException If server returns an error.
     * @throws IOException If server URI was malformed or other I/O error occurs
     *             when trying to send the data or read the response.
     * @throws ConnectionException When network error occurs when trying to make
     *             connection.
     * @throws ConnectivityException Data network is not available.
     * @throws InterruptedException If the thread is interrupted before
     *             complete.
     */
    public OAuth2TokenResponse refreshToken(String refreshToken) throws InterruptedException,
            ConnectivityException, ConnectionException, IOException, HttpException {
        mLogger.verbose();

        // Test arguments.
        if (TextUtils.isEmpty(refreshToken))
            throw new IllegalArgumentException("'refreshToken' is null or empty.");

        // Compose URL.
        StringBuilder urlBuilder = new StringBuilder(mServerUri)
                .append("Services/OAuth/Token");
        URL url = new URL(urlBuilder.toString());

        // Compose payload.
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new NameValuePair(WebAuthDefs.PARAM_GRANT_TYPE,
                WebAuthDefs.GRANT_TYPE_REFRESH_TOKEN));
        params.add(new NameValuePair(WebAuthDefs.PARAM_REFRESH_TOKEN, refreshToken));
        String payload = URLEncodedUtils.format(params);

        // Make REST call.
        return mHttpClient.post(mHttpClient.getRequestBuilder(url,
                new JsonInputStreamReader<OAuth2TokenResponse>() {
                }).setDataWriter(new StringOutputStreamWriter(payload))
                .addRequestProperty("Content-Type", "application/x-www-form-urlencoded")
                .build(), null, null).getResult(HTTP_TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }

    /**
     * Get the account profile.
     * 
     * @param accessToken Access token.
     * @return {@link WAccountProfile}
     * @throws HttpException If server returns an error.
     * @throws IOException If server URI was malformed or other I/O error occurs
     *             when trying to send the data or read the response.
     * @throws ConnectionException When network error occurs when trying to make
     *             connection.
     * @throws ConnectivityException Data network is not available.
     * @throws InterruptedException If the thread is interrupted before
     *             complete.
     */
    public HtcAccountProfile getAccountProfile(String accessToken) throws InterruptedException,
            ConnectivityException, ConnectionException, IOException, HttpException {
        mLogger.verbose();

        if (TextUtils.isEmpty(accessToken))
            throw new IllegalArgumentException("'accessToken' is null or empty.");

        // Compose URL.
        StringBuilder urlBuilder = new StringBuilder(mServerUri)
                .append("Services/AccountsExtn.svc/Accounts/RetrieveAccountProfile/");
        URL url = new URL(urlBuilder.toString());

        // Make REST call.
        return mHttpClient.get(mHttpClient.getRequestBuilder(url,
                new JsonInputStreamReader<HtcAccountProfile>() {
                }).addRequestProperty("AuthKey", accessToken).build(), null, null).getResult(
                HTTP_TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }
}
