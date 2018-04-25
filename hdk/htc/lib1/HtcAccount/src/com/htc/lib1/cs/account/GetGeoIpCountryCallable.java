
package com.htc.lib1.cs.account;

import java.io.IOException;
import java.util.concurrent.Callable;

import android.content.Context;
import android.text.TextUtils;

import com.htc.lib1.cs.StringUtils;
import com.htc.lib1.cs.account.restobj.GeoIpCountry;
import com.htc.lib1.cs.httpclient.ConnectionException;
import com.htc.lib1.cs.httpclient.ConnectivityException;
import com.htc.lib1.cs.httpclient.HttpException;
import com.htc.lib1.cs.httpclient.ParseResponseException;
import com.htc.lib1.cs.logging.CommLoggerFactory;
import com.htc.lib1.cs.logging.HtcLogger;

/**
 * Callable to retrieve Geo IP country code.
 */
/* package */class GetGeoIpCountryCallable implements Callable<String> {
    private HtcLogger mLogger = new CommLoggerFactory(this).create();
    private Context mContext;
    private String mServerUri;

    /**
     * @param context Context to operate on.
     * @param serverUri Server URI to use.
     */
    public GetGeoIpCountryCallable(Context context, String serverUri) {
        if (context == null)
            throw new IllegalArgumentException("'context' is null.");
        if (TextUtils.isEmpty(serverUri))
            throw new IllegalArgumentException("'serverUri' is null or empty.");

        mContext = context;
        mServerUri = StringUtils.ensureTrailingSlash(serverUri);
    }

    @Override
    public String call() throws ParseResponseException, HttpException, IOException,
            ConnectionException, ConnectivityException, InterruptedException {

        // Initialize REST resource.
        ConfigurationResource confRes = new ConfigurationResource(mContext, mServerUri);

        // Get Geo IP country code.
        GeoIpCountry geoIpCountry = confRes.getGeoIpCountry().getResult();
        mLogger.debugS("IP: ", geoIpCountry.ipAddr, ", CountryCode: ", geoIpCountry.countryCode);
        return geoIpCountry.countryCode;
    }
}
