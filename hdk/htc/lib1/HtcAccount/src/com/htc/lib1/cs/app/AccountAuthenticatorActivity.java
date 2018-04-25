
package com.htc.lib1.cs.app;

import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.os.Bundle;

import com.htc.lib1.cs.account.HtcAccountAuthenticatorResponse;

/**
 * An HTC Account extended version of
 * {@link android.accounts.AccountAuthenticatorActivity} which adds customizable
 * error message.
 */
public abstract class AccountAuthenticatorActivity extends SelfLogActivity {
    private HtcAccountAuthenticatorResponse mAccountAuthenticatorResponse = null;
    private Bundle mResultBundle = null;
    private int mErrorCode = AccountManager.ERROR_CODE_CANCELED;
    private String mErrorMessage = "canceled";
    private boolean mActivityRelaunching;

    /**
     * Set the result that is to be sent as the result of the request that
     * caused this Activity to be launched. If result is null or this method is
     * never called then the request will be canceled.
     * 
     * @param result this is returned as the result of the
     *            AbstractAccountAuthenticator request
     */
    public final void setAccountAuthenticatorResult(Bundle result) {
        mResultBundle = result;
    }

    /**
     * Set the error code and error message to indicate an error.
     * 
     * @param errorCode Error code to use. Default
     *            {@link AccountManager#ERROR_CODE_CANCELED}.
     * @param errorMessage Error string to used. Default "canceled".
     */
    public final void setAccountAuthenticatorException(int errorCode, String errorMessage) {
        mResultBundle = null;
        mErrorCode = errorCode;
        mErrorMessage = errorMessage;
    }

    /**
     * Retrieves the AccountAuthenticatorResponse from either the intent or the
     * icicle, if the icicle is non-zero.
     * 
     * @param icicle the save instance data of this Activity, may be null
     */
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        if (icicle == null) {
            mAccountAuthenticatorResponse = getIntent().getParcelableExtra(
                    AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE);
        } else {
            mAccountAuthenticatorResponse = (HtcAccountAuthenticatorResponse) icicle
                    .getParcelable(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE);
        }

        if (mAccountAuthenticatorResponse != null) {
            mAccountAuthenticatorResponse.onRequestContinued();
        }
    }

    /**
     * Get the {@link AccountAuthenticatorResponse} passed from the caller.
     * 
     * @return {@link AccountAuthenticatorResponse} or {@code null} if not
     *         given.
     */
    protected HtcAccountAuthenticatorResponse getAuthenticatorResponse() {
        return mAccountAuthenticatorResponse;
    }

    /**
     * Sends the result or a Constants.ERROR_CODE_CANCELED error if a result
     * isn't present.
     */
    public void finish() {
        if (mAccountAuthenticatorResponse != null) {
            // send the result bundle back if set, otherwise send an error.
            if (mResultBundle != null) {
                mLogger.debugS("onResult(): ", mResultBundle);
                mAccountAuthenticatorResponse.onResult(mResultBundle);
            } else {
                mLogger.warning("onError(): ", mErrorCode, ": ", mErrorMessage);
                mAccountAuthenticatorResponse.onError(mErrorCode, mErrorMessage);
            }
            mAccountAuthenticatorResponse = null;
        }
        super.finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        mLogger.verbose("The activity may be killed by System.");
        mActivityRelaunching = true;
        outState.putParcelable(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE,
                mAccountAuthenticatorResponse);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        mLogger.verbose("The activity is relaunched by System.");
        mActivityRelaunching = false;
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (!mActivityRelaunching) {
            /**
             * If the activity is not restarting, send the response directly.
             * Otherwise if the activity is destroyed directly, the caller will
             * be blocked.
             */
            finish();
        }
    }
}
