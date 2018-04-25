package com.htc.lib1.cs.auth.googleplus;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.htc.lib1.cs.JsonUtils;
import com.htc.lib1.cs.PermissionHelper;
import com.htc.lib1.cs.account.HtcAccountDefs;
import com.htc.lib1.cs.account.HtcAccountManager;
import com.htc.lib1.cs.account.HtcAccountManagerCreator;
import com.htc.lib1.cs.account.HtcAccountRestServiceException;
import com.htc.lib1.cs.account.OAuth2ConfigHelper;
import com.htc.lib1.cs.account.restobj.AuthenticationToken;
import com.htc.lib1.cs.account.restobj.DeviceToken;
import com.htc.lib1.cs.account.restobj.HtcAccountProfile;
import com.htc.lib1.cs.account.restobj.SigninWithSocialPayload;
import com.htc.lib1.cs.account.restservice.ConfirmRequiredException;
import com.htc.lib1.cs.account.restservice.IdentityErrorStreamReader;
import com.htc.lib1.cs.account.restservice.IdentityRequestPropertiesBuilder;
import com.htc.lib1.cs.account.server.HtcAccountServerHelper;
import com.htc.lib1.cs.app.ConfirmAccountActivity;
import com.htc.lib1.cs.app.SelfLogActivity;
import com.htc.lib1.cs.auth.AuthLoggerFactory;
import com.htc.lib1.cs.auth.R;
import com.htc.lib1.cs.auth.client.IdentityDefs;
import com.htc.lib1.cs.auth.web.TokenDefs;
import com.htc.lib1.cs.auth.web.WebAuthConfig;
import com.htc.lib1.cs.httpclient.ConnectionException;
import com.htc.lib1.cs.httpclient.ConnectivityException;
import com.htc.lib1.cs.httpclient.HttpClient;
import com.htc.lib1.cs.httpclient.HttpException;
import com.htc.lib1.cs.httpclient.JsonInputStreamReader;
import com.htc.lib1.cs.httpclient.JsonOutputStreamWriter;
import com.htc.lib1.cs.logging.HtcLogger;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by leohsu on 2017/3/24.
 */

public class GoogleAuthTask {
    public static final int REQUIRED_ACTIVITY_REQ_CODES = 6;

    private static final String URL_HEADER_SIGN_IN_GOOGLE =
            "https://www.htcsense.com/auth/htcidgoogle";
    private static final String URL_HEADER_SIGN_IN_GOOGLE_STG =
            "https://www.htctouch.com/auth/htcidgoogle";

    private static final String API_SIGN_IN_WITH_GOOGLE = "Services/Accounts/v2/signin_with_google";
    private static final String API_SIGN_UP_WITH_GOOGLE = "Services/Accounts/v2/signup_with_google";

    private static final String[] REQUIRED_PERMISSIONS = {
            Manifest.permission.GET_ACCOUNTS,
    };

    public enum ResultCode {
        UNDEFINED,
        OK,
        USER_CANCELED,
        NO_AVAILABLE_ACCOUNT,
        GOOGLE_API_ERROR,
        OTHERS,
    }

    private enum InternalResultCode {
        UNDEFINED,
        OK,
        USER_CANCELED,
        NO_AVAILABLE_ACCOUNT,
        GOOGLE_API_ERROR,
        OTHERS,
        SIGN_UP_CONFIRMED;

        ResultCode toResultCode() {
            if (this == UNDEFINED) {
                return ResultCode.UNDEFINED;
            } else if (this == OK) {
                return ResultCode.OK;
            } else if (this == USER_CANCELED) {
                return ResultCode.USER_CANCELED;
            } else if (this == NO_AVAILABLE_ACCOUNT) {
                return ResultCode.NO_AVAILABLE_ACCOUNT;
            } else if (this == GOOGLE_API_ERROR) {
                return ResultCode.GOOGLE_API_ERROR;
            }
            return ResultCode.OTHERS;
        }
    }

    public static boolean isGoogleSign(String url) {
        return url.startsWith(URL_HEADER_SIGN_IN_GOOGLE) ||
                url.startsWith(URL_HEADER_SIGN_IN_GOOGLE_STG);
    }

