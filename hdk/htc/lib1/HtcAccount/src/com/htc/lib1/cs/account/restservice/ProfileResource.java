
package com.htc.lib1.cs.account.restservice;

import android.content.Context;
import android.text.TextUtils;

import com.htc.lib1.cs.StringUtils;
import com.htc.lib1.cs.account.HtcAccountRestErrorStreamReader;
import com.htc.lib1.cs.account.restobj.HtcAccountProfile;
import com.htc.lib1.cs.account.restservice.IdentityJsonClasses.WGracePeriodStatus;
import com.htc.lib1.cs.auth.AuthLoggerFactory;
import com.htc.lib1.cs.auth.client.IdentityDefs;
import com.htc.lib1.cs.httpclient.ConnectionException;
import com.htc.lib1.cs.httpclient.ConnectivityException;
import com.htc.lib1.cs.httpclient.HttpClient;
import com.htc.lib1.cs.httpclient.HttpException;
import com.htc.lib1.cs.httpclient.JsonInputStreamReader;
import com.htc.lib1.cs.logging.HtcLogger;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Service/resource related to account profiles.
 */
public class ProfileResource {
    private HtcLogger mLogger = new AuthLoggerFactory(this).create();
    private String mServerUri;
    private HttpClient mHttpClient;

    /**
     * Construct an {@code ProfileResource}.
     * 
     * @param context Application context.
     * @param serverUri Server URI.
     * @param authKey AuthKey to use.
     * @param sourceService Source service name to identity which integrated
     *            service triggers the flow.
     */
    public ProfileResource(Context context, String serverUri, String authKey, String sourceService) {
        // Test arguments.
        if (context == null)
            throw new IllegalArgumentException("'context' is null.");
        if (TextUtils.isEmpty(serverUri))
            throw new IllegalArgumentException("'serverUri' is null or empty.");
        if (TextUtils.isEmpty(authKey))
            throw new IllegalArgumentException("'authKey' is null or empty.");
        if (TextUtils.isEmpty(sourceService)) {
            throw new IllegalArgumentException("'sourceService' is null or empty.");
        }

        // Remove timestamp prefix if any.
        if (authKey.contains("."))
            authKey = authKey.substring(authKey.indexOf(".") + 1);

        mServerUri = StringUtils.ensureTrailingSlash(serverUri);
        mHttpClient = new HttpClient(context, new HtcAccountRestErrorStreamReader(),
                new IdentityRequestPropertiesBuilder(context, sourceService).setAuthKey(authKey)
                        .build());
    }

    /**
     * Get user subscription status of SendEmailAboutProducts. <br>
     * Path: {serverUri}/Services/Accounts.svc/Accounts/SendEmailAboutProducts/
     * 
     * @return User subscription status.
     * @throws HttpException If server returns an error.
     * @throws IOException If server URI was malformed or other I/O error occurs
     *             when trying to send the data or read the response.
     * @throws ConnectionException When network error occurs when trying to make
     *             connection.
     * @throws ConnectivityException Data network is not available.
     * @throws InterruptedException If the thread is interrupted before
     *             complete.
     */
    public boolean getSendEmailAboutProducts() throws HttpException, IOException,
            ConnectionException, ConnectivityException, InterruptedException {
        mLogger.verbose();

        // Compose URL.
        StringBuilder urlBuilder = new StringBuilder(mServerUri)
                .append("Services/Accounts.svc/Accounts/SendEmailAboutProducts/");
        URL url = new URL(urlBuilder.toString());

        // Make REST call.
        return mHttpClient.get(
                mHttpClient.getRequestBuilder(url, new JsonInputStreamReader<Boolean>() {
                }).build(), null, null).getResult(IdentityDefs.HTTP_TIMEOUT_SECONDS,
                TimeUnit.SECONDS);
    }

