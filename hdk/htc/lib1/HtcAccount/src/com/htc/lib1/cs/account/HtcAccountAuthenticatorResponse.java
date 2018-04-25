
package com.htc.lib1.cs.account;

import android.accounts.AccountAuthenticatorResponse;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;

import com.htc.lib0.htcdebugflag.HtcWrapHtcDebugFlag;
import com.htc.lib1.cs.auth.BuildConfig;
import com.htc.lib1.cs.logging.CommLoggerFactory;
import com.htc.lib1.cs.logging.HtcLogger;

/**
 * {@link AccountAuthenticatorResponse}-equivalent implementation for HTC
 * Account authenticator.
 * 
 * @author samael_wang
 */
public class HtcAccountAuthenticatorResponse implements Parcelable {

    /**
     * Creator for {@link HtcAccountAuthenticatorResponse}.
     */
    public static final Creator<HtcAccountAuthenticatorResponse> CREATOR =
            new Creator<HtcAccountAuthenticatorResponse>() {
                public HtcAccountAuthenticatorResponse createFromParcel(Parcel source) {
                    return new HtcAccountAuthenticatorResponse(source);
                }

                public HtcAccountAuthenticatorResponse[] newArray(int size) {
                    return new HtcAccountAuthenticatorResponse[size];
                }
            };

    private HtcLogger mLogger = new CommLoggerFactory(this).create();
    private IHtcAccountAuthenticatorResponse mAccountAuthenticatorResponse;

    /**
     * Construct an {@link HtcAccountAuthenticatorResponse} from an
     * {@link IHtcAccountAuthenticatorResponse}.
     * 
     * @param response
     */
    public HtcAccountAuthenticatorResponse(IHtcAccountAuthenticatorResponse response) {
        mAccountAuthenticatorResponse = response;
    }

    /**
     * Construct an {@link HtcAccountAuthenticatorResponse} from a
     * {@link Parcel}.
     * 
     * @param parcel
     */
    public HtcAccountAuthenticatorResponse(Parcel parcel) {
        mAccountAuthenticatorResponse =
                IHtcAccountAuthenticatorResponse.Stub.asInterface(parcel.readStrongBinder());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStrongBinder(mAccountAuthenticatorResponse.asBinder());
    }

    public void onResult(Bundle result) {
        if (HtcWrapHtcDebugFlag.Htc_SECURITY_DEBUG_flag || BuildConfig.DEBUG) {
            result.keySet(); // force it to be unparcelled
            mLogger.verboseS("result=", result);
        }
        try {
            mAccountAuthenticatorResponse.onResult(result);
        } catch (RemoteException e) {
            // this should never happen
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    public void onRequestContinued() {
        mLogger.verbose();
        try {
            mAccountAuthenticatorResponse.onRequestContinued();
        } catch (RemoteException e) {
            // this should never happen
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    public void onError(int errorCode, String errorMessage) {
        mLogger.warning("errorCode=", errorCode, ", errorMessage=", errorMessage);
        try {
            mAccountAuthenticatorResponse.onError(errorCode, errorMessage);
        } catch (RemoteException e) {
            // this should never happen
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

}
