package com.htc.lib2.opensense.social;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;

/**
 * A class is containing the response from each social network plugin
 * 
 * @hide
 */
public class SocialPluginResponse implements Parcelable {

	private ISocialPluginResponse mSocialPluginResponse;

	/* package private */ SocialPluginResponse(ISocialPluginResponse response) {
		mSocialPluginResponse = response;
	}

	private SocialPluginResponse(Parcel parcel) {
		mSocialPluginResponse = ISocialPluginResponse.Stub.asInterface(parcel
				.readStrongBinder());
	}

	/**
	 * @hide
	 */
	@Override
	public int describeContents() {
		return 0;
	}

	/**
	 * @hide
	 */
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeStrongBinder(mSocialPluginResponse.asBinder());
	}

	/**
	 * This is a standard parcelable skeleton.
	 * <p>
	 * Please refer to {@link Parcelable.Creator} for more information.
	 * 
	 * @hide
	 */
	public static final Creator<SocialPluginResponse> CREATOR = new Creator<SocialPluginResponse>() {

		/**
		 * This is a standard parcelable skeleton.
		 * <p>
		 * Please refer to {@link Parcelable.Creator#createFromParcel(Parcel)} for more information.
		 * 
		 * @hide
		 */
		@Override
		public SocialPluginResponse createFromParcel(Parcel source) {
			return new SocialPluginResponse(source);
		}

		/**
		 * This is a standard parcelable skeleton.
		 * <p>
		 * Please refer to {@link Parcelable.Creator#newArray(int)} for more information.
		 * 
		 * @hide
		 */
		@Override
		public SocialPluginResponse[] newArray(int size) {
			return new SocialPluginResponse[size];
		}

	};

	/**
	 * Called when the operation finished
	 * 
	 * @param result the operation result
	 * 
	 * @hide
	 */
	public void onResult(Bundle result) {
		try {
			mSocialPluginResponse.onResult(result);
		} catch (RemoteException e) {
			// this should never happen
		}
	}

	/**
	 * Called when the operation failed
	 * 
	 * @param errorCode the operation error code
	 * @param errorMessage the operation error message
	 * 
	 * @hide
	 */
	public void onError(int errorCode, String errorMessage) {
		try {
			mSocialPluginResponse.onError(errorCode, errorMessage);
		} catch (RemoteException e) {
			// this should never happen
		}
	}
}
