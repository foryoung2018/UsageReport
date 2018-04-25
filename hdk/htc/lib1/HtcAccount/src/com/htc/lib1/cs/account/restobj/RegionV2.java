
package com.htc.lib1.cs.account.restobj;

import java.util.UUID;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.htc.lib1.cs.RestObject;

/**
 * Region information of HTC Account. The legacy V1 format has been deprecated.
 * 
 * @author samael_wang
 */
public class RegionV2 extends RestObject {
    private static final long serialVersionUID = 1L;

    /**
     * Id of the region.
     */
    @SerializedName("Id")
    public UUID id;

    /**
     * Localized name of the region.
     */
    @SerializedName("Name")
    public String name;

    /**
     * 2-digit country code of the region.
     */
    @SerializedName("CountryCode")
    public String countryCode;

    /**
     * The id of data center the region belongs to.
     */
    @SerializedName("DataCenterId")
    public UUID dataCenterId;

    /**
     * The default value for newsletter subscription in this region.
     */
    @SerializedName("DefaultSendEmailAboutProduct")
    public boolean defaultSendEmailAboutProduct;

    /**
     * The default language for this region.
     */
    @SerializedName("DefaultLanguage")
    public String defaultLanguage;

    @Override
    public boolean isValid() {
        return id != null && dataCenterId != null && !TextUtils.isEmpty(countryCode);
    }
}
