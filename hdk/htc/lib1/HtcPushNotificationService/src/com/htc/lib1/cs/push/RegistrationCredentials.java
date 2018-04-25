
package com.htc.lib1.cs.push;

import android.text.TextUtils;


/**
 * PNS registration credentials which contains an id and a key.
 * 
 * @author samael_wang@htc.com
 */
public class RegistrationCredentials {
    private String mId;
    private String mKey;

    /**
     * Compose a credentials from the corresponding web class object.
     * 
     * @param response A non-{@code null} response object.
     */
    public RegistrationCredentials(RegisterResponse response) {
        if (response == null || TextUtils.isEmpty(response.regId)
                || TextUtils.isEmpty(response.regKey))
            throw new IllegalArgumentException("'response' is null or the content is invalid.");

        mId = response.regId;
        mKey = response.regKey;
    }

    public RegistrationCredentials(String regId, String regKey) {
        if (TextUtils.isEmpty(regId))
            throw new IllegalArgumentException("'regId' is null or empty.");
        if (TextUtils.isEmpty(regKey))
            throw new IllegalArgumentException("'regKey' is null or empty.");

        mId = regId;
        mKey = regKey;
    }

    /**
     * Get the registration ID.
     * 
     * @return ID
     */
    public String getId() {
        return mId;
    }

    /**
     * Get the registration key.
     * 
     * @return Key.
     */
    public String getKey() {
        return mKey;
    }
}
