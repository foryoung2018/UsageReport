/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.htc.lib1.cs.account;

import java.util.Arrays;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

import com.htc.lib0.htcdebugflag.HtcWrapHtcDebugFlag;
import com.htc.lib1.cs.auth.BuildConfig;
import com.htc.lib1.cs.logging.CommLoggerFactory;
import com.htc.lib1.cs.logging.HtcLogger;

/**
 * Abstract base class for creating HTC AccountAuthenticators. In order to be an
 * authenticator one must extend this class, provider implementations for the
 * abstract methods and write a service that returns the result of
 * {@link #getIBinder()} in the service's
 * {@link android.app.Service#onBind(android.content.Intent)} when invoked with
 * an intent with action
 * {@link LocalHtcAccountAuthenticatorSession#ACTION_LOCAL_HTC_ACCOUNT_AUTHENTICATOR_INTENT}
 * . This service must specify the following intent filter and metadata tags in
 * its AndroidManifest.xml file
 * 
 * <pre>
 *   &lt;intent-filter&gt;
 *     &lt;action android:name="com.htc.cs.HtcAccountAuthenticator" /&gt;
 *   &lt;/intent-filter&gt;
 * </pre>
 * <p>
 * The standard pattern for implementing any of the abstract methods is the
 * following:
 * <ul>
 * <li>If the supplied arguments are enough for the authenticator to fully
 * satisfy the request then it will do so and return a {@link Bundle} that
 * contains the results.
 * <li>If the authenticator needs information from the user to satisfy the
 * request then it will create an {@link Intent} to an activity that will prompt
 * the user for the information and then carry out the request. This intent must
 * be returned in a Bundle as key {@link AccountManager#KEY_INTENT}.
 * <p>
 * The activity needs to return the final result when it is complete so the
 * Intent should contain the {@link HtcAccountAuthenticatorResponse} as
 * {@link AccountManager#KEY_ACCOUNT_MANAGER_RESPONSE}. The activity must then
 * call {@link HtcAccountAuthenticatorResponse#onResult} or
 * {@link HtcAccountAuthenticatorResponse#onError} when it is complete.
 * <li>If the authenticator cannot synchronously process the request and return
 * a result then it may choose to return null and then use the
 * AccountManagerResponse to send the result when it has completed the request.
 * </ul>
 * <p>
 * The following descriptions of each of the abstract authenticator methods will
 * not describe the possible asynchronous nature of the request handling and
 * will instead just describe the input parameters and the expected result.
 * <p>
 * When writing an activity to satisfy these requests one must pass in the
 * AccountManagerResponse and return the result via that response when the
 * activity finishes (or whenever else the activity author deems it is the
 * correct time to respond). The {@link HtcAccountAuthenticatorActivity} handles
 * this, so one may wish to extend that when writing activities to handle these
 * requests.
 * <p>
 * This is an HTC Account specific implementation for local HTC authenticator.
 */
public abstract class AbstractHtcAccountAuthenticator {
    private HtcLogger mLogger = new CommLoggerFactory(AbstractHtcAccountAuthenticator.class).create();
    private final Context mContext;

    public AbstractHtcAccountAuthenticator(Context context) {
        mContext = context;
    }

    private class Transport extends IHtcAccountAuthenticator.Stub {
        
        @Override
        public void addAccount(IHtcAccountAuthenticatorResponse response, String accountType,
                String authTokenType, String[] features, Bundle options)
                throws RemoteException {
            mLogger.verboseS("accountType=", accountType, ", authTokenType=",
                    authTokenType, ", features=",
                    (features == null ? "[]" : Arrays.toString(features)), ", options=", options);
            checkBinderPermission();
            try {
                final Bundle result = AbstractHtcAccountAuthenticator.this.addAccount(
                        new HtcAccountAuthenticatorResponse(response),
                        accountType, authTokenType, features, options);
                if (HtcWrapHtcDebugFlag.Htc_SECURITY_DEBUG_flag || BuildConfig.DEBUG) {
                    result.keySet(); // force it to be unparcelled
                    mLogger.verboseS("result=", result);
                }
                if (result != null) {
                    response.onResult(result);
                }
            } catch (Exception e) {
                handleException(response, "addAccount", accountType, e);
            }
        }

