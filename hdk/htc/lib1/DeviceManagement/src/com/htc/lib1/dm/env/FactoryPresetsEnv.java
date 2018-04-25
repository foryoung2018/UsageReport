package com.htc.lib1.dm.env;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import com.htc.lib0.customization.HtcWrapCustomizationManager;
import com.htc.lib0.customization.HtcWrapCustomizationReader;
import com.htc.lib1.dm.logging.Logger;

/**
 * Factory presets information of DM.
 *
 * Retrieve from HTC Lib0 Customization.
 *
 * Created by Joe_Wu on 8/21/14.
 */
public class FactoryPresetsEnv {

    private static final Logger LOGGER = Logger.getLogger("[DM]",FactoryPresetsEnv.class);

    // --------------------------------------------------
    // Customization keys...

    private static final String CUSTOMIZATION_CATEGORY_DEVICE_MANAGEMENT = "DeviceManagement";

    // Server base URI
    private static final String CUSTOMIZATION_SERVICE_BASE_URI = "base_uri";

    // Device manifest resource path
    private static final String CUSTOMIZATION_BOOTSTRAP_DEVICE_MANIFEST_RESOURCE_PATH = "deviceManifest_path";

    // Device manifest resource URI.
    private static final String CUSTOMIZATION_BOOTSTRAP_DEVICE_MANIFEST_RESOURCE_URI = "deviceManifest_uri";

    // --------------------------------------------------
    // Attribute keys...

    private static final String PREFS_NAME = "DeviceManagement.Acc";

    // Key for accessing the override value for the service base URI.
    private static final String PREFS_SERVICE_BASE_URI = "dm.serviceBaseUri";

    // Key for accessing the override value for the bootstrap device manifest resource path.
    private static final String PREFS_BOOTSTRAP_DEVICE_MANIFEST_RESOURCE_PATH = "dm.deviceManifestResourcePath";

    // Key for accessing the override value for the bootstrap device manifest resource URI.
    private static final String PREFS_BOOTSTRAP_DEVICE_MANIFEST_RESOURCE_URI = "dm.deviceManifestResourceUri";

    // --------------------------------------------------
    // Attribute fallbacks...

    // The fallback DM server base URI
    private static final String FALLBACK_SERVICE_BASE_URI = "https://dm.htcsense.com";

    // The fallback bootstrap device manifest resource path.
    private static final String FALLBACK_BOOTSTRAP_DEVICE_MANIFEST_RESOURCE_PATH = "/{platform}/devices/manifest";

    // Singleton instance...
    private static FactoryPresetsEnv sInstance = null;

    // --------------------------------------------------

    private HtcWrapCustomizationReader dmCategoryReader;

    // The named SharedPreferences that is used to hold persistent state.
    protected SharedPreferences prefs;

    // --------------------------------------------------

    public static FactoryPresetsEnv get(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("context is null");
        }

