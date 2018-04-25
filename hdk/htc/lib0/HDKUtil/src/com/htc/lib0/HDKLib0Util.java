package com.htc.lib0;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.htc.lib0.htcdebugflag.HtcWrapHtcDebugFlag;
import com.htc.lib0.customization.HtcWrapCustomizationManager;
import com.htc.lib0.customization.HtcWrapCustomizationReader;

public class HDKLib0Util {
    private static final String TAG = "HDKLib0Util";
    private static Method s_getHDKBaseVersion = null;
    private static final float MIN_SENSE_VERSION = 6.0f;
    //private static final int HDK_LIB3_SUPPORTED_TYPE;
    //private static final float HDK_BASE_VERSION;

    /**
     * HDK-base version.
     */
    private static final float HDK_VERSION = HDK_VERSION_CODES.KITKAT_1;

    /** Various version strings. */
    public static class HDK_VERSION_CODES {
        /**
         * The original, first, version of HTC HDK for KITKAT.
         */
        public static final float KITKAT_0 = Build.VERSION_CODES.KITKAT;
        public static final float KITKAT_1 = Build.VERSION_CODES.KITKAT + 0.1f;
    }

    /**
     * If the HDKLib3 version is fully supported on the device.
     */
    public static final int FULLY_SUPPORT = 0;

    /**
     * If the HDKLib3 version is not fully supported on the device.
     */
    public static final int NOT_FULLY_SUPPORT = 1;

    /**
     * If the device is not HTC device, HDKLib3 is not supported.
     */
//    public static final int NOT_HTC_DEVICE = 2;

    /**
     * If the HDKLib3 version is not supported on the device.
     */
    public static final int NOT_SUPPORT = 3;

    private static final String HDK_Lib3_API_VERSION_KEY_PREFIX = "HDK_Lib3_API";

//    static {
//        int supportType = NOT_SUPPORT;
//        float hdkBaseVersion = 0.0f;
//        try {
//            // Load method from android.os.Build
//            s_getHDKBaseVersion = Build.class.getMethod("getHDKBaseVersion");
//
//            // If it's HTC framework and Sense Version >= 6.0,
//            // load HDK-base info from HTC framework.
//            if (s_getHDKBaseVersion != null && getSenseVersion() >= MIN_SENSE_VERSION) {
//                hdkBaseVersion = (Float) s_getHDKBaseVersion.invoke(null);
//
//                if (hdkBaseVersion > 0.0f) {
//                    supportType = (HDK_VERSION_CODES.KITKAT_0/*FIX ME, workaround to prevent framework return 19.0*/ > hdkBaseVersion) ?
//                            NOT_FULLY_SUPPORT : FULLY_SUPPORT;
//                } else {
//                    // If we get HDKBaseVersion <= 0.0f.
//                    supportType = NOT_SUPPORT;
//                }
//            }
//        } catch (NoSuchMethodException e) {
//            Log.d(TAG, "NoSuchMethodException");
//            if (HtcWrapHtcDebugFlag.Htc_DEBUG_flag) {
//                e.printStackTrace();
//            }
//        } catch (IllegalAccessException e) {
//            Log.d(TAG, "IllegalAccessException");
//            if (HtcWrapHtcDebugFlag.Htc_DEBUG_flag) {
//                e.printStackTrace();
//            }
//        } catch (IllegalArgumentException e) {
//            Log.d(TAG, "IllegalArgumentException");
//            if (HtcWrapHtcDebugFlag.Htc_DEBUG_flag) {
//                e.printStackTrace();
//            }
//        } catch (InvocationTargetException e) {
//            Log.d(TAG, "InvocationTargetException");
//            if (HtcWrapHtcDebugFlag.Htc_DEBUG_flag) {
//                e.printStackTrace();
//            }
//        }
//
//        HDK_LIB3_SUPPORTED_TYPE = supportType;
//        HDK_BASE_VERSION = hdkBaseVersion;
//    }

    private static float getSenseVersion() {
        HtcWrapCustomizationManager manager = new HtcWrapCustomizationManager();
        HtcWrapCustomizationReader reader = null;
        if (manager != null) {
            reader = manager.getCustomizationReader("system",
                    HtcWrapCustomizationManager.READER_TYPE_XML, false);
        }

        float senseVersion = 0.0f;
        try {
            if (reader != null) {
                final String readSenseVersion = reader.readString("sense_version", "0.0");
                senseVersion = readSenseVersion != null ? Float.parseFloat(readSenseVersion)
                                                        : 0.0f;
            }
        } catch (Exception e) {
            if (HtcWrapHtcDebugFlag.Htc_DEBUG_flag) {
                Log.w(TAG, "Sense version parse failed.", e);
            }
        }
        return senseVersion;
    }

    /**
     *  Check the HDKLib3 version is fully supported or not on the device.
     *
     *  @return One of {@link #FULLY_SUPPORT}, {@link #NOT_FULLY_SUPPORT}, {@link #NOT_HTC_DEVICE} or {@link #NOT_SUPPORT}.
     */
    @Deprecated
    public static int isHDKLib3SupportedInDevice() {
        return FULLY_SUPPORT;
    }

