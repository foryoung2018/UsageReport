package com.htc.lib2.opensense.internal;

import android.text.TextUtils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.IDN;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;

/**
 * @hide
 */
public class SystemWrapper {

    private static final boolean INTERNAL_DEBUG_FLAG = "userdebug".equals(android.os.Build.TYPE) || "eng".equals(android.os.Build.TYPE);

    public static final String APP_PACKAGE_NAME = com.htc.lib2.opensense.BuildConfig.CUSTOM_AUTHORITY_PREFIX;

    private static final String CUSTOM_AUTHORITY_PREFIX = APP_PACKAGE_NAME != null ? APP_PACKAGE_NAME + "." : "";
    private static final String DEFAULT_HSP_PACKAGE_NAME = APP_PACKAGE_NAME != null ? APP_PACKAGE_NAME : "com.htc.sense.hsp";

    private static final String DEFAULT_CACHEMANAGER_AUTHORITY = CUSTOM_AUTHORITY_PREFIX + "com.htc.sense.hsp.opensense.cachemanager";
    private static final String DEFAULT_PLUGINMANAGER_AUTHORITY = CUSTOM_AUTHORITY_PREFIX + "com.htc.sense.hsp.opensense.plugin";
    private static final String DEFAULT_SOCIALMANAGER_AUTHORITY = CUSTOM_AUTHORITY_PREFIX + "com.htc.sense.hsp.opensense.social";
    private static final String DEFAULT_SOCIALMANAGER_PACKAGE_NAME = "com.htc.sense.hsp.opensense.social";
    private static final String DEFAULT_PLUGINMANAGER_PACKAGE_NAME = "com.htc.sense.hsp.opensense.plugin";
    private static final String DEFAULT_HDK_API_PREFIX = "hdkapi_";
    private static final String DEFAULT_HSP_API_PREFIX = DEFAULT_HDK_API_PREFIX;

    private static String sCacheManagerAuthority = null;
    private static String sPluginManagerAuthority = null;
    private static String sSocialManagerAuthority = null;
    private static String sSocialManagerPackageName = null;
    private static String sPluginManagerPackageName = null;
    private static String sHspPackageName = null;
    private static String sHdkApiPrefix = null;
    private static String sHspApiPrefix = null;
    private static boolean sIgnoreHdkSupportCheck = false;

    /**
     * @hide
     */
    public static class Build {

        /**
         * @hide
         */
        public static float getHDKBaseVersion() {
            // return android.os.Build.getHDKBaseVersion();
            return invokePublicStaticMethod(
                    "android.os.Build",
                    "getHDKBaseVersion",
                    0.0f,
                    null,
                    null
            );
        }
    }

    /**
     * @hide
     */
    public static class Environment {

        private static final File DEFAULT_PHONE_STORAGE_FILE = new File("/storage/emmc");
        private static final File DEFAULT_REMOVABLE_STORAGE_FILE = new File("/storage/ext_sd");

        /**
         * @hide
         */
        public static final String MEDIA_REMOVED = android.os.Environment.MEDIA_REMOVED;

        /**
         * @hide
         */
        public static final String MEDIA_MOUNTED = android.os.Environment.MEDIA_MOUNTED;

        /**
         * @hide
         */
        public static String getExternalStorageState() {
            return android.os.Environment.getExternalStorageState();
        }

        /**
         * @hide
         */
        public static File getExternalStorageDirectory() {
            return android.os.Environment.getExternalStorageDirectory();
        }

        /**
         * @hide
         */
        public static boolean isExternalStorageEmulated() {
            return android.os.Environment.isExternalStorageEmulated();
        }

        /**
         * @hide
         */
        public static boolean hasRemovableStorageSlot() {
            // return android.os.Environment.hasRemovableStorageSlot();
            return invokePublicStaticMethod(
                    "android.os.Environment",
                    "hasRemovableStorageSlot",
                    false,
                    null,
                    null
            );
        }

        /**
         * @hide
         */
        public static String getRemovableStorageState() {
            // return android.os.Environment.getRemovableStorageState();
            return invokePublicStaticMethod(
                    "android.os.Environment",
                    "getRemovableStorageState",
                    MEDIA_REMOVED,
                    null,
                    null
            );
        }

