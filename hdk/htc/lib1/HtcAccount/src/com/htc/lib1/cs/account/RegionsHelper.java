
package com.htc.lib1.cs.account;

import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.htc.lib1.cs.CallbackFutureTask;
import com.htc.lib1.cs.CallbackFutureTask.Callback;
import com.htc.lib1.cs.DeviceProfileHelper;
import com.htc.lib1.cs.logging.CommLoggerFactory;
import com.htc.lib1.cs.logging.HtcLogger;
import com.htc.lib1.cs.workflow.AsyncWorkflowTask;

/**
 * {@link RegionsHelper} manages the available regions, and
 */
public class RegionsHelper {
    private static final String FALLBACK_COUNTRY_CODE = "US";
    private static final long TIMEOUT_SECONDS = 30;
    private static final Object LOCK = new Object();
    private static RegionsHelper sInstance;
    private HtcLogger mLogger = new CommLoggerFactory(this).create();
    private String mServerUri = HtcAccountDefs.DEFAULT_SERVER_URI;
    private Context mContext;
    private List<IdentityRegion> mRegionList;
    private String mGeoIpCountryCode;
    private CallbackFutureTask<List<IdentityRegion>> mGetRegionsFuture;
    private CallbackFutureTask<String> mGetGeoIpCountryFuture;

    /**
     * Create a new RegionsManager object.
     * 
     * @param context Context to construct with. It will be only used to get
     *            application context and {@link RegionsHelper} will be
     *            construct on the application context.
     */
    private RegionsHelper(Context context) {
        if (context == null)
            throw new IllegalArgumentException("'context' is null.");

        /*
         * Ensure constructing on application context as this is a long life
         * singleton object.
         */
        mContext = context.getApplicationContext();
    }

    /**
     * Get the singleton {@link RegionsHelper} instance. If the instance is not
     * existed yet, construct a new one with given context. The object is
     * guaranteed to be construct on application context.
     * 
     * @param context Context to construct with if necessary.
     * @return {@link RegionsHelper} instance object.
     */
    public static synchronized RegionsHelper get(Context context) {
        if (sInstance == null)
            sInstance = new RegionsHelper(context);

        return sInstance;
    }

    /**
     * Check if regions information has been initialized.
     * 
     * @return True if initialized, false otherwise.
     */
    public boolean hasRegionsInitialized() {
        boolean init;

        synchronized (LOCK) {
            init = (mRegionList != null);
        }

        mLogger.verbose("initialized = ", init);
        return init;
    }

    /**
     * Reset the regions status, i.e. clear regions list.
     */
    public void resetRegions() {
        mLogger.verbose();

        synchronized (LOCK) {
            // Stop running task.
            if (mGetRegionsFuture != null) {
                mGetRegionsFuture.cancel(true /* mayInterruptIfRunning */);
                mGetRegionsFuture = null;
            }

            // Clear regions.
            mRegionList = null;
        }
    }

    /**
     * Get regions. This might be a block call if region list is not ready yet.
     * 
     * @param serverUri Server URI to use.
     * @return Non-{@code null} region list.
     * @throws GetRegionsFailedException If get regions failed for some reasons.
     */
    public List<IdentityRegion> blockingGetRegions(String serverUri)
            throws GetRegionsFailedException {
        mLogger.verbose();

        CallbackFutureTask<List<IdentityRegion>> regionsFuture = startGetRegionsTask(serverUri);
        CallbackFutureTask<String> geoIpCountryFuture = startGetGeoIpCountryTask(serverUri);

        // Waiting for regions results.
        if (regionsFuture != null) {
            try {
                List<IdentityRegion> regionList = regionsFuture.get(TIMEOUT_SECONDS,
                        TimeUnit.SECONDS);
                synchronized (LOCK) {
                    mRegionList = regionList;
                }
            } catch (TimeoutException e) {
                throw new GetRegionsFailedException(e.getMessage(), e);
            } catch (InterruptedException e) {
                throw new GetRegionsFailedException(e.getMessage(), e);
            } catch (CancellationException e) {
                throw new GetRegionsFailedException(e.getMessage(), e);
            } catch (ExecutionException e) {
                Throwable cause;
                if ((cause = e.getCause()) != null) {
                    throw new GetRegionsFailedException(cause.getMessage(), cause);
                } else {
                    throw new GetRegionsFailedException(e.getMessage(), e);
                }

            }
        }

        // Waiting for Geo IP result.
        if (geoIpCountryFuture != null) {
            try {
                String geoIpCountryCode = geoIpCountryFuture.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
                synchronized (LOCK) {
                    mGeoIpCountryCode = geoIpCountryCode;
                }
            } catch (Exception e) {
                mLogger.warning("Get GeoIP country code failed. Ignored.");
            }
        }

        return mRegionList;
    }

