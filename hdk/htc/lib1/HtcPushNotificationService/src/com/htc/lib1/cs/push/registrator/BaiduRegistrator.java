
package com.htc.lib1.cs.push.registrator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.htc.lib1.cs.DeviceProfileHelper;
import com.htc.lib1.cs.account.HtcAccountDefs;
import com.htc.lib1.cs.account.HtcAccountHelper;
import com.htc.lib1.cs.account.HtcAccountNotExistsException;
import com.htc.lib1.cs.httpclient.ConnectionException;
import com.htc.lib1.cs.httpclient.ConnectivityException;
import com.htc.lib1.cs.httpclient.HttpException;
import com.htc.lib1.cs.logging.HtcLogger;
import com.htc.lib1.cs.pns.RegistrationPolicy;
import com.htc.lib1.cs.push.ClientResource;
import com.htc.lib1.cs.push.PnsInternalDefs;
import com.htc.lib1.cs.push.PnsRecords;
import com.htc.lib1.cs.push.PushLoggerFactory;
import com.htc.lib1.cs.push.PushProvider;
import com.htc.lib1.cs.push.RegistrationCredentials;
import com.htc.lib1.cs.push.dm2.PnsConfig;
import com.htc.lib1.cs.push.exception.HtcAccountAvailabilityException;
import com.htc.lib1.cs.push.exception.ReRegistrationNeeddedException;
import com.htc.lib1.cs.push.exception.RegistrationFailedException;
import com.htc.lib1.cs.push.exception.UnregistrationFailedException;
import com.htc.lib1.cs.push.exception.UnregistrationNeededException;
import com.htc.lib1.cs.push.exception.UpdateRegistrationFailedException;
import com.htc.lib1.cs.push.httputils.PnsRestServiceException.InvalidAuthKeyException;
import com.htc.lib1.cs.push.httputils.PnsRestServiceException.TargetNotFoundException;
import com.htc.lib1.cs.push.httputils.PnsServiceUnavailableException;
import com.htc.lib1.cs.push.utils.ApplicationInfoHelper;
import com.htc.lib1.cs.push.utils.VersionHelper;

import java.io.IOException;

/**
 * Registrator for Baidu Push.
 *
 * @author autosun_li@htc.com
 */
public class BaiduRegistrator implements Registrator {
    private static final long DEFAULT_REGISTRATION_TIMEOUT = 60000;
    private HtcLogger mLogger = new PushLoggerFactory(this).create();
    private Context mContext;
    private PnsConfig mPnsConfig = null;


    /**
     * @param context Context to operate on.
     * @param config  Necessary configuration to register Baidu PNS
     */
    public BaiduRegistrator(Context context, PnsConfig config) {
        if (context == null)
            throw new IllegalArgumentException("'context' is null.");
        if (config == null || !config.isValid())
            throw new IllegalArgumentException("'config' is null or its content is not valid.");

        mContext = context;
        mPnsConfig = config;
    }

    @Override
    public RegistrationCredentials register(RegistrationPolicy registrationPolicy)
            throws RegistrationFailedException, PnsServiceUnavailableException {
        mLogger.verbose();

        // Get HTC Account authtoken.
        HtcAccountHelper accntHelper = new HtcAccountHelper(mContext);
        String authToken = null;
        try {
            authToken = accntHelper.getAuthToken(HtcAccountDefs.AUTHTOKEN_TYPE_DEFAULT);
        } catch (Exception e) {
            if (registrationPolicy == RegistrationPolicy.REGISTER_ON_SIGNED_IN) {
                if (e instanceof HtcAccountNotExistsException) {
                    /*
                     * The policy requires HTC Account presents. It should not
                     * even schedule a retry in this case, so we rethrow with a
                     * specialized exception.
                     */
                    throw new HtcAccountAvailabilityException(e.getMessage(), e);
                }

                /*
                 * The policy requires HTC Account token to be available.
                 * Rethrow it to indicate the failure.
                 */
                throw new RegistrationFailedException(e.getMessage(), e);
            } else {
                // Authtoken is not mandatory for this policy.
                mLogger.warning(e);
            }
        }

        // Ensure enable Baidu component.
        RegistratorUtil.enableBaiduPushComponents(mContext);

        // Register Baidu push.
        BaiduRegistrationResponse response = registerBaidu();

        // Register PNS.
        try {
            return tryRegisterPNS(response.mmChannelId, response.mmUserId, authToken);
        } catch (InvalidAuthKeyException e) {
            // Invalidate authtoken.
            accntHelper.invalidateAuthToken(authToken);
            authToken = null;

            // Renew authtoken.
            try {
                authToken = accntHelper.getAuthToken(HtcAccountDefs.AUTHTOKEN_TYPE_DEFAULT);
            } catch (Exception e1) {
                if (registrationPolicy == RegistrationPolicy.REGISTER_ON_SIGNED_IN) {
                    if (e1 instanceof HtcAccountNotExistsException) {
                        /*
                         * The policy requires HTC Account presents. It should
                         * not even schedule a retry in this case, so we rethrow
                         * with a specialized exception.
                         */
                        throw new HtcAccountAvailabilityException(e1.getMessage(), e1);
                    }

                    /*
                     * The policy requires HTC Account token to be available.
                     * Rethrow it to indicate the failure.
                     */
                    throw new RegistrationFailedException(e1.getMessage(), e1);
                } else {
                    // Authtoken is not mandatory for this policy.
                    mLogger.warning(e1);
                }
            }

            try {
                return tryRegisterPNS(response.mmChannelId, response.mmUserId, authToken);
            } catch (InvalidAuthKeyException e1) {
                // A second invalid authkey exception is unexpected.
                throw new RegistrationFailedException(e1.getMessage(), e1);
            }

        }
    }

