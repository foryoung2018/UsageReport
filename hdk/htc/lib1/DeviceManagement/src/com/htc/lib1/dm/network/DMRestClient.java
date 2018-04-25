package com.htc.lib1.dm.network;

import android.content.Context;
import com.google.api.client.http.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.gson.Gson;
import com.htc.lib1.dm.bo.AppConfig;
import com.htc.lib1.dm.bo.DMRequestType;
import com.htc.lib1.dm.bo.DeviceManifest;
import com.htc.lib1.dm.cache.DMCacheManager;
import com.htc.lib1.dm.constants.Constants;
import com.htc.lib1.dm.constants.RestConstants;
import com.htc.lib1.dm.env.DeviceEnv;
import com.htc.lib1.dm.exception.DMException;
import com.htc.lib1.dm.exception.DMHttpException;
import com.htc.lib1.dm.exception.DMJsonParseException;
import com.htc.lib1.dm.logging.Logger;
import com.htc.lib1.dm.util.RetryHelper;

import java.io.IOException;

/**
 * Created by Joe_Wu on 9/3/14.
 */
public class DMRestClient {
    private static final Logger LOGGER = Logger.getLogger("[DM]",DMRestClient.class);
    private static DMRestClient restClient = null;

    HttpTransport httpTransport;
    HttpRequestFactory requestFactory;
    RestHelper resthelper;
    RetryHelper retryHelper;
    DeviceEnv deviceEnv;

    public static synchronized DMRestClient getInstance(Context context, RetryHelper retryHelper){
        if(restClient==null)
            restClient = new DMRestClient(context,retryHelper);
        return restClient;
    }

    private DMRestClient(Context context, RetryHelper retryHelper) {
        this.resthelper = RestHelper.getInstance(context);
        this.httpTransport = new NetHttpTransport();
        this.requestFactory = httpTransport.createRequestFactory();
        this.deviceEnv = DeviceEnv.get(context);
        this.retryHelper = retryHelper;
    }

    public AppConfig getAppConfig(String nextURI) throws IOException, DMException {
        LOGGER.debug("getAppConfig() loading data from network...");
        LOGGER.debugS("getAppConfig() loading data url:"+nextURI);

        // construct default request
        HttpRequest request =  resthelper.prepareRequest(
                requestFactory.buildGetRequest(
                        new GenericUrl(nextURI)
                ),
                true
        );
        // Set specific info into request
        request.getHeaders().setAccept(RestConstants.MEDIA_TYPE_APP_CONFIG);
        request.setConnectTimeout(Constants.HTTP_GET_CONFIG_REQUEST_TIMEOUT);
        request.setReadTimeout(Constants.HTTP_GET_CONFIG_REQUEST_TIMEOUT);

        // DEBUG: Dump Request
        LOGGER.debugS(resthelper.getRequestInfoDumpString(request));

        request.setParser(new GsonFactory().createJsonObjectParser());
        request.setThrowExceptionOnExecuteError(false);

        HttpResponse response = request.execute();

        String responseBody = response.parseAsString();

        // DEBUG: Dump Response
        LOGGER.debug("loadDataFromNetwork got HTTP Response Code:",response.getStatusCode()," Message:",response.getStatusMessage());
        LOGGER.debugS(resthelper.getResponseInfoDumpString(response));
        LOGGER.debugS(responseBody);

        switch(response.getStatusCode()){
            case HttpStatusCodes.STATUS_CODE_OK:
                return AppConfig.parseAppConfigFromJson(responseBody);
            case 503: // Service Unavailable
                // Set Retry-After
                String retryAfter = response.getHeaders().getRetryAfter();
                LOGGER.informative("GetConfig got HTTP Service Unavailable Exception with Retry-After:"+retryAfter);
                retryHelper.setRetryAfter(DMRequestType.GET_CONFIG,retryAfter);
            default:
                throw new DMHttpException(response);
        }

    }

    public void putProfile(String postURI, DeviceManifest deviceManifest) throws IOException, DMException {
        LOGGER.debug("posting DeviceProfile to network...");
        LOGGER.debugS("posting DeviceProfile url:"+postURI);

        String profileJsonString = new Gson().toJson(deviceManifest);
        LOGGER.debugS("posting DeviceProfile body:\r\n"+profileJsonString);

        HttpRequest request =  resthelper.prepareRequest(
                requestFactory.buildPutRequest(
                        new GenericUrl(postURI),
                        ByteArrayContent.fromString(RestConstants.MEDIA_TYPE_DEVICE_MANIFEST_V2, profileJsonString)
                ),
                false
        );
        // Set specific info into request
        request.getHeaders().setAccept(RestConstants.MEDIA_TYPE_APP_CONFIGS_V2);
        request.setConnectTimeout(Constants.HTTP_PUT_PROFILE_REQUEST_TIMEOUT);
        request.setReadTimeout(Constants.HTTP_PUT_PROFILE_REQUEST_TIMEOUT);

        // Set BasicAuthentication
        request.getHeaders().setBasicAuthentication(deviceEnv.getDeviceSN().replace(":", ""), resthelper.genDeviceAuthenticationPassphrase());

        // DEBUG: Dump Request
        LOGGER.debugS(resthelper.getRequestInfoDumpString(request));

        request.setParser(new GsonFactory().createJsonObjectParser());
        request.setThrowExceptionOnExecuteError(false);

        HttpResponse response = request.execute();

        String responseBody = response.parseAsString();

        // DEBUG: Dump Response
        LOGGER.debug("loadDataFromNetwork got HTTP Response Code:",response.getStatusCode()," Message:",response.getStatusMessage());
        LOGGER.debugS(resthelper.getResponseInfoDumpString(response));
        LOGGER.debugS(responseBody);

        switch(response.getStatusCode()){
            case HttpStatusCodes.STATUS_CODE_OK:
            case 201: // Created
                // Success
                break;
            case 503: // Service Unavailable
                // Handle Retry-After
                String retryAfter = response.getHeaders().getRetryAfter();
                LOGGER.informative("PutProfile got HTTP Service Unavailable Exception with Retry-After:"+retryAfter);
                retryHelper.setRetryAfter(DMRequestType.PUT_PROFILE,retryAfter);
                throw new DMHttpException(response);
            default:
                throw new DMHttpException(response);
        }

    }

}
