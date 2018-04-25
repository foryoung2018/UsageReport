
package com.htc.lib1.cs.account;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;

import android.content.Context;
import android.text.TextUtils;

import com.htc.lib1.cs.StringUtils;
import com.htc.lib1.cs.account.restobj.DataCenterV2;
import com.htc.lib1.cs.account.restobj.RegionList;
import com.htc.lib1.cs.account.restobj.RegionV2;
import com.htc.lib1.cs.httpclient.ConnectionException;
import com.htc.lib1.cs.httpclient.ConnectivityException;
import com.htc.lib1.cs.httpclient.HttpException;
import com.htc.lib1.cs.httpclient.ParseResponseException;

/**
 * Callable to retrieve regions table.
 */
/* package */class GetRegionsCallable implements Callable<List<IdentityRegion>> {
    private Context mContext;
    private String mServerUri;

    /**
     * @param context Context to operate on.
     * @param serverUri Server URI to use.
     */
    public GetRegionsCallable(Context context, String serverUri) {
        if (context == null)
            throw new IllegalArgumentException("'context' is null.");
        if (TextUtils.isEmpty(serverUri))
            throw new IllegalArgumentException("'serverUri' is null or empty.");

        mContext = context;
        mServerUri = StringUtils.ensureTrailingSlash(serverUri);
    }

    @Override
    public List<IdentityRegion> call() throws ParseResponseException, IOException,
            ConnectionException,
            ConnectivityException, InterruptedException, HttpException {

        // Initialize REST resource.
        ConfigurationResource confRes = new ConfigurationResource(mContext, mServerUri);

        // Get regions.
        RegionList wregions = confRes.getRegionsV2().getResult();

        // Compose server URI map.
        Map<UUID, String> serverUriMap = new HashMap<UUID, String>();
        Map<UUID, String> profileServerUriMap = new HashMap<>();
        for (DataCenterV2 dc : wregions.dataCenters.Results) {
            serverUriMap.put(dc.id, dc.serviceUri);
            profileServerUriMap.put(dc.id, dc.profileServerUri);
        }


        // Convert to model regions type.
        List<IdentityRegion> regionList = new ArrayList<IdentityRegion>();
        for (RegionV2 wregion : wregions.regions.Results) {
            // Find server URI from data center list.
            String regionServerUri = serverUriMap.get(wregion.dataCenterId);

            // Check server URI.
            if (TextUtils.isEmpty(regionServerUri))
                throw new IllegalStateException("'regionServerUri' is null or empty.");

            String regionProfileServerUri = profileServerUriMap.get(wregion.dataCenterId);

            // Check profile server URI.
            if (TextUtils.isEmpty(regionProfileServerUri))
                throw new IllegalStateException("'regionProfileServerUri' is null or empty.");

            // Compose region object.
            IdentityRegion region = new IdentityRegion(wregion.id, wregion.name,
                    wregion.countryCode, regionServerUri, regionProfileServerUri,
                    wregion.defaultSendEmailAboutProduct);
            regionList.add(region);
        }

        return regionList;
    }
}
