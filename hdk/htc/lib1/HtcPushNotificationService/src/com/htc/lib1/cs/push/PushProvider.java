
package com.htc.lib1.cs.push;

import android.text.TextUtils;

/**
 * String-typed enumeration of push providers.
 */
public class PushProvider {

    /** GCM push provider. */
    public static final PushProvider GCM = new PushProvider("gcm");

    /** Baidu push provider. */
    public static final PushProvider BAIDU = new PushProvider("baidu");

    private String mValue;

    private PushProvider(String value) {
        mValue = value;
    }

    /**
     * Get the string value which compliant the representation in HTC push
     * notification service.
     */
    public String toString() {
        return mValue;
    }

    /**
     * Get the corresponding enumeration from string.
     * 
     * @param value String value which compliant the representation in HTC push
     *            notification service.
     * @return {@link PushProvider} instance or {@code null} if {@code value} is
     *         null or empty.
     */
    public static PushProvider fromString(String value) {
        if (TextUtils.isEmpty(value))
            return null;
        
        if (GCM.mValue.equals(value))
            return GCM;
        else if (BAIDU.mValue.equals(value))
            return BAIDU;
        else
            throw new IllegalArgumentException("Invalid provider type '" + value + "'");
    }
}