    /**
     * Set user subscription status of SendEmailAboutProducts. <br>
     * Path: {serverUri}/Services/Accounts.svc/Accounts/SendEmailAboutProducts/
     * 
     * @param value Subscription status.
     * @throws HttpException If server returns an error.
     * @throws IOException If server URI was malformed or other I/O error occurs
     *             when trying to send the data or read the response.
     * @throws ConnectionException When network error occurs when trying to make
     *             connection.
     * @throws ConnectivityException Data network is not available.
     * @throws InterruptedException If the thread is interrupted before
     *             complete.
     */
    public void setSendEmailAboutProducts(boolean value) throws HttpException, IOException,
            ConnectionException, ConnectivityException, InterruptedException {
        mLogger.verbose();

        // Compose URL.
        StringBuilder urlBuilder = new StringBuilder(mServerUri)
                .append("Services/Accounts.svc/Accounts/SendEmailAboutProducts/");
        URL url = new URL(urlBuilder.toString());

        try {
            // Compose data.
            String data = new JSONObject().put("SendEmailAboutProducts", value).toString();

            // Make REST call.
            mHttpClient.post(url, data, null, null)
                    .getResult(IdentityDefs.HTTP_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        } catch (JSONException e) {
            // Convert to runtime exception.
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * Start email grace period. <br>
     * Path: {serverUri}/Services/AccountsExtn.svc/Accounts/ResendVerifyEmail/
     * 
     * @return Expiration date of the grace period.
     * @throws HttpException If server returns an error.
     * @throws IOException If server URI was malformed or other I/O error occurs
     *             when trying to send the data or read the response.
     * @throws ConnectionException When network error occurs when trying to make
     *             connection.
     * @throws ConnectivityException Data network is not available.
     * @throws InterruptedException If the thread is interrupted before
     *             complete.
     */
    public Date startGracePeriod() throws HttpException, IOException, ConnectionException,
            ConnectivityException, InterruptedException {
        mLogger.verbose();

        // Compose URL
        StringBuilder urlBuilder = new StringBuilder(mServerUri)
                .append("Services/Accounts.svc/Accounts/StartGracePeriod/");
        URL url = new URL(urlBuilder.toString());

        // Make REST call.
        return mHttpClient.get(
                mHttpClient.getRequestBuilder(url, new JsonInputStreamReader<Date>() {
                }).build(), null, null).getResult(IdentityDefs.HTTP_TIMEOUT_SECONDS,
                TimeUnit.SECONDS);
    }

    /**
     * Resend the email verification mail.
     * 
     * @param emailAddress Email address to verify.
     * @throws HttpException If server returns an error.
     * @throws IOException If server URI was malformed or other I/O error occurs
     *             when trying to send the data or read the response.
     * @throws ConnectionException When network error occurs when trying to make
     *             connection.
     * @throws ConnectivityException Data network is not available.
     * @throws InterruptedException If the thread is interrupted before
     *             complete.
     */
    public void resendVerifyEmail(String emailAddress) throws HttpException,
            IOException, ConnectionException, ConnectivityException, InterruptedException {
        mLogger.verbose();

        // Test arguments.
        if (TextUtils.isEmpty(emailAddress))
            throw new IllegalArgumentException("'emailAddress' is null or empty.");

        try {
            // Compose URL
            StringBuilder urlBuilder = new StringBuilder(mServerUri)
                    .append("Services/AccountsExtn.svc/Accounts/ResendVerifyEmail/");
            URL url = new URL(urlBuilder.toString());

            // Compose data.
            String data = new JSONObject().put("emailAddress", emailAddress).toString();

            // Make REST call.
            mHttpClient.post(url, data, null, null)
                    .getResult(IdentityDefs.HTTP_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        } catch (JSONException e) {
            // Convert to runtime exception.
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * Get email grace period status.
     * 
     * @return User email grace period status.
     * @throws HttpException If server returns an error.
     * @throws IOException If server URI was malformed or other I/O error occurs
     *             when trying to send the data or read the response.
     * @throws ConnectionException When network error occurs when trying to make
     *             connection.
     * @throws ConnectivityException Data network is not available.
     * @throws InterruptedException If the thread is interrupted before
     *             complete.
     */
    public WGracePeriodStatus getGracePeriodStatus() throws HttpException, IOException,
            ConnectionException, ConnectivityException, InterruptedException {
        // Compose URL
        StringBuilder urlBuilder = new StringBuilder(mServerUri)
                .append("Services/Accounts.svc/Accounts/GetGracePeriodStatus/");
        URL url = new URL(urlBuilder.toString());

        // Make REST call.
        return mHttpClient.get(
                mHttpClient.getRequestBuilder(url, new JsonInputStreamReader<WGracePeriodStatus>() {
                }).build(), null, null).getResult(IdentityDefs.HTTP_TIMEOUT_SECONDS,
                TimeUnit.SECONDS);
    }

    /**
     * Get the account profile.
     * 
     * @return {@link HtcAccountProfile}
     * @throws HttpException If server returns an error.
     * @throws IOException If server URI was malformed or other I/O error occurs
     *             when trying to send the data or read the response.
     * @throws ConnectionException When network error occurs when trying to make
     *             connection.
     * @throws ConnectivityException Data network is not available.
     * @throws InterruptedException If the thread is interrupted before
     *             complete.
     */
    public HtcAccountProfile getAccountProfile() throws InterruptedException,
            ConnectivityException, ConnectionException, IOException, HttpException {
        mLogger.verbose();

        // Compose URL.
        StringBuilder urlBuilder = new StringBuilder(mServerUri)
                .append("Services/AccountsExtn.svc/Accounts/RetrieveAccountProfile/");
        URL url = new URL(urlBuilder.toString());

        // Make REST call.
        return mHttpClient.get(
                mHttpClient.getRequestBuilder(url, new JsonInputStreamReader<HtcAccountProfile>() {
                }).build(), null, null).getResult(IdentityDefs.HTTP_TIMEOUT_SECONDS,
                TimeUnit.SECONDS);
    }
}
