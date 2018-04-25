package com.htc.lib2;

import com.htc.lib2.opensense.internal.SystemWrapper;
import com.htc.lib0.HDKLib0Util;



import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

public class Hms {

    public static enum CompatibilityStatus {
        ERROR_UNKNOWN,
        ERROR_HSP_NOT_SUPPORTED,
        ERROR_HSP_NOT_INSTALLED,
        ERROR_HSP_NOT_ENABLED,
        HMS_APP_UPDATE_REQUIRED,
        HSP_UPDATE_REQUIRED,
        COMPATIBLE
    }

    public static final String BUNDLE_KEY_INCLUDE_API16_NONHTC = "include_api16_nonhtc_device";
    public static final String BUNDLE_KEY_INCLUDE_GOOGLE_PLAY_EDITION = "include_google_play_edition";

    private static final int MINIMUM_VERSION_DIGIT = 3; // [a.b.c]
    private static final String HSP_PACKAGE_NAME = SystemWrapper.getHspPackageName() /* "com.htc.sense.hsp" */;
    private static final String LOG_TAG = Hms.class.getSimpleName();
    private static final String FEATURE_GOOGLE_PLAY_EDITION = "com.google.android.feature.GOOGLE_EXPERIENCE";
    // private static final String FEATURE_HTC_DEVICE = "com.htc.software.HTC";
    private static final String FEATURE_HTC_ODM_DEVICE = "com.htc.software.ODF";

    private static boolean sIsHtcDevice = false;
    private static boolean sIsValidApiLevelHtc = false;
    private static boolean sIsValidApiLevelOther = false;
    private static float sHdkBaseVersion = 0.0f;
    private static float sSenseVersion = 0.0f;

    static {
        sIsHtcDevice = isHtcDevice();
        sIsValidApiLevelHtc = isValidApiLevelHtc();
        sIsValidApiLevelOther = isValidApiLevelOther();
        sHdkBaseVersion = SystemWrapper.Build.getHDKBaseVersion();
        sSenseVersion = getSenseVersion();
    }

    private Hms() {
    }

    public static CompatibilityStatus checkCompatibility(Context context) throws IllegalArgumentException, CompatibilityException {
        return checkCompatibility(context, null);
    }