        /**
         * @hide
         */
        public static File getRemovableStorageDirectory() {
            // return android.os.Environment.getRemovableStorageDirectory();
            return invokePublicStaticMethod(
                    "android.os.Environment",
                    "getRemovableStorageDirectory",
                    DEFAULT_REMOVABLE_STORAGE_FILE,
                    null,
                    null
            );
        }

        /**
         * @hide
         */
        public static boolean hasPhoneStorage() {
            // return android.os.Environment.hasPhoneStorage();
            return invokePublicStaticMethod(
                    "android.os.Environment",
                    "hasPhoneStorage",
                    false,
                    null,
                    null
            );
        }

        /**
         * @hide
         */
        public static String getPhoneStorageState() {
            // return android.os.Environment.getPhoneStorageState();
            return invokePublicStaticMethod(
                    "android.os.Environment",
                    "getPhoneStorageState",
                    MEDIA_REMOVED,
                    null,
                    null
            );
        }

        /**
         * @hide
         */
        public static File getPhoneStorageDirectory() {
            // return android.os.Environment.getPhoneStorageDirectory();
            return invokePublicStaticMethod(
                    "android.os.Environment",
                    "getPhoneStorageDirectory",
                    DEFAULT_PHONE_STORAGE_FILE,
                    null,
                    null
            );
        }
    }

    /**
     * @hide
     */
    public static class HtcCustomizationManager {

        public static final int READER_TYPE_XML = 0x0001;
        public static final int READER_TYPE_BINARY = 0x0002;

        /**
         * @hide
         */
        public HtcCustomizationManager() {
        }

        /**
         * @hide
         */
        public HtcCustomizationReader getCustomizationReader(String name, int type, boolean needSimReady) {
            return new HtcCustomizationReader(name, type, needSimReady);
        }
    }

    /**
     * @hide
     */
    public static class HtcCustomizationReader {

        private static final String CUSTOMIZATION_MANAGER_CLASSNAME = "com.htc.customization.HtcCustomizationManager"; // This class is in framework, not in HDK
        private static final String CUSTOMIZATION_MANAGER_METHOD_GETINSTANCE = "getInstance";
        private static final String CUSTOMIZATION_MANAGER_METHOD_GETREADER = "getCustomizationReader";
        private static final String CUSTOMIZATION_READER_CLASSNAME = "com.htc.customization.HtcCustomizationReader"; // This class is in framework, not in HDK

        private boolean mNeedSimReady = false;
        private int mType = 0;
        private String mName = null;
        private Class<?> mReaderClazz = null;
        private Object mReaderInstance = null;

        /**
         * @hide
         */
        public HtcCustomizationReader(String name, int type) {
            this(name, type, false);
        }

        /**
         * @hide
         */
        public HtcCustomizationReader(String name, int type, boolean needSimReady) {
            mName = name;
            mType = type;
            mNeedSimReady = needSimReady;
        }

        /**
         * @hide
         */
        public int readInteger(String key, int defaultValue) {
            return invokeHtcCustomizationReaderMethod(
                    "readInteger",
                    defaultValue,
                    new Class<?>[] { String.class, int.class },
                    new Object[] { key, defaultValue }
            );
        }

        /**
         * @hide
         */
        public String readString(String key, String defaultValue) {
            return invokeHtcCustomizationReaderMethod(
                    "readString",
                    defaultValue,
                    new Class<?>[] { String.class, String.class },
                    new Object[] { key, defaultValue }
            );
        }

        /**
         * @hide
         */
        public boolean readBoolean(String key, boolean defaultValue) {
            return invokeHtcCustomizationReaderMethod(
                    "readBoolean",
                    defaultValue,
                    new Class<?>[] { String.class, boolean.class },
                    new Object[] { key, defaultValue }
            );
        }

        /**
         * @hide
         */
        public byte readByte(String key, byte defaultValue) {
            return invokeHtcCustomizationReaderMethod(
                    "readByte",
                    defaultValue,
                    new Class<?>[] { String.class, byte.class },
                    new Object[] { key, defaultValue }
            );
        }