    /**
     *  Check the HDKLib3 version is fully supported or not on the device.
     *
     *  @param context The Android context instance
     *  @return One of {@link #FULLY_SUPPORT}, {@link #NOT_FULLY_SUPPORT}, {@link #NOT_HTC_DEVICE} or {@link #NOT_SUPPORT}.
     */
    @Deprecated
    public static int isHDKLib3SupportedInDevice(Context context) {

        int _HDK_LIB3_SUPPORTED_TYPE = NOT_SUPPORT;
        float fHDKVersion = getHDKVersion(context);
        if (getHDKBaseVersion() > 0.0f) {
            _HDK_LIB3_SUPPORTED_TYPE = (fHDKVersion > getHDKBaseVersion()) ? NOT_FULLY_SUPPORT : FULLY_SUPPORT;
        }
        return _HDK_LIB3_SUPPORTED_TYPE;
    }

    /**
     * Get HDK version.
     *
     * @return HDK version.
     */
    @Deprecated
    public static float getHDKVersion() {
        return HDK_VERSION;
    }

    /**
     * Get HDK version.
     *
     * @param context The Android context instance
     * @return HDK version.
     */
    @Deprecated
    public static float getHDKVersion(Context context) {

        float fHDKVersion = 0.0f;
        String strHDKVersion = null;
        try {
            ApplicationInfo ai =
                    context.getPackageManager().getApplicationInfo(
                            context.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            for (String key : bundle.keySet()) {
                if (key == null)
                    continue;

                if (key.startsWith(HDK_Lib3_API_VERSION_KEY_PREFIX)) {

                    strHDKVersion = bundle.getString(key);
                    break;
                }
            }
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (strHDKVersion != null) {
            if ((strHDKVersion.lastIndexOf(".") > 0) && (strHDKVersion.lastIndexOf(".") > strHDKVersion.indexOf("."))) {
                fHDKVersion = Float.parseFloat(
                        strHDKVersion.substring(0, strHDKVersion.lastIndexOf(".")));
            }
        }

        return fHDKVersion;
    }

    /**
     * Get HDK-base version of the HDK lib3. (HTC framework dependency)
     * The default value is 19.0 for KITKAT Sense60 and after.
     *
     * @return the full HDK-base version of the HDK lib3.
     */
    @Deprecated
    public static float getHDKBaseVersion() {
        return 0.0f;
    }

    /**
     * Returns whether the device is a HTC ODM device.
     *
     * @param context The Android context instance
     * @return <tt>true</tt> if it is a ODM device, <tt>null</tt> when the input context is null.
     */
    public static Boolean isODMDevice(Context context) {
        if (context == null)
            return null;
        else {
            boolean hasFeature = context.getPackageManager().hasSystemFeature("com.htc.software.ODF");
            return hasFeature;
        }
    }

    /**
     * Returns whether the device is a HTC device. A HTC device includes HEP, ODM and Stock UI.
     *
     * @return <tt>true</tt> if it is a HTC device.
     */
    public static boolean isHTCDevice() {
        try {
            Class.forName("com.htc.customization.HtcCustomizationManager");
        } catch (ClassNotFoundException e) {
            return false;
        }
        return true;
    }

    /**
     * Returns whether the device is a HTC Stock UI device.
     *
     * @param context The Android context instance
     * @return <tt>true</tt> if it is a Stock UI device, <tt>null</tt> if the context parameter is null.
     */
    public static Boolean isStockUIDevice(Context context) {
        if (context == null)
            return null;
        else {
            boolean hasHTCFeature = context.getPackageManager().hasSystemFeature("com.htc.software.HTC");
            if (isHTCDevice() && !hasHTCFeature)
                return true;
            else
                return false;
        }
    }

    /**
     * Returns whether the device is a HTC HEP device.
     *
     * @param context The Android context instance
     * @return <tt>true</tt> if it is a HEP device, <tt>null</tt> if the context parameter is null.
     */
    public static Boolean isHEPDevice(Context context) {
        if (context == null)
            return null;
        else {
            boolean hasHTCFeature = context.getPackageManager().hasSystemFeature("com.htc.software.HTC");
            boolean hasODFFeature = context.getPackageManager().hasSystemFeature("com.htc.software.ODF");

            if (hasHTCFeature && !hasODFFeature)
                return true;
            else
                return false;
        }
    }

    /**
     * Returns whether the device is a HTC GPSense device.
     *
     * @param context The Android context instance
     * @return <tt>true</tt> if it is a GPSense device, <tt>null</tt> if the context parameter is null.
     */
    public static Boolean isGPSenseDevice(Context context) {
        if (context == null)
            return null;
        else {
            boolean hasGPSenseFeature = context.getPackageManager().hasSystemFeature("com.htc.software.GPSense");

            if (hasGPSenseFeature)
                return true;
            else
                return false;
        }
    }
    
    /**
     * Thrown when a error found in HDK-base.
     */
    public static class HDKException extends Exception {
        private static final long serialVersionUID = 1;

        public HDKException () {
            super("No such method in HDK-base.");
        }

        public HDKException (String why) {
            super(why);
        }
    }
}
