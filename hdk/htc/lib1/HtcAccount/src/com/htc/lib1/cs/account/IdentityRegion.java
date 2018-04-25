
package com.htc.lib1.cs.account;

import java.util.UUID;

import com.htc.lib1.cs.account.restobj.DataCenterV2;
import com.htc.lib1.cs.account.restobj.RegionV2;

import android.text.TextUtils;

/**
 * The model representation of the a region. It combines the core values of
 * {@link RegionV2} and {@code serverUri} from {@link DataCenterV2}.
 */
public class IdentityRegion {
    private UUID mId;
    private String mName;
    private String mCountryCode;
    private String mServerUri;
    private String mProfileServerUri;
    private boolean mNewsOptOut;

    /**
     * Construct a region object.
     * 
     * @param id Region ID.
     * @param name Region name such as "America".
     * @param countryCode 2-digit country code such as "US".
     * @param serverUri Server base URI for this region.
     * @param profileServerUri Profile server base URI for this region.
     * @param newsletterOptOut Default newsletter option. not.
     */
    public IdentityRegion(UUID id, String name, String countryCode, String serverUri,
            String profileServerUri, boolean newsletterOptOut) {
        if (TextUtils.isEmpty(name))
            throw new IllegalArgumentException("'name' is null or empty.");
        if (TextUtils.isEmpty(countryCode))
            throw new IllegalArgumentException("'countryCode' is null or empty.");
        if (TextUtils.isEmpty(serverUri))
            throw new IllegalArgumentException("'serverUri' is null or empty.");
        if (TextUtils.isEmpty(profileServerUri))
            throw new IllegalArgumentException("'profileServerUri' is null or empty.");

        mId = id;
        mName = name;
        mCountryCode = countryCode;
        mServerUri = serverUri;
        mProfileServerUri = profileServerUri;
        mNewsOptOut = newsletterOptOut;
    }

    /**
     * Get region ID.
     * 
     * @return Region ID.
     */
    public UUID getId() {
        return mId;
    }

    /**
     * Get region name.
     * 
     * @return Region name.
     */
    public String getName() {
        return mName;
    }

    /**
     * Get country code.
     * 
     * @return Country code.
     */
    public String getCountryCode() {
        return mCountryCode;
    }

    /**
     * Get server URI.
     * 
     * @return Server URI.
     */
    public String getServerUri() {
        return mServerUri;
    }

    /**
     * Get profile server URI.
     *
     * @return Profile server URI.
     */
    public String getProfileServerUri() {
        return mProfileServerUri;
    }

    /**
     * Check if default news letter settings is opt-out.
     * 
     * @return True if opt-out, false for opt-in.
     */
    public boolean isNewsletterDefaultOn() {
        return mNewsOptOut;
    }

    @Override
    public String toString() {
        return new StringBuilder(getClass().getSimpleName())
                .append(" {id=\"").append(mId)
                .append("\",name=\"").append(mName)
                .append(",countryCode=\"").append(mCountryCode)
                .append("\",serverUri=\"").append(mServerUri)
                .append("\",newsOptOut=\"").append(mNewsOptOut)
                .append("\"}").toString();
    }
}