        /**
         * @hide
         */
        public int[] readIntArray(String key, int[] defaultValue) {
            return invokeHtcCustomizationReaderMethod(
                    "readIntArray",
                    defaultValue,
                    new Class<?>[] { String.class, int[].class },
                    new Object[] { key, defaultValue }
            );
        }

        /**
         * @hide
         */
        public String[] readStringArray(String key, String[] defaultValue) {
            return invokeHtcCustomizationReaderMethod(
                    "readStringArray",
                    defaultValue,
                    new Class<?>[] { String.class, String[].class },
                    new Object[] { key, defaultValue }
            );
        }

        @SuppressWarnings("unchecked")
        private <T> T invokeHtcCustomizationReaderMethod(String methodName, T defaultReturnValue,
                Class<?>[] parameterTypes, Object[] parameterValues) {
            if ( mReaderInstance == null || mReaderClazz == null ) {
                Class<?> managerClazz = null;
                Object managerInstance = null;
                try {
                    managerClazz = Class.forName(CUSTOMIZATION_MANAGER_CLASSNAME);
                    Method managerMethodToGetInstance = managerClazz.getMethod(
                            CUSTOMIZATION_MANAGER_METHOD_GETINSTANCE,
                            (Class<?>[]) null
                    );
                    managerInstance = managerMethodToGetInstance.invoke(
                            null,
                            (Object[]) null
                    );
                } catch (Exception e) {
                    printStackTrace(e);
                } catch (Error e) {
                    printStackTrace(e);
                }
                if ( managerClazz == null || managerInstance == null ) {
                    return defaultReturnValue;
                }

                Method managerMethodToGetReader = null;
                try {
                    managerMethodToGetReader = managerClazz.getMethod(
                            CUSTOMIZATION_MANAGER_METHOD_GETREADER,
                            new Class<?>[] { String.class, int.class, boolean.class }
                    );
                    mReaderClazz = Class.forName(CUSTOMIZATION_READER_CLASSNAME);
                    mReaderInstance = managerMethodToGetReader.invoke(
                            managerInstance,
                            new Object[] { mName, mType, mNeedSimReady }
                    );
                } catch (Exception e) {
                    printStackTrace(e);
                } catch (Error e) {
                    printStackTrace(e);
                }
                if ( managerMethodToGetReader == null ) {
                    try {
                        managerMethodToGetReader = managerClazz.getMethod(
                                CUSTOMIZATION_MANAGER_METHOD_GETREADER,
                                new Class<?>[] { String.class, int.class }
                        );
                        mReaderClazz = Class.forName(CUSTOMIZATION_READER_CLASSNAME);
                        mReaderInstance = managerMethodToGetReader.invoke(
                                managerInstance,
                                new Object[] { mName, mType }
                        );
                    } catch (Exception e) {
                        printStackTrace(e);
                    } catch (Error e) {
                        printStackTrace(e);
                    }
                }
                if ( managerMethodToGetReader == null ) {
                    return defaultReturnValue;
                }
            }

            try {
                Method readerMethod = mReaderClazz.getMethod(methodName, parameterTypes);
                return (T) readerMethod.invoke(mReaderInstance, parameterValues);
            } catch (Exception e) {
                printStackTrace(e);
            } catch (Error e) {
                printStackTrace(e);
            }

            return defaultReturnValue;
        }
    }

    /**
     * @hide
     */
    public static class SystemProperties {

        /**
         * @hide
         */
        public static String get(String key) {
            // return android.os.SystemProperties.get(key);
            return invokePublicStaticMethod(
                    "android.os.SystemProperties",
                    "get",
                    "",
                    new Class<?>[] { String.class },
                    new Object[] { key }
            );
        }

        /**
         * @hide
         */
        public static String get(String key, String def) {
            // return android.os.SystemProperties.get(key, def);
            return invokePublicStaticMethod(
                    "android.os.SystemProperties",
                    "get",
                    def,
                    new Class<?>[] { String.class, String.class },
                    new Object[] { key, def }
            );
        }