    @Override
    public void update(RegistrationPolicy registrationPolicy)
            throws UpdateRegistrationFailedException, ReRegistrationNeeddedException,
            UnregistrationNeededException, PnsServiceUnavailableException {
        mLogger.verbose();

        // Get HTC Account authtoken.
        HtcAccountHelper accntHelper = new HtcAccountHelper(mContext);
        String authToken = null;
        try {
            authToken = accntHelper.getAuthToken(HtcAccountDefs.AUTHTOKEN_TYPE_DEFAULT);
        } catch (HtcAccountNotExistsException e) {
            if (registrationPolicy == RegistrationPolicy.REGISTER_ON_SIGNED_IN) {
                /*
                 * The policy requires HTC Account to be available. Trigger
                 * unregistration.
                 */
                throw new UnregistrationNeededException(e.getMessage(), e);
            } else {
                // Authtoken is not mandatory for this policy.
                mLogger.warning(e);
            }
        } catch (Exception e) {
            if (registrationPolicy == RegistrationPolicy.REGISTER_ON_SIGNED_IN) {
                /*
                 * The policy requires HTC Account token to be available.
                 * Rethrow it to indicate the failure.
                 */
                throw new UpdateRegistrationFailedException(e.getMessage(), e);
            } else {
                // Authtoken is not mandatory for this policy.
                mLogger.warning(e);
            }
        }

        // Register Baidu push.
        BaiduRegistrationResponse response;
        try {
            response = registerBaidu();
        } catch (RegistrationFailedException e) {
            throw new UpdateRegistrationFailedException(e.getMessage(), e);
        }

        // Update PNS.
        String regId = PnsRecords.get(mContext).getRegId();
        String regKey = PnsRecords.get(mContext).getRegKey();
        try {
            tryUpdatePNS(regId, regKey, response.mmChannelId, response.mmUserId, authToken);
        } catch (InvalidAuthKeyException e) {
            // Invalidate authtoken.
            accntHelper.invalidateAuthToken(authToken);
            authToken = null;

            // Renew authtoken.
            try {
                authToken = accntHelper.getAuthToken(HtcAccountDefs.AUTHTOKEN_TYPE_DEFAULT);
            } catch (HtcAccountNotExistsException e1) {
                if (registrationPolicy == RegistrationPolicy.REGISTER_ON_SIGNED_IN) {
                    /*
                     * The policy requires HTC Account to be available. Trigger
                     * unregistration.
                     */
                    throw new UnregistrationNeededException(e1.getMessage(), e1);
                } else {
                    // Authtoken is not mandatory for this policy.
                    mLogger.warning(e1);
                }
            } catch (Exception e1) {
                if (registrationPolicy == RegistrationPolicy.REGISTER_ON_SIGNED_IN) {
                    /*
                     * The policy requires HTC Account token to be available.
                     * Rethrow it to indicate the failure.
                     */
                    throw new UpdateRegistrationFailedException(e1.getMessage(), e1);
                } else {
                    // Authtoken is not mandatory for this policy.
                    mLogger.warning(e1);
                }
            }

            try {
                tryUpdatePNS(regId, regKey, response.mmChannelId, response.mmUserId, authToken);
            } catch (InvalidAuthKeyException e1) {
                // A second invalid authkey exception is unexpected.
                throw new UpdateRegistrationFailedException(e1.getMessage(), e1);
            }
        }
    }