        @Override
        public void confirmCredentials(IHtcAccountAuthenticatorResponse response,
                HtcAccount htcAccount, Bundle options) throws RemoteException {
            mLogger.verboseS("htcAccount=", htcAccount, ", options=", options);
            checkBinderPermission();
            try {
                final Bundle result = AbstractHtcAccountAuthenticator.this.confirmCredentials(
                        new HtcAccountAuthenticatorResponse(response), htcAccount, options);
                if (HtcWrapHtcDebugFlag.Htc_SECURITY_DEBUG_flag || BuildConfig.DEBUG) {
                    result.keySet(); // force it to be unparcelled
                    mLogger.verboseS("result=", result);
                }
                if (result != null) {
                    response.onResult(result);
                }
            } catch (Exception e) {
                handleException(response, "confirmCredentials", htcAccount.toString(), e);
            }
        }

        @Override
        public void getAuthTokenLabel(IHtcAccountAuthenticatorResponse response,
                String authTokenType)
                throws RemoteException {
            mLogger.verboseS("authTokenType=", authTokenType);
            checkBinderPermission();
            try {
                Bundle result = new Bundle();
                result.putString(AccountManager.KEY_AUTH_TOKEN_LABEL,
                        AbstractHtcAccountAuthenticator.this.getAuthTokenLabel(authTokenType));
                if (HtcWrapHtcDebugFlag.Htc_SECURITY_DEBUG_flag || BuildConfig.DEBUG) {
                    result.keySet(); // force it to be unparcelled
                    mLogger.verboseS("result=", result);
                }
                response.onResult(result);
            } catch (Exception e) {
                handleException(response, "getAuthTokenLabel", authTokenType, e);
            }
        }

        @Override
        public void getAuthToken(IHtcAccountAuthenticatorResponse response,
                HtcAccount htcAccount, String authTokenType, Bundle loginOptions)
                throws RemoteException {
            mLogger.verboseS("htcAccount=", htcAccount, ", authTokenType=", authTokenType,
                    ", loginOptions=", loginOptions);
            checkBinderPermission();
            try {
                final Bundle result = AbstractHtcAccountAuthenticator.this.getAuthToken(
                        new HtcAccountAuthenticatorResponse(response), htcAccount,
                        authTokenType, loginOptions);
                if (HtcWrapHtcDebugFlag.Htc_SECURITY_DEBUG_flag || BuildConfig.DEBUG) {
                    result.keySet(); // force it to be unparcelled
                    mLogger.verboseS("result=", result);
                }
                if (result != null) {
                    response.onResult(result);
                }
            } catch (Exception e) {
                handleException(response, "getAuthToken",
                        htcAccount.toString() + "," + authTokenType, e);
            }
        }

        @Override
        public void updateCredentials(IHtcAccountAuthenticatorResponse response, HtcAccount htcAccount,
                String authTokenType, Bundle loginOptions) throws RemoteException {
            mLogger.verboseS("htcAccount=", htcAccount, ", authTokenType=", authTokenType,
                    ", loginOptions=", loginOptions);
            checkBinderPermission();
            try {
                final Bundle result = AbstractHtcAccountAuthenticator.this.updateCredentials(
                        new HtcAccountAuthenticatorResponse(response), htcAccount,
                        authTokenType, loginOptions);
                if (HtcWrapHtcDebugFlag.Htc_SECURITY_DEBUG_flag || BuildConfig.DEBUG) {
                    result.keySet(); // force it to be unparcelled
                    mLogger.verboseS("result=", result);
                }
                if (result != null) {
                    response.onResult(result);
                }
            } catch (Exception e) {
                handleException(response, "updateCredentials",
                        htcAccount.toString() + "," + authTokenType, e);
            }
        }

        @Override
        public void editProperties(IHtcAccountAuthenticatorResponse response,
                String accountType) throws RemoteException {
            checkBinderPermission();
            try {
                final Bundle result = AbstractHtcAccountAuthenticator.this.editProperties(
                        new HtcAccountAuthenticatorResponse(response), accountType);
                if (result != null) {
                    response.onResult(result);
                }
            } catch (Exception e) {
                handleException(response, "editProperties", accountType, e);
            }
        }

        @Override
        public void hasFeatures(IHtcAccountAuthenticatorResponse response,
                HtcAccount htcAccount, String[] features) throws RemoteException {
            checkBinderPermission();
            try {
                final Bundle result = AbstractHtcAccountAuthenticator.this.hasFeatures(
                        new HtcAccountAuthenticatorResponse(response), htcAccount, features);
                if (result != null) {
                    response.onResult(result);
                }
            } catch (Exception e) {
                handleException(response, "hasFeatures", htcAccount.toString(), e);
            }
        }

