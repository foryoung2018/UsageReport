
package com.htc.lib1.cs.account.restobj;

import com.google.gson.annotations.SerializedName;
import com.htc.lib1.cs.RestObject;

/**
 * Represents the list of available regions from identity server.
 * 
 * @author samael_wang
 */
public class RegionList extends RestObject {
    private static final long serialVersionUID = 1L;

    /**
     * Available data centers of identity server.
     */
    @SerializedName("DataCenters")
    public SearchResults<DataCenterV2> dataCenters;

    /**
     * Available regions of identity server.
     */
    @SerializedName("Regions")
    public SearchResults<RegionV2> regions;

    @Override
    public boolean isValid() {
        return dataCenters != null && dataCenters.size() > 0
                && regions != null && regions.size() > 0;
    }
}