    public static CompatibilityStatus checkCompatibility(Context context, Bundle bundle) throws IllegalArgumentException, CompatibilityException {
        if ( context == null ) {
            throw new IllegalArgumentException("context == null");
        }

        Context applicationContext = context.getApplicationContext();
        if ( applicationContext == null ) {
            throw new IllegalArgumentException("can not get application context");
        }

        PackageManager manager = applicationContext.getPackageManager();
        if ( manager == null ) {
            throw new IllegalArgumentException("can not get package manager");
        }

        if ( !SystemWrapper.getIgnoreHdkSupportCheck() ) {
            boolean isSupportedDevice = false;

            //Mark for not checking HDK version, because Aero has no HDK version property.
//            if ( !isSupportedDevice ) { // Check Sense 6 HEP or above
//                isSupportedDevice = sHdkBaseVersion > 0.0f && sSenseVersion >= 6.0f;
//            }

            if ( !isSupportedDevice ) { // Check Sense 6 HEP or above
          isSupportedDevice = sIsHtcDevice && sSenseVersion >= 6.0f;
            }
            
            if ( !isSupportedDevice ) { // Check ODM (mimic from com.htc.lib0.HDKLib0Util.isODMDevice() )
                isSupportedDevice = manager.hasSystemFeature(FEATURE_HTC_ODM_DEVICE);
            }
            if ( !isSupportedDevice ) { // Check Stock UI
                boolean isStockUiIncluded = false;
                if ( bundle != null ) {
                    // Currently we treat Google Play Edition device device as Stock UI
                    isStockUiIncluded = bundle.getBoolean(BUNDLE_KEY_INCLUDE_GOOGLE_PLAY_EDITION);
                }
                // boolean hasStockUIFeature = !manager.hasSystemFeature(FEATURE_HTC_DEVICE);
                boolean hasStockUIFeature = manager.hasSystemFeature(FEATURE_GOOGLE_PLAY_EDITION);
                isSupportedDevice = isStockUiIncluded && sIsHtcDevice && sIsValidApiLevelHtc && hasStockUIFeature;
            }
            if ( !isSupportedDevice ) { // Check Non-HTC device with API level
                boolean isNonHtcIncluded = false;
                if ( bundle != null ) {
                    isNonHtcIncluded = bundle.getBoolean(BUNDLE_KEY_INCLUDE_API16_NONHTC);
                }
                isSupportedDevice = isNonHtcIncluded && !sIsHtcDevice && sIsValidApiLevelOther;
            }
            if ( !isSupportedDevice ) {
                return CompatibilityStatus.ERROR_HSP_NOT_SUPPORTED;
            }
        }

        int hspEnableState = PackageManager.COMPONENT_ENABLED_STATE_DEFAULT;

        try {
            hspEnableState = manager.getApplicationEnabledSetting(HSP_PACKAGE_NAME);
        } catch (IllegalArgumentException e) {
            return CompatibilityStatus.ERROR_HSP_NOT_INSTALLED;
        }
        if ( hspEnableState == PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                || hspEnableState == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER ) {
            return CompatibilityStatus.ERROR_HSP_NOT_ENABLED;
        }

        ApplicationInfo hspApplicationInfo = null;
        try {
            hspApplicationInfo = manager.getApplicationInfo(
                    HSP_PACKAGE_NAME,
                    PackageManager.GET_META_DATA
            );
        } catch (NameNotFoundException e) {
        }
        if ( hspApplicationInfo == null ) {
            return CompatibilityStatus.ERROR_HSP_NOT_INSTALLED; // not available for current user
        }

        ApplicationInfo hdkApplicationInfo = null;
        try {
            hdkApplicationInfo = manager.getApplicationInfo(
                    applicationContext.getPackageName(),
                    PackageManager.GET_META_DATA
            );
        } catch (NameNotFoundException e) {
        }
        if ( hdkApplicationInfo == null ) {
            throw new CompatibilityException("hdkApplicationInfo == null"); // should not happen!
        }

        Bundle hspMetaData = hspApplicationInfo.metaData;
        Bundle hdkMetaData = hdkApplicationInfo.metaData;
        if ( hspMetaData == null || hspMetaData.isEmpty() ) {
            return CompatibilityStatus.HSP_UPDATE_REQUIRED;
        }
        if ( hdkMetaData == null || hdkMetaData.isEmpty() ) {
            Log.e(LOG_TAG, "You should add \"manifestmerger.enabled=true\" in your project.properties.");
            throw new CompatibilityException("hdkMetaData == null || hdkMetaData.isEmpty()");
        }

        CompatibilityStatus compatibility = CompatibilityStatus.ERROR_UNKNOWN;
        final String hdkApiPrefix = SystemWrapper.getHdkApiPrefix(); // format: hdkapi_<moduleName>
        final String hspApiPrefix = SystemWrapper.getHspApiPrefix(); // format: hdkapi_<moduleName>

        for (String hdkKey : hdkMetaData.keySet()) {
            if ( TextUtils.isEmpty(hdkKey) ) {
                continue;
            }
            if ( hdkKey.startsWith(hdkApiPrefix) ) {
                String hspKey = hspApiPrefix + hdkKey.substring(hdkApiPrefix.length());
                if ( !hspMetaData.containsKey(hspKey) ) {
                    return CompatibilityStatus.HSP_UPDATE_REQUIRED;
                }
                String hdkVersion = hdkMetaData.getString(hdkKey);
                String hspVersion = hspMetaData.getString(hspKey);
                int[] thisVersionArray = getIntArrayFromString(hdkVersion);
                int[] hspVersionArray = getIntArrayFromString(hspVersion);
                compatibility = compareVersion(thisVersionArray, hspVersionArray);
                if ( compatibility != CompatibilityStatus.COMPATIBLE ) {
                    if ( compatibility == CompatibilityStatus.ERROR_UNKNOWN ) {
                        throw new CompatibilityException("Unknown error. hdkVer: " + hdkVersion + ", hspVer: " + hspVersion);
                    }
                    return compatibility;
                }
            }
        }
        if ( compatibility == CompatibilityStatus.ERROR_UNKNOWN ) {
            throw new CompatibilityException("Unknown error.");
        }
        return compatibility;
    }

