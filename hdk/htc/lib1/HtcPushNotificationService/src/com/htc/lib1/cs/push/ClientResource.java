
package com.htc.lib1.cs.push;

import android.content.Context;
import android.os.Build;
import android.provider.Settings.Secure;
import android.text.TextUtils;

import com.htc.lib1.cs.StringUtils;
import com.htc.lib1.cs.httpclient.ConnectionException;
import com.htc.lib1.cs.httpclient.ConnectivityException;
import com.htc.lib1.cs.httpclient.HtcRestRequestPropertiesBuilder;
import com.htc.lib1.cs.httpclient.HttpClient;
import com.htc.lib1.cs.httpclient.HttpException;
import com.htc.lib1.cs.httpclient.JsonInputStreamReader;
import com.htc.lib1.cs.httpclient.JsonOutputStreamWriter;
import com.htc.lib1.cs.httpclient.StringInputStreamReader;
import com.htc.lib1.cs.logging.HtcLogger;
import com.htc.lib1.cs.push.httputils.PnsErrorStreamReader;
import com.htc.lib1.cs.push.utils.SystemPropertiesProxy;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ClientResource {
    private static final int PROTO_VERSION = 3;
    private static final String OS_ANDROID = "android";
    private static final String BUILD_CHINA_SENSE = "ro.build.chinasense";
    private HtcLogger mLogger = new PushLoggerFactory(this).create();
    private Context mContext;
    private String mServerUri;
    private HttpClient mHttpClient;

    /**
     * Construct a {@link ClientResource}
     * 
     * @param context Application context.
     * @param serverUri Sense server URI.
     * @param authKey HTC Account authkey.
     */
    public ClientResource(Context context, String serverUri, String authKey) {
        // Test arguments.
        if (context == null)
            throw new IllegalArgumentException("'context' is null.");
        if (TextUtils.isEmpty(serverUri))
            throw new IllegalArgumentException("'serverUri' is null or empty.");

        mContext = context;
        mServerUri = StringUtils.ensureTrailingSlash(serverUri);
        mHttpClient = new HttpClient(context, new PnsErrorStreamReader(),
                new HtcRestRequestPropertiesBuilder(context).setAuthKey(authKey).build());
    }

    /**
     * Register a GCM client. <br>
     * Path: {serverUri}/pns/client/
     * 
     * @param uuid UUID used to identify this client.
     * @param modelId Device model id. (ex. PN0710000)
     * @param customerId Device customer id. (ex. ORANG001)
     * @param senseVersion HTC Sense version.
     * @param romVersion Device ROM version.
     * @param clientVersion Push client version.
     * @param gcmSenderId GCM sender ID.
     * @param gcmRegId GCM registration ID.
     * @return Response of register which contains a registration id and a key
     *         used to update / unregister.
     * @throws HttpException If server returns an error.
     * @throws IOException If server URI was malformed or other I/O error occurs
     *             when trying to send the data or read the response.
     * @throws ConnectionException When network error occurs when trying to make
     *             connection.
     * @throws ConnectivityException Data network is not available.
     * @throws InterruptedException If the thread is interrupted before
     *             complete.
     */
    public RegisterResponse addGCMClient(UUID uuid, String modelId, String customerId,
            String senseVersion, String romVersion, String clientVersion, String gcmSenderId,
            String gcmRegId) throws IOException, HttpException, ConnectionException,
            ConnectivityException, InterruptedException {
        mLogger.verbose();

        /* Mandatory fields */
        if (uuid == null)
            throw new IllegalArgumentException("'uuid' is null.");
        if (TextUtils.isEmpty(clientVersion))
            throw new IllegalArgumentException("'clientVersion' is null or empty");
        if (TextUtils.isEmpty(gcmSenderId))
            throw new IllegalArgumentException("'gcmSenderId' is null or empty.");
        if (TextUtils.isEmpty(gcmRegId))
            throw new IllegalArgumentException("'gcmRegId' is null or empty.");

        /* Important but not mandatory fields. */
        if (TextUtils.isEmpty(modelId)) {
            mLogger.info("'modelId' is null or empty. Either it's an engineering sample device or non-HEP environment.");
            modelId = null;
        }
        if (TextUtils.isEmpty(customerId)) {
            mLogger.info("'customerId' is null or empty. Either it's an engineering sample device or non-HEP environment.");
            customerId = null;
        }
        if (TextUtils.isEmpty(senseVersion)) {
            mLogger.info("'senseVersion' is null or empty. Either it's an engineering sample device or non-HEP environment.");
            senseVersion = null;
        }
        if (TextUtils.isEmpty(romVersion)) {
            mLogger.info("'romVersion' is null or empty. Either it's an engineering sample device or non-HEP environment.");
            romVersion = null;
        }

        RegisterPayload profile = new RegisterPayload();
        profile.clientUuid = uuid;
        profile.modelId = modelId;
        profile.customerId = customerId;
        profile.senseVersion = senseVersion;
        profile.romVersion = romVersion;
        profile.clientVersion = clientVersion;
        profile.pushProvider = PushProvider.GCM.toString();
        profile.gcmSenderId = gcmSenderId;
        profile.gcmRegId = gcmRegId;

        return addClient(profile);
    }

    /**
     * Register a Baidu client. <br>
     * Path: {serverUri}/pns/client/
     * 
     * @param uuid UUID used to identify this client.
     * @param modelId Device model id. (ex. PN0710000)
     * @param customerId Device customer id. (ex. ORANG001)
     * @param senseVersion HTC Sense version.
     * @param romVersion Device ROM version.
     * @param clientVersion Push client version.
     * @param baiduApiKey Baidu api key.
     * @param baiduChannelId Baidu channel id.
     * @param baiduUserId Baidu user id.
     * @return Response of register which contains a registration id and a key
     *         used to update / unregister.
     * @throws HttpException If server returns an error.
     * @throws IOException If server URI was malformed or other I/O error occurs
     *             when trying to send the data or read the response.
     * @throws ConnectionException When network error occurs when trying to make
     *             connection.
     * @throws ConnectivityException Data network is not available.
     * @throws InterruptedException If the thread is interrupted before
     *             complete.
     */
    public RegisterResponse addBaiduClient(UUID uuid, String modelId, String customerId,
            String senseVersion, String romVersion, String clientVersion, String baiduApiKey,
            String baiduChannelId, String baiduUserId) throws IOException, HttpException,
            ConnectionException, ConnectivityException, InterruptedException {
        mLogger.verbose();

        /* Mandatory fields. */
        if (uuid == null)
            throw new IllegalArgumentException("'uuid' is null.");
        if (TextUtils.isEmpty(clientVersion))
            throw new IllegalArgumentException("'clientVersion' is null or empty");
        if (TextUtils.isEmpty(baiduApiKey))
            throw new IllegalArgumentException("'baiduApiKey' is null or empty.");
        if (TextUtils.isEmpty(baiduChannelId))
            throw new IllegalArgumentException("'baiduChannelId' is null or empty.");
        if (TextUtils.isEmpty(baiduUserId))
            throw new IllegalArgumentException("'baiduUserId' is null or empty.");

        /* Important but not mandatory fields. */
        if (TextUtils.isEmpty(modelId)) {
            mLogger.info("'modelId' is null or empty. Either it's an engineering sample device or non-HEP environment.");
            modelId = null;
        }
        if (TextUtils.isEmpty(customerId)) {
            mLogger.info("'customerId' is null or empty. Either it's an engineering sample device or non-HEP environment.");
            customerId = null;
        }
        if (TextUtils.isEmpty(senseVersion)) {
            mLogger.info("'senseVersion' is null or empty. Either it's an engineering sample device or non-HEP environment.");
            senseVersion = null;
        }
        if (TextUtils.isEmpty(romVersion)) {
            mLogger.info("'romVersion' is null or empty. Either it's an engineering sample device or non-HEP environment.");
            romVersion = null;
        }

        RegisterPayload profile = new RegisterPayload();
        profile.clientUuid = uuid;
        profile.modelId = modelId;
        profile.customerId = customerId;
        profile.senseVersion = senseVersion;
        profile.romVersion = romVersion;
        profile.clientVersion = clientVersion;
        profile.pushProvider = PushProvider.BAIDU.toString();
        profile.baiduAppKey = baiduApiKey;
        profile.baiduChannelId = baiduChannelId;
        profile.baiduUserId = baiduUserId;
        profile.need_encryption = true;     // Encrypt messages via Baidu push

        return addClient(profile);
    }

    /**
     * Register the client. <br>
     * Path: {serverUri}/pns/client/
     * 
     * @param payload Payload of register.
     * @return Response of register.
     * @throws HttpException If server returns an error.
     * @throws IOException If server URI was malformed or other I/O error occurs
     *             when trying to send the data or read the response.
     * @throws ConnectionException When network error occurs when trying to make
     *             connection.
     * @throws ConnectivityException Data network is not available.
     * @throws InterruptedException If the thread is interrupted before
     *             complete.
     */
    public RegisterResponse addClient(RegisterPayload payload) throws IOException, HttpException,
            ConnectionException, ConnectivityException, InterruptedException {
        mLogger.verbose();

        // Test arguments.
        if (payload == null)
            throw new IllegalArgumentException("'payload' is null.");

        // Set common fields.
        payload.protocolVersion = PROTO_VERSION;
        payload.os = OS_ANDROID;
        payload.osVersion = payload.androidVersion = Build.VERSION.RELEASE;
        payload.product = Build.PRODUCT;

        String chinaSense = SystemPropertiesProxy.get(mContext, BUILD_CHINA_SENSE);
        if (!TextUtils.isEmpty(chinaSense)) {
            payload.chinaSenseVersion = chinaSense;
        }
        
        payload.sim_mccmnc = getMCCMNCList();
        
        payload.manufacturer = Build.MANUFACTURER;
        payload.deviceSerialNum = Build.SERIAL;
        payload.clientId = mContext.getPackageName();
        payload.androidId = Secure.getString(mContext.getContentResolver(), Secure.ANDROID_ID);

        // Compose URL.
        StringBuilder urlBuilder = new StringBuilder(mServerUri)
                .append("pns/client/");
        URL url = new URL(urlBuilder.toString());

        // Make REST call.
        return mHttpClient.post(mHttpClient.getRequestBuilder(url,
                new JsonInputStreamReader<RegisterResponse>() {
                }).setDataWriter(new JsonOutputStreamWriter(payload)).build(), null, null)
                .getResult(PnsInternalDefs.HTTP_TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }
    
    /**
     * Get MCC/MNC info with dual SIM case follow by google design.
     * @return array list of MCC/MNC
     */
    private List<String> getMCCMNCList() {
        List<String> mccmncList = null;
        String mccMncNumeric = SystemPropertiesProxy.get(mContext, PnsInternalDefs.KEY_SYSTEM_PROP_SIM_MCC_MNC); // 46692,46692
        mLogger.debugS(PnsInternalDefs.KEY_SYSTEM_PROP_SIM_MCC_MNC, " is ", mccMncNumeric);

        if (!TextUtils.isEmpty(mccMncNumeric)) {
            String[] list = mccMncNumeric.trim().split("\\s*,\\s*");

            String trimedString = "";
            for (String s : list) {
                if (mccmncList == null) {
                    mccmncList = new ArrayList<String>();
                }

                trimedString = s.trim();
                if (!TextUtils.isEmpty(trimedString)) {
                    mccmncList.add(trimedString);
                }
            }
        }

        return mccmncList;
    }

    /**
     * Update the profile of a registered GCM client. <br>
     * Path: {serverUri}/pns/client/{regId}/
     * 
     * @param regId Registration id from push server.
     * @param regKey Registration key from push server.
     * @param senseVersion HTC Sense version.
     * @param romVersion Device ROM version.
     * @param clientVersion Push client version.
     * @param gcmSenderId GCM sender ID.
     * @param gcmRegId GCM registration ID.
     * @throws HttpException If server returns an error.
     * @throws IOException If server URI was malformed or other I/O error occurs
     *             when trying to send the data or read the response.
     * @throws ConnectionException When network error occurs when trying to make
     *             connection.
     * @throws ConnectivityException Data network is not available.
     * @throws InterruptedException If the thread is interrupted before
     *             complete.
     */
    public void updateGCMClient(String regId, String regKey, String senseVersion,
            String romVersion, String clientVersion, String gcmSenderId, String gcmRegId)
            throws IOException, HttpException, ConnectionException, ConnectivityException,
            InterruptedException {
        mLogger.verbose();

        /* Mandatory fields */
        if (TextUtils.isEmpty(regId))
            throw new IllegalArgumentException("'regId' is null or empty.");
        if (TextUtils.isEmpty(regKey))
            throw new IllegalArgumentException("'regKey' is null or empty.");
        if (TextUtils.isEmpty(clientVersion))
            throw new IllegalArgumentException("'clientVersion' is null or empty");
        if (TextUtils.isEmpty(gcmSenderId))
            throw new IllegalArgumentException("'gcmSenderId' is null or empty.");
        if (TextUtils.isEmpty(gcmRegId))
            throw new IllegalArgumentException("'gcmRegId' is null or empty.");

        /* Important but not mandatory fields. */
        if (TextUtils.isEmpty(senseVersion)) {
            mLogger.info("'senseVersion' is null or empty. Either it's an engineering sample device or non-HEP environment.");
            senseVersion = null;
        }
        if (TextUtils.isEmpty(romVersion)) {
            mLogger.info("'romVersion' is null or empty. Either it's an engineering sample device or non-HEP environment.");
            romVersion = null;
        }

        UpdateRegisterPayload profile = new UpdateRegisterPayload();
        profile.regKey = regKey;
        profile.senseVersion = senseVersion;
        profile.romVersion = romVersion;
        profile.clientVersion = clientVersion;
        profile.pushProvider = PushProvider.GCM.toString();
        profile.gcmSenderId = gcmSenderId;
        profile.gcmRegId = gcmRegId;

        updateClient(regId, profile);
    }

    /**
     * Update the profile of a registered Baidu client. <br>
     * Path: {serverUri}/pns/client/{regId}/
     * 
     * @param regId Registration id from push server.
     * @param regKey Registration key from push server.
     * @param senseVersion HTC Sense version.
     * @param romVersion Device ROM version.
     * @param clientVersion Push client version.
     * @param baiduApiKey Baidu api key.
     * @param baiduChannelId Baidu channel ID.
     * @param baiduUserId Baidu user ID.
     * @throws HttpException If server returns an error.
     * @throws IOException If server URI was malformed or other I/O error occurs
     *             when trying to send the data or read the response.
     * @throws ConnectionException When network error occurs when trying to make
     *             connection.
     * @throws ConnectivityException Data network is not available.
     * @throws InterruptedException If the thread is interrupted before
     *             complete.
     */
    public void updateBaiduClient(String regId, String regKey, String senseVersion,
            String romVersion, String clientVersion, String baiduApiKey, String baiduChannelId,
            String baiduUserId) throws IOException, HttpException, ConnectionException,
            ConnectivityException, InterruptedException {
        mLogger.verbose();

        /* Mandatory fields. */
        if (TextUtils.isEmpty(regId))
            throw new IllegalArgumentException("'regId' is null or empty.");
        if (TextUtils.isEmpty(regKey))
            throw new IllegalArgumentException("'regKey' is null or empty.");
        if (TextUtils.isEmpty(clientVersion))
            throw new IllegalArgumentException("'clientVersion' is null or empty");
        if (TextUtils.isEmpty(baiduApiKey))
            throw new IllegalArgumentException("'baiduApiKey' is null or empty.");
        if (TextUtils.isEmpty(baiduChannelId))
            throw new IllegalArgumentException("'baiduChannelId' is null or empty.");
        if (TextUtils.isEmpty(baiduUserId))
            throw new IllegalArgumentException("'baiduUserId' is null or empty.");

        /* Important but not mandatory fields. */
        if (TextUtils.isEmpty(senseVersion)) {
            mLogger.info("'senseVersion' is null or empty. Either it's an engineering sample device or non-HEP environment.");
            senseVersion = null;
        }
        if (TextUtils.isEmpty(romVersion)) {
            mLogger.info("'romVersion' is null or empty. Either it's an engineering sample device or non-HEP environment.");
            romVersion = null;
        }

        UpdateRegisterPayload profile = new UpdateRegisterPayload();
        profile.regKey = regKey;
        profile.senseVersion = senseVersion;
        profile.romVersion = romVersion;
        profile.clientVersion = clientVersion;
        profile.pushProvider = PushProvider.BAIDU.toString();
        profile.baiduAppKey = baiduApiKey;
        profile.baiduChannelId = baiduChannelId;
        profile.baiduUserId = baiduUserId;
        profile.need_encryption = true;     // Encrypt messages via Baidu push

        updateClient(regId, profile);
    }

    /**
     * Update the profile of a registered client. <br>
     * Path: {serverUri}/pns/client/{regId}/
     * 
     * @param regId registration id.
     * @param payload Payload of update registration.
     * @throws HttpException If server returns an error.
     * @throws IOException If server URI was malformed or other I/O error occurs
     *             when trying to send the data or read the response.
     * @throws ConnectionException When network error occurs when trying to make
     *             connection.
     * @throws ConnectivityException Data network is not available.
     * @throws InterruptedException If the thread is interrupted before
     *             complete.
     */
    public void updateClient(String regId, UpdateRegisterPayload payload)
            throws IOException, HttpException, ConnectionException,
            ConnectivityException, InterruptedException {
        mLogger.verbose();

        // Test arguments.
        if (TextUtils.isEmpty(regId))
            throw new IllegalArgumentException("'regId' is null or empty.");
        if (payload == null)
            throw new IllegalArgumentException("'payload' is null.");

        // Set common fields.
        payload.protocolVersion = PROTO_VERSION;
        payload.os = OS_ANDROID;
        payload.osVersion = payload.androidVersion = Build.VERSION.RELEASE;
        payload.product = Build.PRODUCT;
        
        String chinaSense = SystemPropertiesProxy.get(mContext, BUILD_CHINA_SENSE);
        if (!TextUtils.isEmpty(chinaSense)) {
            payload.chinaSenseVersion = chinaSense;
        }
        
        payload.sim_mccmnc = getMCCMNCList();
        
        payload.clientId = mContext.getPackageName();

        // Compose URL.
        StringBuilder urlBuilder = new StringBuilder(mServerUri)
                .append("pns/client/").append(regId);
        URL url = new URL(urlBuilder.toString());

        // Make REST call.
        mHttpClient.put(mHttpClient.getRequestBuilder(url, new StringInputStreamReader())
                .setDataWriter(new JsonOutputStreamWriter(payload)).build(), null, null).
                getResult(PnsInternalDefs.HTTP_TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }

    /**
     * Unregister the client. <br>
     * Path: {serverUri}/pns/client/{regId}?reg_key={regKey}
     * 
     * @param regId registration id.
     * @param regKey registration key.
     * @throws HttpException If server returns an error.
     * @throws IOException If server URI was malformed or other I/O error occurs
     *             when trying to send the data or read the response.
     * @throws ConnectionException When network error occurs when trying to make
     *             connection.
     * @throws ConnectivityException Data network is not available.
     * @throws InterruptedException If the thread is interrupted before
     *             complete.
     */
    public void deleteClient(String regId, String regKey)
            throws IOException, HttpException, ConnectionException,
            ConnectivityException, InterruptedException {
        mLogger.verbose();

        // Test arguments.
        if (TextUtils.isEmpty(regId))
            throw new IllegalArgumentException("'regId' is null or empty.");
        if (TextUtils.isEmpty(regKey))
            throw new IllegalArgumentException("'regKey' is null.");

        // Compose URL.
        StringBuilder urlBuilder = new StringBuilder(mServerUri)
                .append("pns/client/").append(regId).append("?reg_key=").append(regKey);
        URL url = new URL(urlBuilder.toString());

        // Make REST call.
        mHttpClient.delete(url, null, null)
                .getResult(PnsInternalDefs.HTTP_TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }
}