    /**
     * Overloaded method to use previously used server URI.
     * 
     * @return Non-{@code null} region list.
     * @throws GetRegionsFailedException
     */
    public List<IdentityRegion> blockingGetRegions() throws GetRegionsFailedException {
        return blockingGetRegions(mServerUri);
    }

    /**
     * Get region list immediately. If it's not ready then return {@code null}.
     * 
     * @param serverUri Server URI to use.
     * @return {@link IdentityRegion} list or {@code null} if not retrieved yet.
     */
    public List<IdentityRegion> getRegions(String serverUri) {
        mLogger.verbose();

        startGetRegionsTask(serverUri);
        startGetGeoIpCountryTask(serverUri);

        return mRegionList;
    }

    /**
     * Overloaded method to use previously used server URI.
     * 
     * @return {@link IdentityRegion} list or {@code null} if not retrieved yet.
     */
    public List<IdentityRegion> getRegions() {
        return getRegions(mServerUri);
    }

    /**
     * Get the suggested country code that most possibly to be current user
     * country.
     * 
     * @return Country code based on SIM card and Geo IP or the default fallback
     *         country if not able to get the country code from SIM card or geo
     *         IP.
     */
    public String getSuggestedCountryCode() {
        // Try to get country code.
        String countryCode = DeviceProfileHelper.get(mContext).getSimCardISO();
        if (TextUtils.isEmpty(countryCode))
            countryCode = mGeoIpCountryCode;
        if (TextUtils.isEmpty(countryCode))
            countryCode = FALLBACK_COUNTRY_CODE;

        // Ensure all capital.
        countryCode = countryCode.toUpperCase(Locale.ENGLISH);

        mLogger.debug("countryCode: ", countryCode);

        return countryCode;
    }

    /**
     * Get the suggested region that most possibly to be current user region.
     * 
     * @param fallback True to use fallback country code if not able to get the
     *            country code from SIM card or geo IP.
     * @return {@link IdentityRegion} or {@code null} if no suggestion found or
     *         regions are not initialized yet.
     */
    public IdentityRegion getSuggestedRegion(boolean fallback) {
        IdentityRegion suggest = findRegionByCountryCode(getSuggestedCountryCode(), fallback);
        mLogger.verboseS("SuggestedRegion = ", suggest);
        return suggest;
    }

    /**
     * Overloaded method which doesn't use fallback country code.
     * 
     * @return {@link IdentityRegion} or {@code null} if no suggestion found or
     *         regions are not initialized yet.
     */
    public IdentityRegion getSuggestedRegion() {
        return getSuggestedRegion(false);
    }

    /**
     * Find a region by ID.
     * 
     * @param id ID to lookup.
     * @return {@link IdentityRegion} or {@code null} if nothing found.
     */
    public IdentityRegion findRegionById(UUID id) {
        if (mRegionList != null) {
            for (IdentityRegion region : mRegionList)
                if (region.getId().equals(id))
                    return region;
        }
        return null;
    }

    /**
     * Find a region by name.
     * 
     * @param name Name to lookup.
     * @return {@link IdentityRegion} or {@code null} if nothing found.
     */
    public IdentityRegion findRegionByName(String name) {
        if (mRegionList != null) {
            for (IdentityRegion region : mRegionList)
                if (region.getName().equalsIgnoreCase(name))
                    return region;
        }
        return null;
    }

    /**
     * Find a region by country code.
     * 
     * @param countryCode Country code to lookup.
     * @param fallback {@code true} to try fallback country if no regions found
     *            with the given country code.
     * @return {@link IdentityRegion} or {@code null} if nothing found.
     */
    public IdentityRegion findRegionByCountryCode(String countryCode, boolean fallback) {
        if (mRegionList != null && !TextUtils.isEmpty(countryCode)) {
            // Lookup region corresponding to the given country code.
            for (IdentityRegion region : mRegionList)
                if (region.getCountryCode().equalsIgnoreCase(countryCode))
                    return region;

            mLogger.warning("No region found for given countryCode: ", countryCode);

            // No region found. Try fallback country.
            if (fallback && !FALLBACK_COUNTRY_CODE.equals(countryCode)) {
                mLogger.debug("Trying fallback country...");
                for (IdentityRegion region : mRegionList)
                    if (region.getCountryCode().equalsIgnoreCase(FALLBACK_COUNTRY_CODE))
                        return region;

                mLogger.error("No region found for fallback country: " + FALLBACK_COUNTRY_CODE);
            }
        }

        return null;
    }

