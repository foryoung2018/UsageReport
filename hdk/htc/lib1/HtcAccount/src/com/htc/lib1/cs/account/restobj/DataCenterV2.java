
package com.htc.lib1.cs.account.restobj;

import java.util.UUID;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.htc.lib1.cs.RestObject;

/**
 * Data center information of identity servers. The legacy V1 format has been
 * deprecated.
 * 
 * @author samael_wang
 */
public class DataCenterV2 extends RestObject {
    private static final long serialVersionUID = 1L;

    /**
     * Data center ID.
     */
    @SerializedName("Id")
    public UUID id;

    /**
     * HTTP URI of the data center.
     */
    @SerializedName("ServiceUri")
    public String serviceUri;

    /**
     * Profile server URI of the data center.
     */
    @SerializedName("ProfileServiceUri")
    public String profileServerUri;

    @Override
    public boolean isValid() {
        return id != null && !TextUtils.isEmpty(serviceUri);
    }
}
