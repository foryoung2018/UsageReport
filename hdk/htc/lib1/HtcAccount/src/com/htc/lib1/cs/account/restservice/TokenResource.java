
package com.htc.lib1.cs.account.restservice;

import android.content.Context;
import android.text.TextUtils;

import com.htc.lib1.cs.StringUtils;
import com.htc.lib1.cs.account.OAuth2ErrorStreamReader;
import com.htc.lib1.cs.account.restservice.IdentityJsonClasses.WMasterToken;
import com.htc.lib1.cs.auth.AuthLoggerFactory;
import com.htc.lib1.cs.auth.client.IdentityDefs;
import com.htc.lib1.cs.httpclient.ConnectionException;
import com.htc.lib1.cs.httpclient.ConnectivityException;
import com.htc.lib1.cs.httpclient.HttpClient;
import com.htc.lib1.cs.httpclient.HttpException;
import com.htc.lib1.cs.httpclient.JsonInputStreamReader;
import com.htc.lib1.cs.httpclient.StringOutputStreamWriter;
import com.htc.lib1.cs.httpclient.URLEncodedUtils;
import com.htc.lib1.cs.httpclient.URLEncodedUtils.NameValuePair;
import com.htc.lib1.cs.logging.HtcLogger;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * REST resource to refresh token.
 */
public class TokenResource {
    private HtcLogger mLogger = new AuthLoggerFactory(this).create();
    private String mServerUri;
    private HttpClient mHttpClient;

    /**
     * Construct a {@link AuthorizationResource}
     * 
     * @param context Application context.
     * @param serverUri Sense server URI.
     * @param sourceService Source service name to identity which integrated
     *            service triggers the flow.
     */
    public TokenResource(Context context, String serverUri, String sourceService) {
        // Test arguments.
        if (context == null)
            throw new IllegalArgumentException("'context' is null.");
        if (TextUtils.isEmpty(serverUri))
            throw new IllegalArgumentException("'serverUri' is null or empty.");
        if (TextUtils.isEmpty(sourceService)) {
            throw new IllegalArgumentException("'sourceService' is null or empty.");
        }

        mServerUri = StringUtils.ensureTrailingSlash(serverUri);
        mHttpClient = new HttpClient(context, new OAuth2ErrorStreamReader(),
                new IdentityRequestPropertiesBuilder(context, sourceService)
                        .addRequestProperty("Content-Type", "application/x-www-form-urlencoded")
                        .build());
        // HashMap<String, String> headers = new HashMap<String, String>();
        // headers.put("Content-Type", "application/x-www-form-urlencoded");
        // mHttpClient = new HttpClient(context, new
        // HtcAccountRestErrorStreamReader(), headers);
    }

    /**
     * Refresh auth-key.
     * 
     * @param refreshToken The refresh token for refreshing auth-key.
     * @return Auth-key (master token).
     * @throws HttpException If server returns an error.
     * @throws IOException If server URI was malformed or other I/O error occurs
     *             when trying to send the data or read the response.
     * @throws ConnectionException When network error occurs when trying to make
     *             connection.
     * @throws ConnectivityException Data network is not available.
     * @throws InterruptedException If the thread is interrupted before
     *             complete.
     */
    public WMasterToken refreshAuthkey(String refreshToken) throws HttpException, IOException,
            ConnectionException, ConnectivityException, InterruptedException {
        mLogger.verbose();

        // Test arguments.
        if (TextUtils.isEmpty(refreshToken))
            throw new IllegalArgumentException("'refreshToken' is null or empty.");

        // Compose URL.
        StringBuilder urlBuilder = new StringBuilder(mServerUri).append("Services/OAuth/Token");
        URL url = new URL(urlBuilder.toString());

        // Compose output data.
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new NameValuePair(IdentityDefs.PARAM_GRANT_TYPE, IdentityDefs.OAUTH_GRANT_TYPE_REFRESHTOKEN));
        params.add(new NameValuePair(IdentityDefs.PARAM_REFRESH_TOKEN, refreshToken));
        String data = URLEncodedUtils.format(params);

        // Make REST call.
        return mHttpClient.post(
                mHttpClient.getRequestBuilder(url, new JsonInputStreamReader<WMasterToken>() {
                }).setDataWriter(new StringOutputStreamWriter(data)).build()
                , null, null).getResult(IdentityDefs.HTTP_TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }
}