    @Override
    public void unregister() throws UnregistrationFailedException, PnsServiceUnavailableException {
        mLogger.verbose();

        RegistrationCredentials credentials = PnsRecords.get(mContext)
                .getRegCredentails();

        // Unregister to PNS.
        try {

            ClientResource resource = new ClientResource(mContext,
                    mPnsConfig.getBaseUri(getProvider()),
                    null);
            resource.deleteClient(credentials.getId(), credentials.getKey());
        } catch (IOException | ConnectivityException | ConnectionException | InterruptedException e) {
            throw new UnregistrationFailedException(e.getMessage(), e);
        } catch (PnsServiceUnavailableException e) {
            throw e;
        } catch (HttpException e) {
            throw new UnregistrationFailedException(e.getMessage(), e);
        } finally {

            // Stop Baidu push.
            PushManager.stopWork(mContext.getApplicationContext());

            // Ensure disable Baidu component.
            RegistratorUtil.disableBaiduPushComponents(mContext);
        }
    }

    @Override
    public PushProvider getProvider() {
        return PushProvider.BAIDU;
    }

    /**
     * Register Baidu.
     *
     * @return Alias.
     * @throws RegistrationFailedException If registration failed.
     */
    private BaiduRegistrationResponse registerBaidu() throws RegistrationFailedException {


        BaiduRegistrationReceiver receiver = new BaiduRegistrationReceiver();
        mContext.registerReceiver(receiver, new IntentFilter(PnsInternalDefs.ACTION_PNS_REGISTER_BAIDU), PnsInternalDefs.PERMISSION_SEND_MESSAGE, null);

        // Login baidu with api key.
        PushManager.startWork(mContext.getApplicationContext(), PushConstants.LOGIN_TYPE_API_KEY, getApiKey());

        // Wait for result.
        BaiduRegistrationResponse response = receiver.getResult(DEFAULT_REGISTRATION_TIMEOUT);
        mLogger.debugS("Baidu registration response: ", response);

        mContext.unregisterReceiver(receiver);

        if (response == null)
            throw new RegistrationFailedException("Registration to Baidu was failed.");

        return response;
    }

    /**
     * Try to register PNS.
     *
     * @param baiduChannelId Baidu channel id.
     * @param baiduUserId    Baidu user id.
     * @param authToken      HTC Account authtoken.
     * @return RegistrationCredentials
     * @throws RegistrationFailedException Registration fails.
     * @throws InvalidAuthKeyException     The authkey is expired or invalid.
     */
    private RegistrationCredentials tryRegisterPNS(String baiduChannelId, String baiduUserId,
                                                   String authToken) throws RegistrationFailedException,
            InvalidAuthKeyException, PnsServiceUnavailableException {

        try {
            ClientResource resource = new ClientResource(mContext,
                    mPnsConfig.getBaseUri(getProvider()),
                    authToken);
            DeviceProfileHelper deviceProfile = DeviceProfileHelper.get(mContext);

            return new RegistrationCredentials(
                    resource.addBaiduClient(PnsRecords.get(mContext).getUuid(),
                            deviceProfile.getModelId(),
                            deviceProfile.getCustomerId(),
                            deviceProfile.getSenseVersion(),
                            deviceProfile.getRomVersion(),
                            VersionHelper.get(mContext).getVersionName(),
                            getApiKey(), baiduChannelId, baiduUserId));
        } catch (InvalidAuthKeyException | PnsServiceUnavailableException e) {
            /*
             * Since InvalidAuthKeyException is a HttpException but needs
             * different handling. Catch and rethrow it here.
             */
            throw e;
        } catch (IOException | HttpException | ConnectionException | ConnectivityException | InterruptedException e) {
            throw new RegistrationFailedException(e.getMessage(), e);
        }
    }