        synchronized (FactoryPresetsEnv.class) {

            if (sInstance == null) {
                sInstance = new FactoryPresetsEnv(context);
                LOGGER.debug("Created new instance: ", sInstance);
            }

            return sInstance;
        }
    }

    private FactoryPresetsEnv(Context context) {
        HtcWrapCustomizationManager customizationMgr = new HtcWrapCustomizationManager();
        dmCategoryReader = customizationMgr.getCustomizationReader(CUSTOMIZATION_CATEGORY_DEVICE_MANAGEMENT, HtcWrapCustomizationManager.READER_TYPE_XML, false);
        prefs = context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
    }

    private String getValueFromPrefOrAcc(String PrefKey, String AccKey, String fallbackValue) {
        String value = null;
        try {
            if (prefs.contains(PrefKey)) {
                value = prefs.getString(PrefKey, null);
            }
            if (value == null) {
                LOGGER.debug("value of from ACC. key:", AccKey);
                value = dmCategoryReader.readString(AccKey, fallbackValue);
                prefs.edit().putString(PrefKey, value).commit();
                LOGGER.debug("value of from ACC. key:", AccKey, " value:", value);
            }
        }catch(Exception ex){
            LOGGER.error("Get value from SharedPreference and ACC failed, using fallbackValue. key=",AccKey,", fallbackValue=",fallbackValue,ex);
            value=fallbackValue;
        }
        return value;
    }

    // --------------------------------------------------
    // DM device manifest resource URI...
    // --------------------------------------------------

    /**
     /**
     * The device manifest resource URI.
     * <p>
     * This may be a RFC 6570 compatible URI template.
     * <p>
     * The value returned is obtained according to the following precedence rules:
     * <ul>
     * <li>An override value defined via this API and stored in SharedPreferences.</li>
     * <li>An override of the factory preset value obtained from ACC</li>
     * <li>An override of the factory preset composed from baseURI and/or resource path.</li>
     * </ul>
     *
     * @return the fully qualified configured bootstrap device manifest resource URI or the empty string if nothing configured.
     */
    public String getBootstrapDeviceManifestResourceUri() {
        return getValueFromPrefOrAcc(PREFS_BOOTSTRAP_DEVICE_MANIFEST_RESOURCE_URI,CUSTOMIZATION_BOOTSTRAP_DEVICE_MANIFEST_RESOURCE_URI,"");
    }

    /**
     * The DM service base URI.
     * <p>
     * The value returned is obtained according to the following precedence rules:
     * <ul>
     * <li>An override value defined via this API and stored in SharedPreferences.</li>
     * <li>The value obtained from ACC.</li>
     * <li>The hardwired fallback value.</li>
     * </ul>
     * @return the DM service base URI
     */
    public String getServiceBaseUri() {
        return getValueFromPrefOrAcc(PREFS_SERVICE_BASE_URI,CUSTOMIZATION_SERVICE_BASE_URI,FALLBACK_SERVICE_BASE_URI);
    }

    /**
     * The configured device manifest resource path.
     * <p>
     * This may be a RFC 6570 compatible URI template.
     * <p>
     * The value returned is obtained according to the following precedence rules:
     * <ul>
     * <li>An override value defined via this API and stored in SharedPreferences.</li>
     * <li>The value obtained from ACC.</li>
     * <li>The hardwired fallback value.</li>
     * </ul>
     * @return
     */
    public String getBootstrapDeviceManifestResourcePath() {
        return getValueFromPrefOrAcc(PREFS_BOOTSTRAP_DEVICE_MANIFEST_RESOURCE_PATH,CUSTOMIZATION_BOOTSTRAP_DEVICE_MANIFEST_RESOURCE_PATH,FALLBACK_BOOTSTRAP_DEVICE_MANIFEST_RESOURCE_PATH);
    }

    /**
     * Build the fully qualified device manifest resource URI.
     * <p>
     * Builds the URI from a configured URI or constituent parts (service base + resource path).
     * <p>
     * If a fully qualified resource URI has been configured, the configured value takes precedence
     * over any configured constituent parts.
     *
     * @return the fully qualified device manifest resource URI
     */
    public String buildConfiguredBootstrapDeviceManifestResourceUri() {

        // If a fully qualified URI has been configured, use that.
        if (!TextUtils.isEmpty(getBootstrapDeviceManifestResourceUri())) {
            return getBootstrapDeviceManifestResourceUri();
        }

        // Build the fully qualified URI from constituent parts...
        StringBuilder builder = new StringBuilder();

        String serviceBaseUri = getServiceBaseUri();
        builder.append(serviceBaseUri);
        if (!serviceBaseUri.endsWith("/")) {
            builder.append("/");
        }

        String resourcePath = getBootstrapDeviceManifestResourcePath();
        if (resourcePath.startsWith("/")) {
            resourcePath = resourcePath.substring(1);
        }
        builder.append(resourcePath);

        return builder.toString();
    }

}
