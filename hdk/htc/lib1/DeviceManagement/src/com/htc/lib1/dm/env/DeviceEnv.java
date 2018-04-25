package com.htc.lib1.dm.env;

import java.util.Locale;

import android.util.DisplayMetrics;
import com.htc.lib0.customization.HtcWrapCustomizationManager;
import com.htc.lib0.customization.HtcWrapCustomizationReader;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import com.htc.lib1.dm.logging.Logger;

/**
 * Useful information about the device.
 * <p/>
 * Information that can be obtained using standard Android SDK APIs.
 *
 * @author brian_anderson
 */
public class DeviceEnv {

    private static final Logger LOGGER = Logger.getLogger("[DM]", DeviceEnv.class);

    public static final String ANDROID_ID_SCHEME = "ANDID";
    public static final String IMEI_ID_SCHEME = "IMEI";
    public static final String MEID_ID_SCHEME = "MEID";
    public static final String SIP_ID_SCHEME = "SIP";
    public static final String NONE_ID_SCHEME = "NONE";

    // --------------------------------------------------
    // Customization flags...

    private static final String CUSTOMIZATION_CATEGORY_SYSTEM = "system";

    private static final String CUSTOMIZATION_KEY_SENSE_VERSION = "sense_version";
    private static final String CUSTOMIZATION_KEY_EXTRA_SENSE_VERSION = "extra_sense_version";
    private static final String CUSTOMIZATION_KEY_REGION = "region";
    private static final String CUSTOMIZATION_KEY_SKU_ID = "sku_id";

    // --------------------------------------------------
    // Values for ACC:system:region

    public static final int REGION_GLOBAL = 0;
    public static final int REGION_NORTH_AMERICA = 1;
    public static final int REGION_SOUTH_AMERICA = 2;
    public static final int REGION_CHINA = 3;
    public static final int REGION_JAPAN = 4;
    public static final int REGION_ASIA = 5;
    public static final int REGION_EUROPE = 6;
    public static final int REGION_ARABIC = 7;
    public static final int REGION_HK = 8;
    public static final int REGION_TW = 9;
    public static final int REGION_MMR = 10;
    public static final int REGION_PACIFIC = 11;
    public static final int REGION_MIDDLE_EAST = 12;
    public static final int REGION_AFRICA = 13;
    public static final int REGION_AUSTRALIA = 14;

    // --------------------------------------------------

    // --------------------------------------------------
    // Keys for obtaining values from system properties

    // Firmware...
    private static final String SYSTEM_KEY_DEVICE_MID = "ro.mid";
    private static final String SYSTEM_KEY_CID_KEY = "ro.cid";

    // Build info...
    private static final String SYSTEM_KEY_BUILD_DESCRIPTION_KEY = "ro.build.description";
    private static final String SYSTEM_KEY_PROJECT_NAME = "ro.build.project";

    // This has been replaced with ACC flags.
    private static final String SYSTEM_KEY_SENSE_VERSION = "ro.build.sense.version";

    // --------------------------------------------------

    // --------------------------------------------------

    // Singleton instance...
    private static DeviceEnv sInstance = null;

    // --------------------------------------------------

    private Context context;
    private HtcWrapCustomizationReader systemCategoryReader;

    // --------------------------------------------------

    private DeviceEnv(Context context) {
        this.context = context;
        HtcWrapCustomizationManager customizationMgr = new HtcWrapCustomizationManager();
        systemCategoryReader = customizationMgr.getCustomizationReader(CUSTOMIZATION_CATEGORY_SYSTEM, HtcWrapCustomizationManager.READER_TYPE_XML, false);

    }

    public static DeviceEnv get(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("context is null");
        }