        /**
         * @hide
         */
        public static boolean getBoolean(String key, boolean def) {
            // return android.os.SystemProperties.getBoolean(key, def);
            return invokePublicStaticMethod(
                    "android.os.SystemProperties",
                    "getBoolean",
                    def,
                    new Class<?>[] { String.class, boolean.class },
                    new Object[] { key, def }
            );
        }

        /**
         * @hide
         */
        public static int getInt(String key, int def) {
            // return android.os.SystemProperties.getInt(key, def);
            return invokePublicStaticMethod(
                    "android.os.SystemProperties",
                    "getInt",
                    def,
                    new Class<?>[] { String.class, int.class },
                    new Object[] { key, def }
            );
        }

        /**
         * @hide
         */
        public static long getLong(String key, long def) {
            // return android.os.SystemProperties.getLong(key, def);
            return invokePublicStaticMethod(
                    "android.os.SystemProperties",
                    "getLong",
                    def,
                    new Class<?>[] { String.class, long.class },
                    new Object[] { key, def }
            );
        }
    }

    /**
     * @hide
     */
    public static class HtcBuildFlag {

        private static boolean sIsSet = false;
        private static boolean sIsDebug = false;

        /**
         * @hide
         */
        public static final boolean Htc_DEBUG_flag = getHtcDebugFlag();

        private static boolean getHtcDebugFlag() {
            if ( !sIsSet ) {
                // sIsDebug = com.htc.htcjavaflag.HtcBuildFlag.Htc_DEBUG_flag;
                sIsDebug = getPublicStaticField("com.htc.htcjavaflag.HtcBuildFlag", "Htc_DEBUG_flag", false);
                sIsSet = true;
            }
            return sIsDebug;
        }
    }

    /**
     * @hide
     */
    public static class SWLog {

        private static final boolean LOG_ENABLED = SystemWrapper.HtcBuildFlag.Htc_DEBUG_flag;

        /**
         * Send a VERBOSE log message.
         * 
         * @param tag Used to identify the source of a log message. It usually identifies
         *        the class or activity where the log call occurs.
         * @param msg The message you would like logged.
         * 
         * @return The number of bytes written.
         * 
         * @hide
         */
        public static int v(String tag, String msg) {
            if ( LOG_ENABLED ) {
                return android.util.Log.v(tag, msg);
            }
            return 0;
        }

        /**
         * Send a VERBOSE log message and log the exception.
         * 
         * @param tag Used to identify the source of a log message. It usually identifies
         *        the class or activity where the log call occurs.
         * @param msg The message you would like logged.
         * @param tr An exception to log
         * 
         * @return The number of bytes written.
         * 
         * @hide
         */
        public static int v(String tag, String msg, Throwable tr) {
            if ( LOG_ENABLED ) {
                return android.util.Log.v(tag, msg, tr);
            }
            return 0;
        }

        /**
         * Send a DEBUG log message.
         * 
         * @param tag Used to identify the source of a log message. It usually identifies
         *        the class or activity where the log call occurs.
         * @param msg The message you would like logged.
         * 
         * @return The number of bytes written.
         * 
         * @hide
         */
        public static int d(String tag, String msg) {
            if ( LOG_ENABLED ) {
                return android.util.Log.d(tag, msg);
            }
            return 0;
        }

        /**
         * Send a DEBUG log message and log the exception.
         * 
         * @param tag Used to identify the source of a log message. It usually identifies
         *        the class or activity where the log call occurs.
         * @param msg The message you would like logged.
         * @param tr An exception to log
         * 
         * @return The number of bytes written.
         * 
         * @hide
         */
        public static int d(String tag, String msg, Throwable tr) {
            if ( LOG_ENABLED ) {
                return android.util.Log.d(tag, msg, tr);
            }
            return 0;
        }

        /**
         * Send an INFO log message.
         * 
         * @param tag Used to identify the source of a log message. It usually identifies
         *        the class or activity where the log call occurs.
         * @param msg The message you would like logged.
         * 
         * @return The number of bytes written.
         * 
         * @hide
         */
        public static int i(String tag, String msg) {
            if ( LOG_ENABLED ) {
                return android.util.Log.i(tag, msg);
            }
            return 0;
        }

