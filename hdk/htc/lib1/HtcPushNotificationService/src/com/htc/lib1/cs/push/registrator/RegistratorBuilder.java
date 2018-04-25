
package com.htc.lib1.cs.push.registrator;

import com.htc.lib1.cs.logging.HtcLogger;
import com.htc.lib1.cs.push.AccConfigHelper;
import com.htc.lib1.cs.push.PushLoggerFactory;
import com.htc.lib1.cs.push.PushProvider;
import com.htc.lib1.cs.push.dm2.PnsConfig;
import com.htc.lib1.cs.push.google.GooglePlayServicesAvailabilityUtils;
import com.htc.lib1.cs.push.google.GooglePlayServicesAvailabilityUtils.Availability;

import android.content.Context;

/**
 * Simple factory to build a registrator associated with a push provider.
 */
public class RegistratorBuilder {
    private HtcLogger mLogger = new PushLoggerFactory(this).create();
    private Context mContext;
    private PushProvider mPreferredProvider;
    private PnsConfig mPnsConfig = null;

    /**
     * Create a builder.
     * 
     * @param context Context to operate on.
     */
    public RegistratorBuilder(Context context, PnsConfig config) {
        if (context == null)
            throw new IllegalArgumentException("'context' is null.");
        if (config == null || !config.isValid())
            throw new IllegalArgumentException("'config' is null or its content is not valid.");

        mContext = context;
        mPnsConfig = config;
    }

    /**
     * Set the preferred push provider to use.
     * 
     * @param provider Preferred push provider to associate. If passing
     *            {@code null} or the preferred provider isn't available on the
     *            running device, the builder will try to find a proper provider
     *            when invoking {@link RegistratorBuilder#build()}.
     * @return The builder instance.
     */
    public RegistratorBuilder setPreferedProvider(PushProvider provider) {
        mPreferredProvider = provider;
        return this;
    }

    /**
     * Build a registrator. If the provider has been explicitly given by calling
     * {@link RegistratorBuilder#setPreferedProvider(PushProvider)}, it builds
     * the registrator accordingly. Otherwise it tries to find a proper provider
     * and build the registrator.
     * 
     * @return Registrator associated with the provider.
     */
    public Registrator build() {
        // Always use Baidu on China SKUs.
        if (AccConfigHelper.REGION_CODE_CHINA == AccConfigHelper.getInstance()
                .getRomRegionId()) {
            mLogger.debug("Create BaiduRegistrator as it runs on a China SKU device.");
            return new BaiduRegistrator(mContext, mPnsConfig);
        }

        // Try preferred provider.
        Availability availability = GooglePlayServicesAvailabilityUtils
                .isAvaiable(GooglePlayServicesAvailabilityUtils
                        .isGooglePlayServicesAvailable(mContext));
        if (mPreferredProvider == PushProvider.GCM) {
            if (availability != Availability.UNRECOVERABLE) {
                mLogger.debug("Create GCMRegistrator as caller requested.");
                return new GCMRegistrator(mContext, mPnsConfig);
            } else {
                mLogger.warning("Requested GCM but Google Play Services is not available. Fallback to auto-detection.");
            }
        } else if (mPreferredProvider == PushProvider.BAIDU) {
            mLogger.debug("Create BaiduRegistrator as caller requested.");
            return new BaiduRegistrator(mContext, mPnsConfig);
        }

        // Use GCM if available.
        if (availability != Availability.UNRECOVERABLE) {
            mLogger.debug("Create GCMRegistrator by auto-selection.");
            return new GCMRegistrator(mContext, mPnsConfig);
        }

        // Otherwise, use Baidu.
        mLogger.debug("Create BaiduRegistrator by auto-selection.");
        return new BaiduRegistrator(mContext, mPnsConfig);
    }
}
