
package com.htc.lib1.cs.push.exception;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.htc.lib1.cs.push.google.GooglePlayServicesAvailabilityUtils;
import com.htc.lib1.cs.push.google.GooglePlayServicesAvailabilityUtils.Availability;

/**
 * Indicates the registration failed because Google Play Services is missing,
 * disabled or needs to update.
 * 
 * @author samael_wang@htc.com
 */
public class GooglePlayServicesAvailabilityException extends RegistrationFailedException {
    private static final long serialVersionUID = 1L;
    private int mStatus;

    /**
     * @param status status code returned from
     *            {@link GooglePlayServicesUtil#isGooglePlayServicesAvailable(android.content.Context)}
     *            .
     */
    public GooglePlayServicesAvailabilityException(int status) {
        super(GooglePlayServicesUtil.getErrorString(status));
        mStatus = status;
    }

    /**
     * @param status status code returned from
     *            {@link GooglePlayServicesUtil#isGooglePlayServicesAvailable(android.content.Context)}
     *            .
     * @param cause Cause of the exception.
     */
    public GooglePlayServicesAvailabilityException(int status, Throwable cause) {
        super(GooglePlayServicesUtil.getErrorString(status), cause);
        mStatus = status;
    }

    /**
     * Get the status of Google Play Services.
     * 
     * @return status code returned from
     *         {@link GooglePlayServicesUtil#isGooglePlayServicesAvailable(android.content.Context)}
     *         .
     */
    public int getStatus() {
        return mStatus;
    }

    /**
     * Get the availability of Google Play Services.
     * 
     * @return {@link Availability}
     */
    public Availability getAvailability() {
        return GooglePlayServicesAvailabilityUtils.isAvaiable(mStatus);
    }
}