        @Override
        public void getAccountRemovalAllowed(IHtcAccountAuthenticatorResponse response,
                HtcAccount htcAccount) throws RemoteException {
            checkBinderPermission();
            try {
                final Bundle result = AbstractHtcAccountAuthenticator.this.getAccountRemovalAllowed(
                        new HtcAccountAuthenticatorResponse(response), htcAccount);
                if (result != null) {
                    response.onResult(result);
                }
            } catch (Exception e) {
                handleException(response, "getAccountRemovalAllowed", htcAccount.toString(), e);
            }
        }

        @Override
        public void getAccountCredentialsForCloning(IHtcAccountAuthenticatorResponse response,
                HtcAccount htcAccount) throws RemoteException {
            checkBinderPermission();
            try {
                final Bundle result =
                        AbstractHtcAccountAuthenticator.this.getAccountCredentialsForCloning(
                                new HtcAccountAuthenticatorResponse(response), htcAccount);
                if (result != null) {
                    response.onResult(result);
                }
            } catch (Exception e) {
                handleException(response, "getAccountCredentialsForCloning", htcAccount.toString(), e);
            }
        }

        @Override
        public void addAccountFromCredentials(IHtcAccountAuthenticatorResponse response,
                HtcAccount htcAccount, Bundle accountCredentials) throws RemoteException {
            checkBinderPermission();
            try {
                final Bundle result =
                        AbstractHtcAccountAuthenticator.this.addAccountFromCredentials(
                                new HtcAccountAuthenticatorResponse(response), htcAccount,
                                accountCredentials);
                if (result != null) {
                    response.onResult(result);
                }
            } catch (Exception e) {
                handleException(response, "addAccountFromCredentials", htcAccount.toString(), e);
            }
        }
    }

    private void handleException(IHtcAccountAuthenticatorResponse response, String method,
            String data, Exception e) throws RemoteException {
        if (e instanceof NetworkErrorException) {
            mLogger.verboseS(method, "(", data, "): ", e);
            response.onError(AccountManager.ERROR_CODE_NETWORK_ERROR, e.getMessage());
        } else if (e instanceof UnsupportedOperationException) {
            mLogger.verboseS(method, "(", data, "): ", e);
            response.onError(AccountManager.ERROR_CODE_UNSUPPORTED_OPERATION,
                    method + " not supported");
        } else if (e instanceof IllegalArgumentException) {
            mLogger.verboseS(method, "(", data, "): ", e);
            response.onError(AccountManager.ERROR_CODE_BAD_ARGUMENTS,
                    method + " not supported");
        } else {
            mLogger.warning(method, "(", data, "): ", e);
            response.onError(AccountManager.ERROR_CODE_REMOTE_EXCEPTION,
                    method + " failed");
        }
    }

    private void checkBinderPermission() {
        final int uid = Binder.getCallingUid();
        final int myuid = mContext.getApplicationInfo().uid;

        if (uid == myuid) {
            mLogger.verboseS("Calling from the same uid. Skip permission checking.");
        } else {
            final String perm = Manifest.permission.ACCOUNT_MANAGER;
            if (mContext.checkCallingOrSelfPermission(perm) != PackageManager.PERMISSION_GRANTED) {
                throw new SecurityException("caller uid " + uid + " lacks " + perm);
            }
        }
    }

    private Transport mTransport = new Transport();

    /**
     * @return the IBinder for the AccountAuthenticator
     */
    public final IBinder getIBinder() {
        return mTransport.asBinder();
    }

    /**
     * Returns a Bundle that contains the Intent of the activity that can be
     * used to edit the properties. In order to indicate success the activity
     * should call response.setResult() with a non-null Bundle.
     * 
     * @param response used to set the result for the request. If the
     *            Constants.INTENT_KEY is set in the bundle then this response
     *            field is to be used for sending future results if and when the
     *            Intent is started.
     * @param accountType the AccountType whose properties are to be edited.
     * @return a Bundle containing the result or the Intent to start to continue
     *         the request. If this is null then the request is considered to
     *         still be active and the result should sent later using response.
     */
    public abstract Bundle editProperties(HtcAccountAuthenticatorResponse response,
            String accountType);

