
package com.htc.lib1.cs.push.registrator;

import android.content.Context;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
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
import com.htc.lib1.cs.push.exception.GooglePlayServicesAvailabilityException;
import com.htc.lib1.cs.push.exception.HtcAccountAvailabilityException;
import com.htc.lib1.cs.push.exception.ReRegistrationNeeddedException;
import com.htc.lib1.cs.push.exception.RegistrationFailedException;
import com.htc.lib1.cs.push.exception.UnregistrationFailedException;
import com.htc.lib1.cs.push.exception.UnregistrationNeededException;
import com.htc.lib1.cs.push.exception.UpdateRegistrationFailedException;
import com.htc.lib1.cs.push.google.GooglePlayServicesAvailabilityUtils;
import com.htc.lib1.cs.push.google.GooglePlayServicesAvailabilityUtils.Availability;
import com.htc.lib1.cs.push.httputils.PnsRestServiceException.InvalidAuthKeyException;
import com.htc.lib1.cs.push.httputils.PnsRestServiceException.TargetNotFoundException;
import com.htc.lib1.cs.push.httputils.PnsServiceUnavailableException;
import com.htc.lib1.cs.push.receiver.OneTimeOnGooglePlayServicesPackageRecoveredReceiver;
import com.htc.lib1.cs.push.utils.AppComponentSettingUtils;
import com.htc.lib1.cs.push.utils.VersionHelper;

import java.io.IOException;

/**
 * Registrator for GCM.
 */
public class GCMRegistrator implements Registrator {
    private HtcLogger mLogger = new PushLoggerFactory(this).create();
    private Context mContext;
    private PnsConfig mPnsConfig = null;