    private final int REQ_CODE_REQUEST_PERMISSION;
    private final int REQ_CODE_REQUEST_PERMISSION_SETTINGS;
    private final int REQ_CODE_CHOOSE_GOOGLE_ACCOUNT;
    private final int REQ_CODE_RESOLVE_GOOGLE_API_CLIENT;
    private final int REQ_CODE_RECOVER_SIGN_IN_GOOGLE;
    private final int REQ_CODE_CONFIRM_ACCOUNT;

    private Activity mActivity;
    private Callback mCallback;
    private String mAuthTokenType;

    private HtcLogger mLogger = new AuthLoggerFactory(GoogleAuthTask.class).create();

    private boolean mIsStarted = false;
    private boolean mIsDestroyed = false;

    private Handler mHandler;
    private PermissionHelper mPermissionHelper = null;
    private Account mGoogleAccount = null;
    private ResultCode mFinalResultCode = ResultCode.UNDEFINED;
    private Account mFinalResultAccount = null;
    private NotifyEvent mNotifyEvent = new NotifyEvent();

    private String mGoogleToken = null;
    private String mGoogleAccountName = null;
    private String mGoogleAccountUid = null;
    private Intent mConfirmAccountResult = null;

    private DeviceToken mDeviceToken = null;
    HtcAccountServerHelper.RegionConfig mRegionConfig = null;

    public GoogleAuthTask(Activity activity,
                          Callback callback,
                          String authTokenType,
                          int reqCodeStart,
                          int reqCodeLength) {
        if (reqCodeLength < REQUIRED_ACTIVITY_REQ_CODES) {
            throw new IllegalArgumentException("Required " + REQUIRED_ACTIVITY_REQ_CODES +
                    " req codes but only " + reqCodeLength + " is assigned");
        }
        REQ_CODE_REQUEST_PERMISSION = reqCodeStart;
        REQ_CODE_REQUEST_PERMISSION_SETTINGS = reqCodeStart + 1;
        REQ_CODE_CHOOSE_GOOGLE_ACCOUNT = reqCodeStart + 2;
        REQ_CODE_RESOLVE_GOOGLE_API_CLIENT = reqCodeStart + 3;
        REQ_CODE_RECOVER_SIGN_IN_GOOGLE = reqCodeStart + 4;
        REQ_CODE_CONFIRM_ACCOUNT = reqCodeStart + 5;

        mActivity = activity;
        mCallback = callback;
        mAuthTokenType = authTokenType;

        mHandler = new Handler(Looper.getMainLooper());
    }

    public void destroy() {
        mLogger.info();
        mIsDestroyed = true;
        synchronized (this) {
            mActivity = null;
            mCallback = null;
        }
        if (mPermissionHelper != null) {
            mPermissionHelper.destroy();
        }
        mNotifyEvent.setAndNotify(InternalResultCode.USER_CANCELED);
    }

    public void start() {
        if (mIsStarted) {
            throw new IllegalStateException("Start a started task");
        }

        mIsStarted = true;
        checkPermission();
    }

    public boolean handleRequestPermissionResult(int requestCode,
                                                 String[] permissions,
                                                 int[] grantResults) {
        PermissionHelper.ResultCode result = mPermissionHelper.
                handleRequestPermissionResult(requestCode, permissions, grantResults);
        switch (result) {
            case NOT_HANDLED:
                return false;
            case ALL_GRANTED:
                onPermissionGranted();
                break;
            case DENIED:
                onPermissionDenied();
                break;
            case SHOULD_GOTO_SETTINGS:
                onPermissionShouldGotoSettings();
                break;
        }
        return true;
    }