    /**
     * Get the GeoIP country code.
     * 
     * @return GeoIP country code or {@code null} if regions list is not
     *         initialized yet or failed to retrieve GeoIP country code during
     *         regions initialization.
     */
    public String getGeoIpCountryCode() {
        return mGeoIpCountryCode;
    }

    /**
     * Start a {@link CallbackFutureTask} for {@link GetRegionsCallable}. If
     * it's already running, return the running task directly; If the regions
     * has been initialized, return {@code null}.
     * 
     * @param serverUri Server URI to use. If it differs from the previous used
     *            server URI, the regions list will be re-initialized.
     * @return {@link CallbackFutureTask} or {@code null}.
     */
    private CallbackFutureTask<List<IdentityRegion>> startGetRegionsTask(String serverUri) {
        mLogger.verbose();

        synchronized (LOCK) {
            // Check if it's necessary to initialize.
            if (mRegionList != null && mServerUri.equals(serverUri)) {
                mLogger.debug("Regions already initialized.");
                return null;
            }

            // Create a task or use running task.
            if (mGetRegionsFuture == null || mGetRegionsFuture.isDone()
                    || !mServerUri.equals(serverUri)) {
                mLogger.debug("Creating task...");
                mGetRegionsFuture = new CallbackFutureTask<List<IdentityRegion>>(
                        new GetRegionsCallable(mContext, mServerUri = serverUri),
                        new GetRegionsCallback(),
                        new Handler(Looper.getMainLooper()));

                AsyncWorkflowTask.THREAD_POOL_EXECUTOR.execute(mGetRegionsFuture);
            }

            return mGetRegionsFuture;
        }
    }

    /**
     * Start a {@link CallbackFutureTask} for {@link GetGeoIpCountryCallable}.
     * If it's already running, return the running task directly; If the country
     * code has been initialized, return {@code null}.
     * 
     * @param serverUri Server URI to use. If it differs from the previous used
     *            server URI, the geo IP country code will be re-initialized.
     * @return {@link CallbackFutureTask} or {@code null}.
     */
    private CallbackFutureTask<String> startGetGeoIpCountryTask(String serverUri) {
        mLogger.verbose();

        synchronized (LOCK) {
            // Check if it's necessary to initialize.
            if (!TextUtils.isEmpty(mGeoIpCountryCode) && mServerUri.equals(serverUri)) {
                mLogger.debug("Country code already initialized.");
                return null;
            }

            // Create a task or use running task.
            if (mGetGeoIpCountryFuture == null || mGetGeoIpCountryFuture.isDone()
                    || !mServerUri.equals(serverUri)) {
                mLogger.debug("Creating task...");
                mGetGeoIpCountryFuture = new CallbackFutureTask<String>(
                        new GetGeoIpCountryCallable(mContext, mServerUri = serverUri),
                        new GetGeoIpCountryCallback(),
                        new Handler(Looper.getMainLooper()));

                AsyncWorkflowTask.THREAD_POOL_EXECUTOR.execute(mGetGeoIpCountryFuture);
            }

            return mGetGeoIpCountryFuture;
        }
    }

    /**
     * Callback for the task of {@link GetRegionsCallable}.
     * 
     * @author samael_wang
     */
    private class GetRegionsCallback implements Callback<List<IdentityRegion>> {

        @Override
        public void run(CallbackFutureTask<List<IdentityRegion>> future) {
            synchronized (LOCK) {
                mGetRegionsFuture = null;
                try {
                    mRegionList = future.get();
                } catch (InterruptedException e) {
                    mLogger.warning(e);
                } catch (ExecutionException e) {
                    mLogger.warning(e);
                } catch (CancellationException e) {
                    mLogger.warning(e);
                }
            }
        }

    }

    /**
     * Callback for the task of {@link GetGeoIpCountryCallable}.
     * 
     * @author samael_wang
     */
    private class GetGeoIpCountryCallback implements Callback<String> {

        @Override
        public void run(CallbackFutureTask<String> future) {
            synchronized (LOCK) {
                mGetGeoIpCountryFuture = null;
                try {
                    mGeoIpCountryCode = future.get();
                } catch (InterruptedException e) {
                    mLogger.warning(e);
                } catch (ExecutionException e) {
                    mLogger.warning(e);
                } catch (CancellationException e) {
                    mLogger.warning(e);
                }
            }
        }

    }
}