    /**
     * Adds an account of the specified accountType.
     * 
     * @param response to send the result back to the AccountManager, will never
     *            be null
     * @param accountType the type of account to add, will never be null
     * @param authTokenType the type of auth token to retrieve after adding the
     *            account, may be null
     * @param requiredFeatures a String array of authenticator-specific features
     *            that the added account must support, may be null
     * @param options a Bundle of authenticator-specific options, may be null
     * @return a Bundle result or null if the result is to be returned via the
     *         response. The result will contain either:
     *         <ul>
     *         <li> {@link AccountManager#KEY_INTENT}, or
     *         <li> {@link AccountManager#KEY_ACCOUNT_NAME} and
     *         {@link AccountManager#KEY_ACCOUNT_TYPE} of the account that was
     *         added, or
     *         <li> {@link AccountManager#KEY_ERROR_CODE} and
     *         {@link AccountManager#KEY_ERROR_MESSAGE} to indicate an error
     *         </ul>
     * @throws NetworkErrorException if the authenticator could not honor the
     *             request due to a network error
     */
    public abstract Bundle addAccount(HtcAccountAuthenticatorResponse response, String accountType,
            String authTokenType, String[] requiredFeatures, Bundle options)
            throws NetworkErrorException;

    /**
     * Checks that the user knows the credentials of an account.
     * 
     * @param response to send the result back to the AccountManager, will never
     *            be null
     * @param account the account whose credentials are to be checked, will
     *            never be null
     * @param options a Bundle of authenticator-specific options, may be null
     * @return a Bundle result or null if the result is to be returned via the
     *         response. The result will contain either:
     *         <ul>
     *         <li> {@link AccountManager#KEY_INTENT}, or
     *         <li> {@link AccountManager#KEY_BOOLEAN_RESULT}, true if the check
     *         succeeded, false otherwise
     *         <li> {@link AccountManager#KEY_ERROR_CODE} and
     *         {@link AccountManager#KEY_ERROR_MESSAGE} to indicate an error
     *         </ul>
     * @throws NetworkErrorException if the authenticator could not honor the
     *             request due to a network error
     */
    public abstract Bundle confirmCredentials(HtcAccountAuthenticatorResponse response,
            HtcAccount htcAccount, Bundle options)
            throws NetworkErrorException;

    /**
     * Gets the authtoken for an account.
     * 
     * @param response to send the result back to the AccountManager, will never
     *            be null
     * @param account the account whose credentials are to be retrieved, will
     *            never be null
     * @param authTokenType the type of auth token to retrieve, will never be
     *            null
     * @param options a Bundle of authenticator-specific options, may be null
     * @return a Bundle result or null if the result is to be returned via the
     *         response. The result will contain either:
     *         <ul>
     *         <li> {@link AccountManager#KEY_INTENT}, or
     *         <li> {@link AccountManager#KEY_ACCOUNT_NAME},
     *         {@link AccountManager#KEY_ACCOUNT_TYPE}, and
     *         {@link AccountManager#KEY_AUTHTOKEN}, or
     *         <li> {@link AccountManager#KEY_ERROR_CODE} and
     *         {@link AccountManager#KEY_ERROR_MESSAGE} to indicate an error
     *         </ul>
     * @throws NetworkErrorException if the authenticator could not honor the
     *             request due to a network error
     */
    public abstract Bundle getAuthToken(HtcAccountAuthenticatorResponse response,
            HtcAccount htcAccount, String authTokenType, Bundle options)
            throws NetworkErrorException;

    /**
     * Ask the authenticator for a localized label for the given authTokenType.
     * 
     * @param authTokenType the authTokenType whose label is to be returned,
     *            will never be null
     * @return the localized label of the auth token type, may be null if the
     *         type isn't known
     */
    public abstract String getAuthTokenLabel(String authTokenType);

    /**
     * Update the locally stored credentials for an account.
     * 
     * @param response to send the result back to the AccountManager, will never
     *            be null
     * @param account the account whose credentials are to be updated, will
     *            never be null
     * @param authTokenType the type of auth token to retrieve after updating
     *            the credentials, may be null
     * @param options a Bundle of authenticator-specific options, may be null
     * @return a Bundle result or null if the result is to be returned via the
     *         response. The result will contain either:
     *         <ul>
     *         <li> {@link AccountManager#KEY_INTENT}, or
     *         <li> {@link AccountManager#KEY_ACCOUNT_NAME} and
     *         {@link AccountManager#KEY_ACCOUNT_TYPE} of the account that was
     *         added, or
     *         <li> {@link AccountManager#KEY_ERROR_CODE} and
     *         {@link AccountManager#KEY_ERROR_MESSAGE} to indicate an error
     *         </ul>
     * @throws NetworkErrorException if the authenticator could not honor the
     *             request due to a network error
     */
    public abstract Bundle updateCredentials(HtcAccountAuthenticatorResponse response,
            HtcAccount htcAccount, String authTokenType, Bundle options) throws NetworkErrorException;