    public boolean handleActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_CODE_REQUEST_PERMISSION_SETTINGS) {
            if (mPermissionHelper.checkPermissions()) {
                onPermissionGranted();
            } else {
                onPermissionDenied();
            }
        } else if (requestCode == REQ_CODE_CHOOSE_GOOGLE_ACCOUNT) {
            switch (resultCode) {
                case Activity.RESULT_OK: {
                    onAccountPicked(data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME));
                    break;
                }
                case Activity.RESULT_CANCELED:
                    callOnFinish(ResultCode.USER_CANCELED);
                    break;
                default:
                    callOnFinish(ResultCode.OTHERS);
            }
            return true;
        } else if (requestCode == REQ_CODE_RESOLVE_GOOGLE_API_CLIENT) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    onSignInRecoveryResult(InternalResultCode.OK);
                    break;
                default:
                    onSignInRecoveryResult(InternalResultCode.GOOGLE_API_ERROR);
                    break;
            }
        } else if (requestCode == REQ_CODE_RECOVER_SIGN_IN_GOOGLE) {
            mLogger.info("Recover from signing in: " + resultCode);
            switch (resultCode) {
                case Activity.RESULT_OK:
                    onSignInRecoveryResult(InternalResultCode.OK);
                    break;
                case Activity.RESULT_CANCELED:
                    onSignInRecoveryResult(InternalResultCode.USER_CANCELED);
                    break;
                default:
                    onSignInRecoveryResult(InternalResultCode.OTHERS);
            }
            return true;
        } else if (requestCode == REQ_CODE_CONFIRM_ACCOUNT) {
            mLogger.info("Confirm account: " + resultCode + ", data = " + data);
            switch (resultCode) {
                case Activity.RESULT_OK:
                    onAccountConfirmedResult(InternalResultCode.SIGN_UP_CONFIRMED, data);
                    break;
                case Activity.RESULT_CANCELED:
                    onAccountConfirmedResult(InternalResultCode.USER_CANCELED, null);
                    break;
                default:
                    onAccountConfirmedResult(InternalResultCode.OTHERS, null);
            }
        }
        return false;
    }

    private void onPermissionGranted() {
        pickGoogleAccount();
    }

    private void onAccountPicked(String accountName) {
        if (!TextUtils.isEmpty(accountName)) {
            Activity activity = getActivity();
            if (activity == null) {
                mLogger.warning("Activity is unavailable");
                return;
            }
            AccountManager am = AccountManager.get(activity);
            Account[] accounts = am.getAccountsByType(PlusDefs.TYPE_GOOGLE_ACCOUNT);
            for (Account account : accounts) {
                if (account.name.equals(accountName)) {
                    mGoogleAccount = account;
                    startAuthentication();
                    return;
                }
            }
        }
        callOnFinish(ResultCode.NO_AVAILABLE_ACCOUNT);
    }

    private void onSignInRecoveryResult(InternalResultCode resultCode) {
        mNotifyEvent.setAndNotify(resultCode);
    }

    private Activity getActivity() {
        Activity activity = mActivity;
        if (activity == null || activity.isFinishing()) {
            return null;
        }
        if (activity instanceof SelfLogActivity) {
            if (((SelfLogActivity)activity).isDestroyedCompact()) {
                return null;
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (activity.isDestroyed()) {
                return null;
            }
        }
        return activity;
    }

    private void checkPermission() {
        Activity activity = getActivity();
        if (activity == null) {
            mLogger.warning("Activity is unavailable");
            return;
        }

        synchronized (this) {
            if (mPermissionHelper == null) {
                mPermissionHelper = new PermissionHelper(
                        activity, REQUIRED_PERMISSIONS, REQ_CODE_REQUEST_PERMISSION);
            }
        }
        if (mPermissionHelper.checkPermissions()) {
            onPermissionGranted();
        } else if (mPermissionHelper.shouldShowRequestPermissionRationale()) {
            showRationaleDialog();
        } else {
            mPermissionHelper.requestPermissions();
        }
    }

    private void showRationaleDialog() {
        Activity activity = getActivity();
        if (activity == null) {
            mLogger.warning("Activity is unavailable");
            return;
        }

        new AlertDialog.Builder(activity)
                .setCancelable(false)
                .setTitle(R.string.dialog_msg_account_permission_title)
                .setMessage(R.string.dialog_msg_account_permission_rationale)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mPermissionHelper.requestPermissions();
                            }
                        })
                .create()
                .show();
    }

    private void onPermissionDenied() {
        mLogger.warning("Unable to grant permission");
        callOnFinish(ResultCode.USER_CANCELED);
    }

    private void onPermissionShouldGotoSettings() {
        Activity activity = getActivity();
        if (activity == null) {
            mLogger.warning("Activity is unavailable");
            return;
        }

        PackageManager pm = activity.getPackageManager();
        CharSequence appLabel = activity.getApplicationInfo().loadLabel(pm);
        String msg = activity.getResources().getString(
                R.string.dialog_msg_account_permission_settings, appLabel);
        new AlertDialog.Builder(activity)
                .setCancelable(false)
                .setTitle(R.string.dialog_msg_account_permission_title)
                .setMessage(msg)
                .setPositiveButton(R.string.nn_settings,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.addCategory(Intent.CATEGORY_DEFAULT);
                                intent.setData(Uri.parse("package:" + mActivity.getPackageName()));
                                mActivity.startActivityForResult(intent,
                                        REQ_CODE_REQUEST_PERMISSION_SETTINGS);
                            }
                        })
                .setNegativeButton(R.string.va_close,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                onPermissionDenied();
                            }
                        })
                .create()
                .show();
    }

    private void pickGoogleAccount() {
        Activity activity = getActivity();
        if (activity == null) {
            mLogger.warning("Activity is unavailable");
            return;
        }

        Intent intent = AccountPicker.newChooseAccountIntent(null, null,
                new String[]{"com.google"},
                false, null, null, null, null);
        try {
            activity.startActivityForResult(intent, REQ_CODE_CHOOSE_GOOGLE_ACCOUNT);
        } catch (Exception e) {
            mLogger.warning("Unable to choose Google account", e);
            callOnFinish(ResultCode.NO_AVAILABLE_ACCOUNT);
        }
    }

    private void startAuthentication() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                InternalResultCode code = signInHtcAccountWithGoogle(false);
                if (code == InternalResultCode.SIGN_UP_CONFIRMED) {
                    code = signInHtcAccountWithGoogle(true);
                }
                callOnFinish(code.toResultCode());
            }
        }).start();
    }

    private InternalResultCode signInHtcAccountWithGoogle(boolean signUpConfirmed) {
        Activity activity = getActivity();
        if (activity == null) {
            mLogger.warning("Activity is unavailable");
            return InternalResultCode.USER_CANCELED;
        }
        Context appContext = activity.getApplicationContext();

        GoogleApiClient googleApiClient = getGoogleApiClient(appContext);
        if (googleApiClient == null) {
            return InternalResultCode.GOOGLE_API_ERROR;
        }

        InternalResultCode code = getGoogleToken(appContext);
        if (code == InternalResultCode.OK) {
            mGoogleAccountName = Plus.AccountApi.getAccountName(googleApiClient);
            Person person = Plus.PeopleApi.getCurrentPerson(googleApiClient);
            if (person != null) {
                mGoogleAccountUid = person.getId();
            }
        }

        if (code == InternalResultCode.OK) {
            mRegionConfig = HtcAccountServerHelper.getRegionConfig(appContext);
            if (mRegionConfig == null) {
                code = InternalResultCode.OTHERS;
            }
        }

        if (code == InternalResultCode.OK) {
            code = signInHtcAccount(appContext, mRegionConfig.serviceUri, signUpConfirmed);
        }

        if (code == InternalResultCode.OK) {
            code = createLocalAccount(appContext);
        }

        if (mGoogleToken != null) {
            try {
                GoogleAuthUtil.clearToken(appContext, mGoogleToken);
            } catch (Exception e) {
                mLogger.warning("Unable to clear Google token");
            }
            mGoogleToken = null;
        }
        googleApiClient.disconnect();

        return code;
    }

    private synchronized void callOnFinish(ResultCode resultCode) {
        if (mFinalResultCode != ResultCode.UNDEFINED) {
            return;
        }
        mFinalResultCode = resultCode;

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Activity activity = getActivity();
                if (activity == null) {
                    mLogger.warning("Activity is unavailable");
                    return;
                }
                Callback callback = mCallback;
                if (callback == null) {
                    mLogger.warning("Callback is unavailable");
                }
                callback.onFinish(mFinalResultCode, mFinalResultAccount);
            }
        });
    }

    private GoogleApiClient getGoogleApiClient(Context context) {
        GoogleApiClient.Builder builder = new GoogleApiClient.Builder(
                context);
        // HTC Account scopes.
        builder.addApi(Plus.API);
        builder.addScope(new Scope(Scopes.PROFILE));
        builder.addScope(new Scope(PlusDefs.SCOPE_EMAIL));
        builder.setAccountName(mGoogleAccount.name);

        GoogleApiClient googleApiClient = builder.build();
        while (true) {
            ConnectionResult result = googleApiClient.blockingConnect(15, TimeUnit.SECONDS);
            if (result.isSuccess()) {
                return googleApiClient;
            } else if (result.hasResolution()) {
                mLogger.debug("Try to resolve error " + result.getErrorCode());
                try {
                    synchronized (mNotifyEvent) {
                        Activity activity = getActivity();
                        if (activity == null) {
                            mLogger.warning("Activity is unavailable");
                            return null;
                        }
                        if (mIsDestroyed) {
                            return null;
                        }
                        try {
                            result.startResolutionForResult(activity, REQ_CODE_RESOLVE_GOOGLE_API_CLIENT);
                        } catch (IntentSender.SendIntentException e) {
                            mLogger.warning("Unable to resolve Google API connection ", e);
                            return null;
                        }
                        mNotifyEvent.wait();
                        if (mNotifyEvent.resultCode != InternalResultCode.OK) {
                            mLogger.warning("Unable to resolve Google API connection");
                            return null;
                        }
                    }
                } catch (InterruptedException e) {
                    mLogger.warning("Interrupted while waiting for Google resolution: ", e);
                    return null;
                }
            } else {
                mLogger.warning("Result = " + result.getErrorCode() + ": " + result.getErrorMessage());
                if (result.getErrorCode() == ConnectionResult.DEVELOPER_ERROR) {
                    mLogger.error("Is app registered on Google API Console correctly?");
                }
                return null;
            }
        }
    }

    private InternalResultCode getGoogleToken(Context context) {
        try {
            mGoogleToken = GoogleAuthUtil.getToken(context, mGoogleAccount, PlusDefs.OAUTH2_SCOPES_AUTH);
            return TextUtils.isEmpty(mGoogleToken) ? InternalResultCode.OTHERS : InternalResultCode.OK;
        } catch (UserRecoverableAuthException e) {
            mLogger.debug("Try to recover auth from exception", e);
            try {
                synchronized (mNotifyEvent) {
                    if (mIsDestroyed) {
                        return InternalResultCode.USER_CANCELED;
                    }
                    Intent intent = e.getIntent();
                    mHandler.post(new StartActivityRunnable(intent, REQ_CODE_RECOVER_SIGN_IN_GOOGLE));
                    mNotifyEvent.wait();
                    if (mNotifyEvent.resultCode != InternalResultCode.OK) {
                        return mNotifyEvent.resultCode;
                    }
                }
            } catch (InterruptedException e1) {
                mLogger.warning("Interrupted while waiting for Google activity: ", e1);
                return InternalResultCode.OTHERS;
            }

            try {
                mGoogleToken = GoogleAuthUtil.getToken(context, mGoogleAccount, PlusDefs.OAUTH2_SCOPES_AUTH);
                return TextUtils.isEmpty(mGoogleToken) ? InternalResultCode.OTHERS : InternalResultCode.OK;
            } catch (Exception e1) {
                mLogger.warning("Failed to retry getting token: ", e);
                return InternalResultCode.OTHERS;
            }
        } catch (GoogleAuthException e) {
            mLogger.warning("Auth problem: ", e);
            if ("UNREGISTERED_ON_API_CONSOLE".equals(e.getMessage())) {
                mLogger.error("Is app registered on Google API Console correctly?");
            }
            return InternalResultCode.OTHERS;
        } catch (IOException e) {
            mLogger.warning("Network problem: ", e);
            return InternalResultCode.OTHERS;
        }
    }

    private InternalResultCode signInHtcAccount(Context context,
                                                String serverUri,
                                                boolean signUpConfirmed) {
        HttpClient httpClient = new HttpClient(context, new IdentityErrorStreamReader(),
                new IdentityRequestPropertiesBuilder(context, context.getPackageName()).build());

        try {
            if (signUpConfirmed) {
                mLogger.debug("Sign up with Google account");
                try {
                    mDeviceToken = new DeviceToken(signUpWithSocialAccount(
                            context, httpClient, serverUri, API_SIGN_UP_WITH_GOOGLE, mGoogleToken));
                } catch (HtcAccountRestServiceException.WrongDataCenterException e) {
                    /* Retry at most once for wrong data center. */
                    serverUri = e.getDataCenterAddress();
                    mDeviceToken = new DeviceToken(signUpWithSocialAccount(
                            context, httpClient, serverUri, API_SIGN_UP_WITH_GOOGLE, mGoogleToken));
                }
            } else {
                mLogger.debug("Sign in with Google account");
                try {
                    mDeviceToken = new DeviceToken(signInWithSocialAccount(
                            context, httpClient, serverUri, API_SIGN_IN_WITH_GOOGLE, mGoogleToken));
                } catch (HtcAccountRestServiceException.WrongDataCenterException e) {
                    /* Retry at most once for wrong data center. */
                    serverUri = e.getDataCenterAddress();
                    mDeviceToken = new DeviceToken(signInWithSocialAccount(
                            context, httpClient, serverUri, API_SIGN_IN_WITH_GOOGLE, mGoogleToken));
                }
            }
        } catch (HtcAccountRestServiceException.WrongDataCenterException e) {
            mLogger.warning("Unable to connect to data center: ", e);
            return InternalResultCode.OTHERS;
        } catch (ConfirmRequiredException e) {
            mLogger.info("Confirm required");
            Intent intent = ConfirmAccountActivity.createIntentForGoogleAccount(
                    context,
                    e.getConfirmAccount(),
                    mGoogleAccountName,
                    mGoogleAccountUid,
                    mRegionConfig.defaultSendEmailAboutProduct);
            synchronized (mNotifyEvent) {
                if (mIsDestroyed) {
                    return InternalResultCode.USER_CANCELED;
                }
                mHandler.post(new StartActivityRunnable(intent, REQ_CODE_CONFIRM_ACCOUNT));
                try {
                    mNotifyEvent.wait();
                } catch (InterruptedException e1) {
                    mLogger.warning("Interrupted while waiting for account confirm");
                    return InternalResultCode.USER_CANCELED;
                }
                return mNotifyEvent.resultCode;
            }
        } catch (HtcAccountRestServiceException e) {
            mLogger.warning("Failed to sign in with Google account: ", e);
            return InternalResultCode.OTHERS;
        } catch (Exception e) {
            mLogger.warning("Failed to sign in with Google account: ", e);
            return InternalResultCode.OTHERS;
        }

        return InternalResultCode.OK;
    }

    private void onAccountConfirmedResult(InternalResultCode resultCode, Intent data) {
        if (resultCode == InternalResultCode.SIGN_UP_CONFIRMED) {
            mConfirmAccountResult = data;
        }
        mNotifyEvent.setAndNotify(resultCode);
    }

    private AuthenticationToken signInWithSocialAccount(
            Context context,
            HttpClient httpClient,
            String serverUri,
            String signInAPI,
            String socialToken) throws
            InterruptedException,
            HttpException,
            ConnectivityException,
            ConnectionException,
            IOException {
        String urlString = serverUri + signInAPI;
        URL url;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            mLogger.warning("Invalid URL " + urlString);
            return null;
        }

        OAuth2ConfigHelper.AuthClient client = WebAuthConfig.get(context).getAuthClient();
        mLogger.debugS("Client ID = " + client.id + ", scopes = " + client.scopes + ", secret = " + client.secret);

        SigninWithSocialPayload socialAccount = new SigninWithSocialPayload();
        socialAccount.socialAccessToken = socialToken;
        socialAccount.clientId = client.id;
        // Fixed scopes for signing in with social accounts.
        String scopes = client.scopes;
        if (!scopes.contains(IdentityDefs.OAUTH_SCOPE_ISSUETOKEN)) {
            scopes = IdentityDefs.OAUTH_SCOPE_ISSUETOKEN + " " + scopes;
        }
        socialAccount.scopes = convertScopeStringToArray(scopes);
        mLogger.debugS("Social payload = " + JsonUtils.toJson(socialAccount).toString());

        return httpClient.post(httpClient
                .getRequestBuilder(url, new JsonInputStreamReader<AuthenticationToken>() {})
                .setDataWriter(new JsonOutputStreamWriter(socialAccount)).build(), null, null)
                .getResult(IdentityDefs.HTTP_TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }

    private AuthenticationToken signUpWithSocialAccount(
            Context context,
            HttpClient httpClient,
            String serverUri,
            String signInAPI,
            String socialToken) throws
            InterruptedException,
            HttpException,
            ConnectivityException,
            ConnectionException,
            IOException {
        String urlString = serverUri + signInAPI;
        URL url;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            mLogger.warning("Invalid URL " + urlString);
            return null;
        }

        OAuth2ConfigHelper.AuthClient client = WebAuthConfig.get(context).getAuthClient();
        mLogger.debugS("Client ID = " + client.id + ", scopes = " + client.scopes + ", secret = " + client.secret);

        SigninWithSocialPayload.AccountDetail accountDetail = new SigninWithSocialPayload.AccountDetail();
        accountDetail.regionId = mRegionConfig.id;
        accountDetail.languageCode = Locale.getDefault().toString();
        accountDetail.secondEmail = mConfirmAccountResult.getStringExtra(
                ConfirmAccountActivity.KEY_RESULT_SECOND_EMAIL);
        accountDetail.sendEmailAboutProducts = mConfirmAccountResult.getBooleanExtra(
                ConfirmAccountActivity.KEY_RESULT_NEWSLETTER_ON, false);

        SigninWithSocialPayload socialAccount = new SigninWithSocialPayload();
        socialAccount.socialAccessToken = socialToken;
        socialAccount.account = accountDetail;
        socialAccount.clientId = client.id;
        // Fixed scopes for signing in with social accounts.
        String scopes = client.scopes;
        if (!scopes.contains(IdentityDefs.OAUTH_SCOPE_ISSUETOKEN)) {
            scopes = IdentityDefs.OAUTH_SCOPE_ISSUETOKEN + " " + scopes;
        }
        socialAccount.scopes = convertScopeStringToArray(scopes);
        socialAccount.confirm = true;
        mLogger.debugS("Social payload = " + JsonUtils.toJson(socialAccount).toString());

        return httpClient.post(httpClient
                .getRequestBuilder(url, new JsonInputStreamReader<AuthenticationToken>() {})
                .setDataWriter(new JsonOutputStreamWriter(socialAccount)).build(), null, null)
                .getResult(IdentityDefs.HTTP_TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }

    private List<String> convertScopeStringToArray(String scopeString) {
        String[] splitedScopes = scopeString.split("[,; ]");
        ArrayList<String> scopeList = new ArrayList(splitedScopes.length);
        for (int i = 0; i < splitedScopes.length; ++i) {
            String scope = splitedScopes[i].trim();
            if (scope.length() > 0) {
                scopeList.add(scope);
            }
        }
        return scopeList;
    }

    private InternalResultCode createLocalAccount(Context context) {
        // Save the URI returned from server, and use it for the following processes
        String serviceUri = mRegionConfig.serviceUri;
        mLogger.info("Got account " + mDeviceToken.getAccountId());
        String newServerUri = mDeviceToken.getServerUri();
        if (!TextUtils.isEmpty(newServerUri)) {
            mLogger.debugS("New server uri = " + newServerUri);
            serviceUri = newServerUri;
        }

        HtcAccountProfile profile = HtcAccountServerHelper.getProfile(
                context, serviceUri, mDeviceToken.getAuthToken());
        if (profile == null) {
            return InternalResultCode.OTHERS;
        }

        String accountName;
        if (!TextUtils.isEmpty(profile.firstName)) {
            accountName = profile.firstName +
                    ((TextUtils.isEmpty(profile.lastName)) ? "" : " " + profile.lastName);
        } else if (!TextUtils.isEmpty(profile.emailAddress)) {
            accountName = profile.emailAddress;
        } else if (!TextUtils.isEmpty(mGoogleAccountName)) {
            accountName = mGoogleAccountName;
        } else {
            accountName = profile.accountId;
        }
        mLogger.debugS("isEmailVerified = " + profile.isEmailVerified);

        // If a local account presents for any reason, remove the existing one.
        HtcAccountManager accountManager =
                HtcAccountManagerCreator.get().createAsAuthenticator(context);
        for (Account exisingAccnt : accountManager
                .getAccountsByType(HtcAccountDefs.TYPE_HTC_ACCOUNT)) {
            try {
                accountManager.removeAccount(exisingAccnt, null, null).getResult();
            } catch (Exception e) {
                mLogger.warning("Failed to remove existent account " + exisingAccnt.name);
            }
        }

        Account account = new Account(accountName, HtcAccountDefs.TYPE_HTC_ACCOUNT);
        accountManager.addAccountExplicitly(account, null, null);
        accountManager.setAuthToken(account, TokenDefs.TYPE_REFRESH_TOKEN, mDeviceToken.getRefreshToken());
        accountManager.setAuthToken(account, TokenDefs.TYPE_LOCAL_ACCOUNT_TOKEN, mDeviceToken.getAuthToken());
        accountManager.setUserData(account, HtcAccountDefs.KEY_ACCOUNT_ID, profile.accountId);
        accountManager.setUserData(account, HtcAccountDefs.KEY_LOCAL_ACCOUNT_AUTH_BY_CLIENT, Boolean.TRUE.toString());
        accountManager.setUserData(account, HtcAccountDefs.KEY_SERVER_URI, serviceUri);
        accountManager.setUserData(account, HtcAccountDefs.KEY_SOCIAL_ACCOUNT_NAME, mGoogleAccountName);
        accountManager.setUserData(account, HtcAccountDefs.KEY_COUNTRY_CODE, mRegionConfig.countryCode);
        accountManager.setUserData(account, HtcAccountDefs.KEY_AUTH_SERVER_URI, mRegionConfig.serviceUri);
        mFinalResultAccount = account;

        // Try to get token for app
        if (!TextUtils.isEmpty(mAuthTokenType)) {
            mLogger.debug("Getting token for app, type = " + mAuthTokenType);
            Activity activity = getActivity();
            if (activity == null) {
                mLogger.warning("Activity is unavailable");
                //TODO: Should we remove account here?
                return InternalResultCode.USER_CANCELED;
            }

            AccountManagerFuture<Bundle> result = accountManager.getAuthToken(
                    account, mAuthTokenType, null, activity, null, null);
            try {
                Bundle bundle = result.getResult();
                String token = bundle.getString(AccountManager.KEY_AUTHTOKEN);
                accountManager.setAuthToken(account, mAuthTokenType, token);
            } catch (Exception e) {
                mLogger.warning("Unable to get token for app");
            }
        }

        return InternalResultCode.OK;
    }

    private class NotifyEvent {
        InternalResultCode resultCode = InternalResultCode.UNDEFINED;

        void setAndNotify(InternalResultCode _resultCode) {
            synchronized (this) {
                resultCode = _resultCode;
                notifyAll();
            }
        }
    }

    private class StartActivityRunnable implements Runnable {
        Intent mIntent;
        int mReqCode;

        StartActivityRunnable(Intent intent, int reqCode) {
            mIntent = intent;
            mReqCode = reqCode;
        }

        @Override
        public void run() {
            Activity activity = getActivity();
            if (activity == null) {
                onSignInRecoveryResult(InternalResultCode.OTHERS);
                return;
            }
            try {
                activity.startActivityForResult(mIntent, mReqCode);
            } catch (Exception e) {
                mLogger.warning("Unable to start Google sign-in activity", e);
                onSignInRecoveryResult(InternalResultCode.OTHERS);
            }
        }
    }

    public interface Callback {
        void onFinish(ResultCode resultCode, Account account);
    }
}
