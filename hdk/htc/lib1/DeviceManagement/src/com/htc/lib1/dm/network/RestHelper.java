package com.htc.lib1.dm.network;

import android.content.Context;
import android.text.TextUtils;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.htc.lib1.dm.constants.Constants;
import com.htc.lib1.dm.constants.HttpHeaderConstants;
import com.htc.lib1.dm.constants.RestConstants;
import com.htc.lib1.dm.env.AppEnv;
import com.htc.lib1.dm.env.DeviceEnv;
import com.htc.lib1.dm.env.NetworkEnv;
import com.htc.lib1.dm.env.Version;
import com.htc.lib1.dm.exception.DMException;
import com.htc.lib1.dm.util.CryptoHelper;

import java.io.IOException;
import java.util.Map;

/**
 * Created by Joe_Wu on 8/22/14.
 */
public class RestHelper {

    private static RestHelper sInstance;
    private Context mContext;
    private Version version;
    private DeviceEnv deviceEnv;
    private AppEnv appEnv;
    private NetworkEnv networkEnv;

    public static synchronized RestHelper getInstance(Context context) {
        if(sInstance==null){
            sInstance = new RestHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    private RestHelper(Context context) {
        this.mContext = context;
        this.version = Version.get(mContext);
        this.deviceEnv = DeviceEnv.get(mContext);
        this.appEnv = AppEnv.get(mContext);
        this.networkEnv = NetworkEnv.get(mContext);
    }

    public HttpRequest prepareRequest(HttpRequest request, Boolean includeProfileInHeader) throws IOException, DMException {
        // request.setSuppressUserAgentSuffix(false);

        return request.setHeaders(getHeaders(includeProfileInHeader))
                      .setNumberOfRetries(0);
    }

    private HttpHeaders getHeaders(Boolean includeProfileInHeader) throws DMException {
        HttpHeaders headers = new HttpHeaders();
        headers.setUserAgent(version.getUserAgentString());
        headers.setAcceptEncoding(RestConstants.ACCEPT_ENCODING_GZIP);

        // Custom Headers
        headers.set(HttpHeaderConstants.DEVICE_SN, deviceEnv.getDeviceSN());
        headers.set(HttpHeaderConstants.DEVICE_MFR,deviceEnv.getManufacturer());

        // Set Device Tel ID Header
        String telId = deviceEnv.getMobileEquipmentIdentifierUrn();
        if (!TextUtils.isEmpty(telId)) {
            headers.set(HttpHeaderConstants.DEVICE_TEL_ID, telId);
        }

        headers.set(HttpHeaderConstants.PLATFORM_DEVICE_ID,deviceEnv.getPlatformDeviceIdUrn());
        headers.set(HttpHeaderConstants.APP_VERSION,appEnv.getAppVersion());
        headers.set(HttpHeaderConstants.DM_LIB_VERSION, String.format("%s;%s", Constants.LIBRARY_PACKAGE_NAME, Constants.LIBRARY_VERSION_NAME ));

        // TODO TracerID
        // headers.set(HttpHeaderConstants.TRACER_ID,);
        if (!TextUtils.isEmpty(networkEnv.getOperatorPLMN())) {
            headers.set(HttpHeaderConstants.OPERATOR_PLMN, networkEnv.getOperatorPLMN());
        }
        if (!TextUtils.isEmpty(networkEnv.getNetworkPLMN())) {
            headers.set(HttpHeaderConstants.NETWORK_PLMN, networkEnv.getNetworkPLMN());
        }
        headers.set(HttpHeaderConstants.DEFAULT_LOCALE,deviceEnv.getDefaultLocale());

        if(includeProfileInHeader) setProfileInfoIntoHeaders(headers);

        return headers;
    }

    private void setProfileInfoIntoHeaders(HttpHeaders headers) {
        headers.set(HttpHeaderConstants.ANDROID_API_LEVEL, deviceEnv.getAndroidApiLevel() );
        headers.set(HttpHeaderConstants.ANDROID_VERSION, deviceEnv.getAndroidVersion() );
        headers.set(HttpHeaderConstants.BUILD_DESCRIPTION, deviceEnv.getBuildDescription() );
        headers.set(HttpHeaderConstants.CUSTOMER_ID, deviceEnv.getCID() );
        headers.set(HttpHeaderConstants.MARKETING_NAME, deviceEnv.getMarketingName() );
        headers.set(HttpHeaderConstants.MODEL_ID, deviceEnv.getDeviceModelId() );
        headers.set(HttpHeaderConstants.PRODUCT_NAME, deviceEnv.getProductName() );
        headers.set(HttpHeaderConstants.PROJECT_NAME, deviceEnv.getProjectName() );
        headers.set(HttpHeaderConstants.ROM_VERSION, deviceEnv.getRomVersion() );
        headers.set(HttpHeaderConstants.SENSE_VERSION, deviceEnv.getSenseVersion() );
        headers.set(HttpHeaderConstants.REGION_ID, deviceEnv.getRegionID() );
        headers.set(HttpHeaderConstants.SKU_ID, deviceEnv.getSkuID() );
    }

    /**
     For HTTP Basic authentication, the credentials are:

     Base64({userid}:{passphrase})
     Where:

     userid is the device serial number
     passphrase is an encoded passphrase that is computed using a shared secret (between device and server)
     The format of passphrase is:

     {version_code}:{token}
     Where:

     version_code is a non-zero positive integer identifying the version of the algorithm used to compute token
     token is a token derived from shared secret values
     The format of token is:

     Base64(Hash({version_code}:{params_hash}))
     The format of params_hash is:

     Base64(Hash({device_sn}:{dm_client_app_version}))))
     Where:

     device_sn is the device serial number
     dm_client_app_version is the version identifier of the DM client

     version_code 1: SHA_1
     version_code 2: SHA_256
     * @return Passphrase
     */
    public String genDeviceAuthenticationPassphrase() throws DMException {
        String deviceSN = deviceEnv.getDeviceSN();
        String credentialVersion = RestConstants.DEVICE_CREDENTIAL_VERSION_2;
        String clientAppVersion = appEnv.getAppVersion();

        String params_hash = CryptoHelper.computeHash(String.format("%s:%s",deviceSN,clientAppVersion));
        String token = CryptoHelper.computeHash(String.format("%s:%s",credentialVersion,params_hash));
        return String.format("%s:%s",credentialVersion,token);
    }

    public String getRequestInfoDumpString(HttpRequest request) {
        StringBuilder builder = new StringBuilder();
        builder.append("\r\n===== DM HTTP REQUEST (START) =====\r\n");
        builder.append("URL:\t"+request.getUrl()+"\r\n");
        builder.append("ConnectTimeout:\t"+request.getConnectTimeout()+"\r\n");

        HttpHeaders headers = request.getHeaders();
        builder.append("===== DM HTTP REQUEST HEADER =====\r\n");
        builder.append("UserAgent: "+headers.getUserAgent()+"\r\n");
        builder.append("AcceptEncoding: "+headers.getAcceptEncoding()+"\r\n");
        builder.append("Accept: "+headers.getAccept()+"\r\n");
        builder.append("Authorization: "+headers.getAuthorization()+"\r\n");
        builder.append("ContentLength: "+headers.getContentLength()+"\r\n");
        Map<String,Object> custom = headers.getUnknownKeys();
        for(String key : custom.keySet()){
            builder.append(key+": "+custom.get(key)+"\r\n");
        }
        builder.append("===== DM HTTP REQUEST (END) =====\r\n");
        return builder.toString();
    }

    public String getResponseInfoDumpString(HttpResponse response) {
        StringBuilder builder = new StringBuilder();
        builder.append("\r\n===== DM HTTP RESPONSE (START) =====\r\n");
        builder.append("URL:\t"+response.getRequest().getUrl()+"\r\n");

        builder.append("===== DM HTTP RESPONSE HEADER =====\r\n");
        HttpHeaders headers = response.getHeaders();
        builder.append("ContentType: "+headers.getContentType()+"\r\n");
        builder.append("ContentEncoding: "+headers.getContentEncoding()+"\r\n");
        builder.append("ContentLength: "+headers.getContentLength()+"\r\n");
        builder.append("ETag: "+headers.getETag()+"\r\n");
        builder.append("Expires: "+headers.getExpires()+"\r\n");
        Map<String,Object> custom = headers.getUnknownKeys();
        for(String key : custom.keySet()){
            builder.append(key+": "+custom.get(key)+"\r\n");
        }
        builder.append("===== DM HTTP RESPONSE (END) =====\r\n");
        return builder.toString();
    }
}
