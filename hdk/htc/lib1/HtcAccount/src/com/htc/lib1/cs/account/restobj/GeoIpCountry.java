
package com.htc.lib1.cs.account.restobj;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.htc.lib1.cs.RestObject;

/**
 * Mapping of IP address and country code.
 * 
 * @author samael_wang
 */
public class GeoIpCountry extends RestObject {
    private static final long serialVersionUID = 1L;

    /**
     * The IPv4 address.
     */
    @SerializedName("ip-addr")
    public String ipAddr;

    /**
     * Corresponding country code for the IP address.
     */
    @SerializedName("country-code")
    public String countryCode;

    @Override
    public boolean isValid() {
        return !TextUtils.isEmpty(ipAddr) && !TextUtils.isEmpty(countryCode);
    }

}