    /**
     * Checks if the account supports all the specified authenticator specific
     * features.
     * 
     * @param response to send the result back to the AccountManager, will never
     *            be null
     * @param account the account to check, will never be null
     * @param features an array of features to check, will never be null
     * @return a Bundle result or null if the result is to be returned via the
     *         response. The result will contain either:
     *         <ul>
     *         <li> {@link AccountManager#KEY_INTENT}, or
     *         <li> {@link AccountManager#KEY_BOOLEAN_RESULT}, true if the
     *         account has all the features, false otherwise
     *         <li> {@link AccountManager#KEY_ERROR_CODE} and
     *         {@link AccountManager#KEY_ERROR_MESSAGE} to indicate an error
     *         </ul>
     * @throws NetworkErrorException if the authenticator could not honor the
     *             request due to a network error
     */
    public abstract Bundle hasFeatures(HtcAccountAuthenticatorResponse response,
            HtcAccount htcAccount, String[] features) throws NetworkErrorException;

    /**
     * Checks if the removal of this account is allowed.
     * 
     * @param response to send the result back to the AccountManager, will never
     *            be null
     * @param account the account to check, will never be null
     * @return a Bundle result or null if the result is to be returned via the
     *         response. The result will contain either:
     *         <ul>
     *         <li> {@link AccountManager#KEY_INTENT}, or
     *         <li> {@link AccountManager#KEY_BOOLEAN_RESULT}, true if the
     *         removal of the account is allowed, false otherwise
     *         <li> {@link AccountManager#KEY_ERROR_CODE} and
     *         {@link AccountManager#KEY_ERROR_MESSAGE} to indicate an error
     *         </ul>
     * @throws NetworkErrorException if the authenticator could not honor the
     *             request due to a network error
     */
    public Bundle getAccountRemovalAllowed(HtcAccountAuthenticatorResponse response,
            HtcAccount htcAccount) throws NetworkErrorException {
        final Bundle result = new Bundle();
        result.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, true);
        return result;
    }

    /**
     * Returns a Bundle that contains whatever is required to clone the account
     * on a different user. The Bundle is passed to the authenticator instance
     * in the target user via
     * {@link #addAccountFromCredentials(HtcAccountAuthenticatorResponse, Account, Bundle)}
     * . The default implementation returns null, indicating that cloning is not
     * supported.
     * 
     * @param response to send the result back to the AccountManager, will never
     *            be null
     * @param account the account to clone, will never be null
     * @return a Bundle result or null if the result is to be returned via the
     *         response.
     * @throws NetworkErrorException
     * @see {@link #addAccountFromCredentials(HtcAccountAuthenticatorResponse, Account, Bundle)}
     */
    public Bundle getAccountCredentialsForCloning(final HtcAccountAuthenticatorResponse response,
            final HtcAccount htcAccount) throws NetworkErrorException {
        new Thread(new Runnable() {
            public void run() {
                Bundle result = new Bundle();
                result.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, false);
                response.onResult(result);
            }
        }).start();
        return null;
    }

    /**
     * Creates an account based on credentials provided by the authenticator
     * instance of another user on the device, who has chosen to share the
     * account with this user.
     * 
     * @param response to send the result back to the AccountManager, will never
     *            be null
     * @param account the account to clone, will never be null
     * @param accountCredentials the Bundle containing the required credentials
     *            to create the account. Contents of the Bundle are only
     *            meaningful to the authenticator. This Bundle is provided by
     *            {@link #getAccountCredentialsForCloning(HtcAccountAuthenticatorResponse, Account)}
     *            .
     * @return a Bundle result or null if the result is to be returned via the
     *         response.
     * @throws NetworkErrorException
     * @see {@link #getAccountCredentialsForCloning(HtcAccountAuthenticatorResponse, Account)}
     */
    public Bundle addAccountFromCredentials(final HtcAccountAuthenticatorResponse response,
            HtcAccount htcAccount, Bundle accountCredentials) throws NetworkErrorException {
        new Thread(new Runnable() {
            public void run() {
                Bundle result = new Bundle();
                result.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, false);
                response.onResult(result);
            }
        }).start();
        return null;
    }

}