        /**
         * Send a INFO log message and log the exception.
         * 
         * @param tag Used to identify the source of a log message. It usually identifies
         *        the class or activity where the log call occurs.
         * @param msg The message you would like logged.
         * @param tr An exception to log
         * 
         * @return The number of bytes written.
         * 
         * @hide
         */
        public static int i(String tag, String msg, Throwable tr) {
            if ( LOG_ENABLED ) {
                return android.util.Log.i(tag, msg, tr);
            }
            return 0;
        }

        /**
         * Send a WARN log message.
         * 
         * @param tag Used to identify the source of a log message. It usually identifies
         *        the class or activity where the log call occurs.
         * @param msg The message you would like logged.
         * 
         * @return The number of bytes written.
         * 
         * @hide
         */
        public static int w(String tag, String msg) {
            if ( LOG_ENABLED ) {
                return android.util.Log.w(tag, msg);
            }
            return 0;
        }

        /**
         * Send a WARN log message and log the exception.
         * 
         * @param tag Used to identify the source of a log message. It usually identifies
         *        the class or activity where the log call occurs.
         * @param msg The message you would like logged.
         * @param tr An exception to log
         * 
         * @return The number of bytes written.
         * 
         * @hide
         */
        public static int w(String tag, String msg, Throwable tr) {
            if ( LOG_ENABLED ) {
                return android.util.Log.w(tag, msg, tr);
            }
            return 0;
        }

        /**
         * Send a WARN log message and log the exception.
         * 
         * @param tag Used to identify the source of a log message. It usually identifies
         *        the class or activity where the log call occurs.
         * @param tr An exception to log
         * 
         * @return The number of bytes written.
         * 
         * @hide
         */
        public static int w(String tag, Throwable tr) {
            if ( LOG_ENABLED ) {
                return android.util.Log.w(tag, tr);
            }
            return 0;
        }

        /**
         * Send an ERROR log message.
         * 
         * @param tag Used to identify the source of a log message. It usually identifies
         *        the class or activity where the log call occurs.
         * @param msg The message you would like logged.
         * 
         * @return The number of bytes written.
         * 
         * @hide
         */
        public static int e(String tag, String msg) {
            if ( LOG_ENABLED ) {
                return android.util.Log.e(tag, msg);
            }
            return 0;
        }

        /**
         * Send a ERROR log message and log the exception.
         * 
         * @param tag Used to identify the source of a log message. It usually identifies
         *        the class or activity where the log call occurs.
         * @param msg The message you would like logged.
         * @param tr An exception to log
         * 
         * @return The number of bytes written.
         * 
         * @hide
         */
        public static int e(String tag, String msg, Throwable tr) {
            if ( LOG_ENABLED ) {
                return android.util.Log.e(tag, msg, tr);
            }
            return 0;
        }
    }

    public static class HttpLinkConverter {

        public static String getAsciiLink(String originalLink) {
            if ( TextUtils.isEmpty(originalLink) ) {
                return null;
            }

            String mainUri = "";
            String miscParameter = "";
            String[] array = originalLink.split("\\?"); // split with "?"
            if ( array != null ) {
                int arrayLength = array.length;
                if ( arrayLength == 0 ) {
                    mainUri = originalLink;
                } else {
                    mainUri = array[0];
                    for ( int i = 1; i < arrayLength; i++ ) {
                        miscParameter += "?" + array[i];
                    }
                }
            }

            String decodedUri = null;
            try {
                decodedUri = URLDecoder.decode(mainUri, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                printStackTrace(e);
            }

            try {
                URL url = new URL(decodedUri);
                URI validUri = new URI(
                        url.getProtocol(),
                        url.getUserInfo(),
                        IDN.toASCII(url.getHost()),
                        url.getPort(),
                        url.getPath(),
                        null,
                        null
                );
                return validUri.toASCIIString() + miscParameter;
            } catch (Exception e) {
                printStackTrace(e);
            }

            return null;
        }
    }