    /**
     * Try to update PNS registration.
     *
     * @param regId          HTC PNS registration ID.
     * @param regKey         HTC PNS registration key.
     * @param baiduChannelId Baidu channel ID.
     * @param baiduUserId    Baidu user ID.
     * @param authToken      HTC Account auth token.
     * @throws UpdateRegistrationFailedException Update fails.
     * @throws InvalidAuthKeyException           The authkey is expired or invalid.
     */
    private void tryUpdatePNS(String regId, String regKey, String baiduChannelId,
                              String baiduUserId, String authToken) throws UpdateRegistrationFailedException,
            InvalidAuthKeyException, ReRegistrationNeeddedException, PnsServiceUnavailableException {

        // FIXME: It's a workaround for reset the Baidu push connection between
        // Baidu push client and Baidu push server and will be removed in the
        // next version.
        if (!PnsRecords.get(mContext).isForceBaiduReRegistered()) {
            mLogger.debug("Force Baidu push re-register while update Baidu push client.");
            Throwable t = new Throwable("");
            throw new ReRegistrationNeeddedException(
                    "Force re-register Baidu push by Update event.", t);
        }

        try {
            ClientResource resource = new ClientResource(mContext,
                    mPnsConfig.getBaseUri(getProvider()),
                    authToken);
            DeviceProfileHelper deviceProfile = DeviceProfileHelper.get(mContext);
            resource.updateBaiduClient(regId, regKey,
                    deviceProfile.getSenseVersion(),
                    deviceProfile.getRomVersion(),
                    VersionHelper.get(mContext).getVersionName(),
                    getApiKey(), baiduChannelId, baiduUserId);
        } catch (InvalidAuthKeyException | PnsServiceUnavailableException e) {
            /*
             * Since InvalidAuthKeyException is a HttpException but needs
             * different handling. Catch and rethrow it here.
             */
            throw e;
        } catch (TargetNotFoundException e) {
            /*
             * Since ReRegistrationNeeddedException is a HttpException but needs
             * different handling. Catch and throw
             * ReRegistrationNeeddedException.
             */
            throw new ReRegistrationNeeddedException(e.getMessage(), e);
        } catch (IOException | ConnectionException | HttpException | InterruptedException | ConnectivityException e) {
            throw new UpdateRegistrationFailedException(e.getMessage(), e);
        }
    }

    /**
     * Get Baidu apikey.
     *
     * @return Apikey.
     */
    private String getApiKey() {
        Bundle metaData = new ApplicationInfoHelper(mContext).getApplicationMetaData();
        String apiKey = null;
        if (metaData != null)
            apiKey = metaData.getString(PnsInternalDefs.KEY_BAIDU_API_KEY);

        if ((null == apiKey) || apiKey.length() != 24) {
            throw new IllegalStateException("Invalid Baidu API key. Have you declared meta-data '"
                    + PnsInternalDefs.KEY_BAIDU_API_KEY + "' in AndroidManifest.xml?");
        }

        return apiKey;
    }

    /**
     * Response of Baidu registration response.
     */
    private static class BaiduRegistrationResponse {
        public String mmAppId;
        public String mmUserId;
        public String mmChannelId;
        public String mmRequestId;

        @Override
        public String toString() {
            return getClass().getSimpleName() +
                    " {appid=\"" + mmAppId +
                    "\", channel_id=\"" + mmChannelId +
                    "\", user_id=\"" + mmUserId +
                    "\"}";
        }
    }

    private static class BaiduRegistrationReceiver extends BroadcastReceiver {
        private HtcLogger mmLogger = new PushLoggerFactory(this).create();
        private BaiduRegistrationResponse mmBaiduRegistrationResponse;

        @Override
        public synchronized void onReceive(Context context, Intent intent) {

            mmLogger.verboseS(intent);
            if (intent != null && PnsInternalDefs.ACTION_PNS_REGISTER_BAIDU.equals(intent.getAction())) {
                String appid = intent.getStringExtra(PnsInternalDefs.KEY_BAIDU_APP_ID);
                String userid = intent.getStringExtra(PnsInternalDefs.KEY_BAIDU_USER_ID);
                String channelid = intent.getStringExtra(PnsInternalDefs.KEY_BAIDU_CHANNEL_ID);
                String requestid = intent.getStringExtra(PnsInternalDefs.KEY_BAIDU_REQUEST_ID);

                mmLogger.debugS("response: [appid = ", appid, ", channelid = ", channelid, ", userid = ", userid, ", requestid = ", requestid, "]");

                // Ensure the response values are not null, or pns will retry register Baidu push
                if (!TextUtils.isEmpty(appid) && !TextUtils.isEmpty(userid)
                        && !TextUtils.isEmpty(channelid) && !TextUtils.isEmpty(requestid)) {
                    mmBaiduRegistrationResponse = new BaiduRegistrationResponse();
                    mmBaiduRegistrationResponse.mmAppId = appid;
                    mmBaiduRegistrationResponse.mmUserId = userid;
                    mmBaiduRegistrationResponse.mmChannelId = channelid;
                    mmBaiduRegistrationResponse.mmRequestId = requestid;
                }

                notifyAll();
            } else {
                mmLogger.error("Unhandled action: ", (intent != null) ? intent.getAction() : "null");
            }
        }

        /**
         * Get the registration result.
         *
         * @param timeout Timeout in milliseconds.
         * @return Baidu registration response or {@code null} if timed out.
         */
        public synchronized BaiduRegistrationResponse getResult(long timeout) {
            if (mmBaiduRegistrationResponse == null) {
                try {
                    mmLogger.debug("Waiting for Baidu Push registration...");
                    wait(timeout);
                } catch (InterruptedException e) {
                    mmLogger.warning(e);
                }
            }
            return mmBaiduRegistrationResponse;
        }

    }
}