    private static CompatibilityStatus compareVersion(int[] hdkVersionArray, int[] hspVersionArray) {
        if ( hdkVersionArray == null ) {
            return CompatibilityStatus.HMS_APP_UPDATE_REQUIRED;
        }
        if ( hspVersionArray == null ) {
            return CompatibilityStatus.HSP_UPDATE_REQUIRED;
        }

        int hdkVersionLength = hdkVersionArray.length;
        int hspVersionLength = hspVersionArray.length;
        if ( hdkVersionLength < MINIMUM_VERSION_DIGIT ) {
            return CompatibilityStatus.HMS_APP_UPDATE_REQUIRED;
        } else if ( hspVersionLength < MINIMUM_VERSION_DIGIT ) {
            return CompatibilityStatus.HSP_UPDATE_REQUIRED;
        } else if ( hdkVersionLength < hspVersionLength ) {
            return CompatibilityStatus.HMS_APP_UPDATE_REQUIRED;
        } else if ( hdkVersionLength > hspVersionLength ) {
            return CompatibilityStatus.HSP_UPDATE_REQUIRED;
        }

        for (int i = 0; i < hdkVersionLength; i++) {
            if ( i >= MINIMUM_VERSION_DIGIT - 1 ) {
                return CompatibilityStatus.COMPATIBLE;
            }
            if ( hdkVersionArray[i] < hspVersionArray[i] ) {
                if ( i == 0 ) { // in case hdk [1.x.y], hsp [2.a.b]
                    return CompatibilityStatus.HMS_APP_UPDATE_REQUIRED;
                }
            }
            if ( hdkVersionArray[i] > hspVersionArray[i] ) {
                return CompatibilityStatus.HSP_UPDATE_REQUIRED;
            }
        }

        return CompatibilityStatus.ERROR_UNKNOWN;
    }

    private static int[] getIntArrayFromString(String versionString) {
        int[] array = new int[MINIMUM_VERSION_DIGIT];
        if ( TextUtils.isEmpty(versionString) ) {
            return array;
        }
        String[] strings = versionString.split("\\.");
        int i;
        for (i = 0; i < strings.length; i++) {
            if ( i >= array.length ) {
                break;
            }
            array[i] = getInt(strings[i]);
        }
        for (int j = i; j < array.length; j++) {
            array[j] = 0;
        }
        return array;
    }

    private static int getInt(String intString) {
        int result = 0;
        try {
            result = Integer.parseInt(intString);
        } catch (Exception e) {
        }
        return result;
    }

    private static float getFloat(String floatString) {
        float result = 0.0f;
        try {
            result = Float.parseFloat(floatString);
        } catch (Exception e) {
        }
        return result;
    }

    private static float getSenseVersion() {
        float result = 0.0f;

        SystemWrapper.HtcCustomizationManager manager = new SystemWrapper.HtcCustomizationManager();
        if ( manager != null ) {
            SystemWrapper.HtcCustomizationReader reader = manager.getCustomizationReader(
                    "system",
                    SystemWrapper.HtcCustomizationManager.READER_TYPE_XML,
                    false
            );
            if ( reader != null ) {
                String resultString = reader.readString("sense_version", "0.0");
                result = getFloat(resultString);
            }
        }
        return result;
    }

    @SuppressLint({ "DefaultLocale", "UnnecessaryCaseChange" })
    private static boolean isHtcDevice() {
    	//Use Lib0 
    	return HDKLib0Util.isHTCDevice();
        //return android.os.Build.BRAND.toLowerCase().equals("htc");
    }

    private static boolean isValidApiLevelHtc() {
        return android.os.Build.VERSION.SDK_INT >= 19;
    }

    private static boolean isValidApiLevelOther() {
        return android.os.Build.VERSION.SDK_INT >= 16;
    }

    public static class CompatibilityException extends Exception {

        private static final long serialVersionUID = -4069620799022043021L;

        private String mErrorMessage = "";

        public CompatibilityException() {
            super();
        }

        public CompatibilityException(String errorMessage) {
            super(errorMessage);
            if ( errorMessage != null ) {
                mErrorMessage = errorMessage;
            }
        }

        public String getErrorMessage() {
            return mErrorMessage;
        }
    }

    public static String getHspPackageName() {
        return HSP_PACKAGE_NAME;
    }
}