    private static void printStackTrace(Throwable t) {
        if ( INTERNAL_DEBUG_FLAG ) {
            t.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> T getPublicStaticField(String fullClassName, String fieldName, T defaultFieldValue) {
        if ( fullClassName == null || fieldName == null ) {
            return defaultFieldValue;
        }
        try {
            Class<?> clazz = Class.forName(fullClassName);
            Field field = clazz.getField(fieldName);
            return (T) field.get(null);
        } catch (Exception e) {
            printStackTrace(e);
        } catch (Error e) {
            printStackTrace(e);
        }
        return defaultFieldValue;
    }

    @SuppressWarnings("unchecked")
    public static <T> T invokePublicStaticMethod(String fullClassName, String methodName,
            T defaultReturnValue, Class<?>[] parameterTypes, Object[] parameterValues) {
        if ( fullClassName == null || methodName == null ) {
            return defaultReturnValue;
        }
        try {
            Class<?> clazz = Class.forName(fullClassName);
            Method method = clazz.getMethod(methodName, parameterTypes);
            return (T) method.invoke(null, parameterValues);
        } catch (Exception e) {
            printStackTrace(e);
        } catch (Error e) {
            printStackTrace(e);
        }
        return defaultReturnValue;
    }

    public static String getCacheManagerAuthority() {
        if ( sCacheManagerAuthority == null ) {
            sCacheManagerAuthority = DEFAULT_CACHEMANAGER_AUTHORITY;
        }
        return sCacheManagerAuthority;
    }

    public static void setCacheManagerAuthority(String authority) {
        sCacheManagerAuthority = authority;
    }

    public static String getPluginManagerAuthority() {
        if ( sPluginManagerAuthority == null ) {
            sPluginManagerAuthority = DEFAULT_PLUGINMANAGER_AUTHORITY;
        }
        return sPluginManagerAuthority;
    }

    public static void setPluginManagerAuthority(String authority) {
        sPluginManagerAuthority = authority;
    }

    public static String getSocialManagerAuthority() {
        if ( sSocialManagerAuthority == null ) {
            sSocialManagerAuthority = DEFAULT_SOCIALMANAGER_AUTHORITY;
        }
        return sSocialManagerAuthority;
    }

    public static void setSocialManagerAuthority(String authority) {
        sSocialManagerAuthority = authority;
    }

    public static String getSocialManagerPackageName() {
        if ( sSocialManagerPackageName == null ) {
            sSocialManagerPackageName = DEFAULT_SOCIALMANAGER_PACKAGE_NAME;
        }
        return sSocialManagerPackageName;
    }

    public static String getPluginManagerPackageName() {
        if ( sPluginManagerPackageName == null ) {
            sPluginManagerPackageName = DEFAULT_PLUGINMANAGER_PACKAGE_NAME;
        }
        return sPluginManagerPackageName;
    }

    public static void setSocialManagerPackageName(String packageName) {
        sSocialManagerPackageName = packageName;
    }

    public static String getHspPackageName() {
        if ( sHspPackageName == null ) {
            sHspPackageName = DEFAULT_HSP_PACKAGE_NAME;
        }
        return sHspPackageName;
    }

    public static void setHspPackageName(String packageName) {
        sHspPackageName = packageName;
    }

    public static String getHdkApiPrefix() {
        if ( sHdkApiPrefix == null ) {
            sHdkApiPrefix = DEFAULT_HDK_API_PREFIX;
        }
        return sHdkApiPrefix;
    }

    public static void setHdkApiPrefix(String hdkApiPrefix) {
        sHdkApiPrefix = hdkApiPrefix;
    }

    public static String getHspApiPrefix() {
        if ( sHspApiPrefix == null ) {
            sHspApiPrefix = DEFAULT_HSP_API_PREFIX;
        }
        return sHspApiPrefix;
    }

    public static void setHspApiPrefix(String hspApiPrefix) {
        sHspApiPrefix = hspApiPrefix;
    }

    public static boolean getIgnoreHdkSupportCheck() {
        return sIgnoreHdkSupportCheck;
    }

    public static void setIgnoreHdkSupportCheck(boolean ignoreCheck) {
        sIgnoreHdkSupportCheck = ignoreCheck;
    }
}