    /**
     * @param context Context to operate on.
     * @param config Necessary configuration to register GCM
     */
    public GCMRegistrator(Context context, PnsConfig config) {
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

        // Check Google Play Services availability and register GCM.
        int status = GooglePlayServicesAvailabilityUtils.isGooglePlayServicesAvailable(mContext);
        Availability availability = GooglePlayServicesAvailabilityUtils.isAvaiable(status);
        String regId;
        if (availability == Availability.AVAILABLE) {
            // Register GCM.
            try {
                regId = registerGCM();
                // Ensure receiver is disabled.
                AppComponentSettingUtils.disable(mContext,
                        OneTimeOnGooglePlayServicesPackageRecoveredReceiver.class);
            } catch (IOException e) {
                throw new RegistrationFailedException(e.getMessage(), e);
            }
        } else {
            if (availability == Availability.RECOVERABLE) {
                // Show recover notification.
                GooglePlayServicesUtil.showErrorNotification(status, mContext);
            }
            throw new GooglePlayServicesAvailabilityException(status);
        }

        // Register PNS.
        try {
            return tryRegisterPNS(regId, authToken);
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
                return tryRegisterPNS(regId, authToken);
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

        // Check Google Play Services availability and register GCM.
        int status = GooglePlayServicesAvailabilityUtils.isGooglePlayServicesAvailable(mContext);
        Availability availability = GooglePlayServicesAvailabilityUtils.isAvaiable(status);
        String gcmRegId;
        if (availability == Availability.AVAILABLE) {
            // Register GCM.
            try {
                gcmRegId = registerGCM();
            } catch (IOException e) {
                throw new UpdateRegistrationFailedException(e.getMessage(), e);
            }
        } else {
            Exception cause = new GooglePlayServicesAvailabilityException(status);
            if (availability == Availability.RECOVERABLE) {
                // Show recover notification.
                GooglePlayServicesUtil.showErrorNotification(status, mContext);
                throw new UpdateRegistrationFailedException(cause.getMessage(), cause);
            } else /* UNRECOVERABLE */{
                /*
                 * If google play service is not available, trigger
                 * re-registration.
                 */
                throw new ReRegistrationNeeddedException(cause.getMessage(), cause);
            }
        }

        // Update PNS.
        String regId = PnsRecords.get(mContext).getRegId();
        String regKey = PnsRecords.get(mContext).getRegKey();
        try {
            tryUpdatePNS(regId, regKey, gcmRegId, authToken);
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
                tryUpdatePNS(regId, regKey, gcmRegId, authToken);
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

        try {
            ClientResource resource = new ClientResource(mContext,
                    mPnsConfig.getBaseUri(getProvider()),
                    null);
            resource.deleteClient(credentials.getId(), credentials.getKey());
        } catch (IOException | ConnectionException | ConnectivityException | InterruptedException e) {
            throw new UnregistrationFailedException(e.getMessage(), e);
        } catch (PnsServiceUnavailableException e) {
            throw e;
        } catch (HttpException e) {
            throw new UnregistrationFailedException(e.getMessage(), e);
        } finally {
            GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(mContext);
            try {
                gcm.unregister();
            } catch (IOException e) {
                throw new UnregistrationFailedException(e.getMessage(), e);
            }
        }
    }

    @Override
    public PushProvider getProvider() {
        return PushProvider.GCM;
    }

    private String registerGCM() throws IOException {
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(mContext);
        String regId;
        regId = gcm.register(PnsInternalDefs.GCM_SENDER_ID);
        mLogger.debug("Registration success with regId = ", regId);
        return regId;
    }

    /**
     * Try to register PNS.
     * 
     * @param gcmRegId GCM registration ID.
     * @param authToken HTC Account authtoken.
     * @return RegistrationCredentials
     * @throws RegistrationFailedException Registration fails.
     * @throws InvalidAuthKeyException The authkey is expired or invalid.
     */
    private RegistrationCredentials tryRegisterPNS(String gcmRegId, String authToken)
            throws RegistrationFailedException, InvalidAuthKeyException,
            PnsServiceUnavailableException {
        try {
            ClientResource resource = new ClientResource(mContext,
                    mPnsConfig.getBaseUri(getProvider()),
                    authToken);
            DeviceProfileHelper deviceProfile = DeviceProfileHelper.get(mContext);

            return new RegistrationCredentials(
                    resource.addGCMClient(PnsRecords.get(mContext).getUuid(),
                            deviceProfile.getModelId(),
                            deviceProfile.getCustomerId(),
                            deviceProfile.getSenseVersion(),
                            deviceProfile.getRomVersion(),
                            VersionHelper.get(mContext).getVersionName(),
                            PnsInternalDefs.GCM_SENDER_ID, gcmRegId));
        } catch (InvalidAuthKeyException | PnsServiceUnavailableException e) {
            /*
             * Since InvalidAuthKeyException is a HttpException but needs
             * different handling. Catch and rethrow it here.
             */
            throw e;
        } catch (IOException | HttpException | ConnectionException | InterruptedException
                | ConnectivityException e) {
            throw new RegistrationFailedException(e.getMessage(), e);
        }
    }

    /**
     * Try to update PNS registration.
     *
     * @param regId HTC PNS registration ID.
     * @param gcmRegId GCM registration ID.
     * @param authToken HTC Account authtoken.
     * @throws UpdateRegistrationFailedException Update fails.
     * @throws InvalidAuthKeyException The authkey is expired or invalid.
     */
    private void tryUpdatePNS(String regId, String regKey, String gcmRegId, String authToken)
            throws UpdateRegistrationFailedException, InvalidAuthKeyException,
            ReRegistrationNeeddedException, PnsServiceUnavailableException {

        try {
            ClientResource resource = new ClientResource(mContext,
                    mPnsConfig.getBaseUri(getProvider()),
                    authToken);
            DeviceProfileHelper deviceProfile = DeviceProfileHelper.get(mContext);
            resource.updateGCMClient(regId, regKey,
                    deviceProfile.getSenseVersion(),
                    deviceProfile.getRomVersion(),
                    VersionHelper.get(mContext).getVersionName(),
                    PnsInternalDefs.GCM_SENDER_ID,
                    gcmRegId);
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
        } catch (IOException | HttpException | ConnectionException | InterruptedException
                | ConnectivityException e) {
            throw new UpdateRegistrationFailedException(e.getMessage(), e);
        }
    }
}