        synchronized (DeviceEnv.class) {

            if (sInstance == null) {
                sInstance = new DeviceEnv(context.getApplicationContext());
                LOGGER.debug("Created new instance: ", sInstance);
            }

            return sInstance;
        }
    }


    // --------------------------------------------------
    // Hardware/firmware info...
    // --------------------------------------------------


    /**
     * The device serial number.
     * <p/>
     * From: http://android-developers.blogspot.com/2011/03/identifying-app-installations.html
     * <p/>
     * Since Android 2.3 ("Gingerbread") this is available via android.os.Build.SERIAL.
     * Devices without telephony are required to report a unique device ID here; some phones may do so also.
     * <p/>
     * Note the the device serial number is not guaranteed to be globally unique...but we could probably
     * assume that is unique within a given manufacturer.
     *
     * @return the device serial number
     */
    public String getDeviceSN() {
        // ro.serialno
        return Build.SERIAL;
    }

    /**
     * The IMEI/MEID/ESN of the device as a URN.
     * <p/>
     * The mobile equipment identifier is returned as a URN where the scheme attempts
     * to identify the phone type.
     * <p/>
     * May return <code>null</code> if not available.
     *
     * The mobile equipment identifier (IMEI/MEID/ESN) of the device.
     * <p/>
     * May return <code>null</code> if not available.
     * <p/>
     * Note that it appears that IMEIs are MEIDs, but not vice-versa.  MEIDs can contain hexadecimal codes whilst IMEIs do not.
     * <p/>
     * See:
     * <ul>
     * <li>http://www.tiaonline.org/standards/numbering-resources/mobile-equipment-identifiers-meid</li>
     * <li>http://en.wikipedia.org/wiki/Mobile_equipment_identifier</li>
     * </ul>
     *
     * @return the device mobile equipment identifier as a URN
     */
    public String getMobileEquipmentIdentifierUrn() {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        // Get Device ID
        String deviceId = null;
        try {
            deviceId = tm.getDeviceId();
        } catch (Exception e) {
            LOGGER.warning("Get Identifier Failure", e);
        }

        // No Device ID return null
        if (TextUtils.isEmpty(deviceId)) {
            return "";
        }

        // Get Phone Type
        int phoneType = tm.getPhoneType();

        // Create URN
        switch (phoneType) {
            case TelephonyManager.PHONE_TYPE_CDMA:
                return MEID_ID_SCHEME + ":" + deviceId;

            case TelephonyManager.PHONE_TYPE_GSM:
                return IMEI_ID_SCHEME + ":" + deviceId;

            case TelephonyManager.PHONE_TYPE_SIP:
                return SIP_ID_SCHEME + ":" + deviceId;

            case TelephonyManager.PHONE_TYPE_NONE:
                return NONE_ID_SCHEME + ":" + deviceId;

            default:
                LOGGER.warning("Unknown phone type=", phoneType);
                return null;
        }
    }


    // --------------------------------------------------
    // Other IDs...
    // --------------------------------------------------


    /**
     * Android ID.
     * <p/>
     * This is a 64-bit quantity that is generated and stored when the device first boots. It is reset when the device is wiped.
     *
     * @return the Android ID
     */
    public String getAndroidId() {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    /**
     * CS platform device ID as a URN.
     * <p/>
     * A unique identifier that can be used to identify the device in cases where the device serial number
     * may not be present or valid.
     *
     * @return the CS platform device ID as a URN
     */
    public String getPlatformDeviceIdUrn() {
        return ANDROID_ID_SCHEME + ":" + getAndroidId();
    }


    // --------------------------------------------------
    // Build info...
    // --------------------------------------------------


    public String getBuildFingerprint() {
        // ro.build.fingerprint
        return Build.FINGERPRINT;
    }

    /**
     * The build ID.
     * <p/>
     * Typically a changelist number or Git hash used by the underlying source control system.
     *
     * @return build ID
     */
    public String getBuildId() {
        // ro.build.version.incremental
        return Build.VERSION.INCREMENTAL;
    }

    /**
     * Build tags.
     * <p/>
     * Comma separated list of tags describing the build.
     *
     * @return build tags
     */
    public String getBuildTags() {
        // ro.build.tags
        return Build.TAGS;
    }

    /**
     * Build type.
     * <p/>
     * The type of build, like "user" or "eng".
     *
     * @return the build type
     */
    public String getBuildType() {
        // ro.build.type
        return Build.TYPE;
    }

    /**
     * Build display ID.
     * <p/>
     * A build ID string meant for displaying to the user.
     *
     * @return the build display ID
     */
    public String getBuildDisplayId() {
        return Build.DISPLAY;
    }

    // --------------------------------------------------

    public String getAndroidVersion() {
        // ro.build.version.release
        return Build.VERSION.RELEASE;
    }

    public int getAndroidApiLevel() {
        // ro.build.version.sdk
        return Build.VERSION.SDK_INT;
    }


    // --------------------------------------------------
    // Product info...
    // --------------------------------------------------


    /**
     * The manufacturer of the product.
     *
     * @return the manufacturer name
     */
    public String getManufacturer() {
        // ro.product.manufacturer
        return Build.MANUFACTURER;
    }

    /**
     * The overall name of the product.
     *
     * @return the product name
     */
    public String getProductName() {
        // ro.product.name
        return Build.PRODUCT;
    }

    /**
     * The end user visible name of the product.
     *
     * @return the marketing name
     */
    public String getMarketingName() {
        // ro.product.model
        return Build.MODEL;
    }


    // --------------------------------------------------
    // User defined...
    // --------------------------------------------------


    /**
     * The user's preferred locale (language).
     * <p/>
     * The user's preferred locale which can be changed via Settings>Language.
     *
     * @return the user's preferred locale
     */
    public String getDefaultLocale() {
        return Locale.getDefault().toString();
    }


    /**
     * The region
     * <p/>
     *
     * @return the region ID or <code>null</code> if none defined.
     */
    public Integer getRegionID() {
        int value = systemCategoryReader.readInteger(CUSTOMIZATION_KEY_REGION, Integer.MIN_VALUE);
        if (value == Integer.MIN_VALUE) {
            return null;
        } else {
            return Integer.valueOf(value);
        }
    }

    /**
     * The SKU ID
     * <p/>
     *
     * @return the SKU ID or <code>null</code> if none defined.
     */
    public Integer getSkuID() {
        int value = systemCategoryReader.readInteger(CUSTOMIZATION_KEY_SKU_ID, Integer.MIN_VALUE);
        if (value == Integer.MIN_VALUE) {
            return null;
        } else {
            return Integer.valueOf(value);
        }
    }

    /**
     * The core Sense version number.
     *
     * @return the core Sense version or <code>null</code> if unknown.
     */
    public String getSenseVersion() {
        return systemCategoryReader.readString(CUSTOMIZATION_KEY_SENSE_VERSION, null);
    }

    /**
     * The "extra" Sense version.
     * <p/>
     * This variant includes additional information associated with the Sense version.
     * For example:
     * <pre>
     * Sense 6.0:           sense_version = 6.0       extra_sense_version = 6.0
     * Sense 6.0a:          sense_version = 6.0       extra_sense_version = 6.0a
     * Desire Sense 6.0:    sense_version = 6.0       extra_sense_version = desire6.0
     * </pre>
     *
     * @return the "extra" Sense version or <code>null</code> if unknown.
     */
    public String getExtraSenseVersion() {
        return systemCategoryReader.readString(CUSTOMIZATION_KEY_EXTRA_SENSE_VERSION, null);
    }


    // --------------------------------------------------
    // Hardware/firmware info...
    // --------------------------------------------------


    /**
     * The HTC device model id
     *
     * @return the device model id or <code>null</code> if undefined
     */
    public String getDeviceModelId() {
        return SystemWrapper.SystemProperties.get(SYSTEM_KEY_DEVICE_MID, null);
    }

    /**
     * The HTC customer ID (channel through which the device is sold).
     *
     * @return the CID or <code>null</code> if undefined
     */
    public String getCID() {
        return SystemWrapper.SystemProperties.get(SYSTEM_KEY_CID_KEY, null);
    }

    // --------------------------------------------------
    // Build info...
    // --------------------------------------------------


    /**
     * The HTC specific build description.
     *
     * @return the build description or <code>null</code> if undefined
     */
    public String getBuildDescription() {
        return SystemWrapper.SystemProperties.get(SYSTEM_KEY_BUILD_DESCRIPTION_KEY, null);
    }

    /**
     * The HTC specific ROM version.
     *
     * @return the ROM version or <code>null</code> if undefined
     */
    public String getRomVersion() {

        String buildDescription = getBuildDescription();

        if (TextUtils.isEmpty(buildDescription)) {
            return null;
        }

        StringBuffer romVersion = new StringBuffer();

        char ch;
        buildDescription = buildDescription.trim();
        for (int i = 0; i < buildDescription.length(); i++) {
            ch = buildDescription.charAt(i);
            if (ch == '.' || Character.isDigit(ch)) {
                romVersion.append(ch);
            } else {
                break;
            }
        }
        return romVersion.toString();
    }

    /**
     * The HTC (internal) project name.
     *
     * @return the project name or <code>null</code> if undefined
     */
    public String getProjectName() {
        return SystemWrapper.SystemProperties.get(SYSTEM_KEY_PROJECT_NAME, null);
    }

    /**
     * The Sense version (using the legacy system property).
     * <p/>
     * This is the legacy mechanism for accessing the Sense version using system properties.
     * This has been replaced in Sense 6 and beyond with ACC flags sense_version, extra_sense_version, ...
     *
     * @return the Sense version using the legacy system property of <code>null</code> if undefined
     */
    public String getLegacySenseVersion() {
        return SystemWrapper.SystemProperties.get(SYSTEM_KEY_SENSE_VERSION, null);
    }

    public Float getScreenDensity() {
        DisplayMetrics displaymetrics = context.getResources().getDisplayMetrics();
        return displaymetrics.density;
    }

    public Integer getScreenHeight() {
        DisplayMetrics displaymetrics = context.getResources().getDisplayMetrics();
        return displaymetrics.heightPixels;
    }

    public Integer getScreenWidth() {
        DisplayMetrics displaymetrics = context.getResources().getDisplayMetrics();
        return displaymetrics.widthPixels;
    }

}
