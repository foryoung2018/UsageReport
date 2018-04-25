package com.htc.lib1.theme;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

import com.htc.lib0.htcdebugflag.HtcWrapHtcDebugFlag;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * A class for Theme feature to store info in content provider depending on htc/non-htc case
 *
 * It has static cache mechanism to prevent poor performance from frequently query.
 * To work correctly, please register the content observer in order to get benefit from cache mechanism.
 * Otherwise, getString with parameter 'forceGet == true' to guarantee correctness of value
 * if the user don't want to register the observer via ThemeSettingUtil.registerContentObserver.
 *
 **/

public class ThemeSettingUtil {
    private static String LOG_TAG = "ThemeSettingUtil";
    private static boolean securedLOGD = HtcWrapHtcDebugFlag.Htc_SECURITY_DEBUG_flag;
    public static boolean DEBUG = BuildConfig.DEBUG || securedLOGD || Config.DEBUG_LOG || HtcWrapHtcDebugFlag.Htc_DEBUG_flag;

    /**
     * A helper class to debug
     **/
    public static class Config {
        private final static String PROPERTY_THEME_DEBUG = "thm_debug_log";
        private final static String PROPERTY_THEME_NON_HTC = "thm_debug_non_htc";
        private final static String PROPERTY_THEME_HTC = "thm_debug_htc";

        /**
         * Dynamically turn ON/OFF property with command:
         * # adb shell setprop log.tag.PROPERTY_NAME [VERBOSE|SUPPRESS]
         */
        private static boolean isPropertyEnabled(String propertyName) {
            return Log.isLoggable(propertyName, Log.VERBOSE);
        }

        //ex: adb shell setprop log.tag.thm_debug_log VERBOSE
        public final static boolean DEBUG_LOG = isPropertyEnabled(PROPERTY_THEME_DEBUG);
        //ex: adb shell setprop log.tag.thm_debug_non_htc VERBOSE
        public final static boolean DEBUG_NONHTC = isPropertyEnabled(PROPERTY_THEME_NON_HTC);
        //ex: adb shell setprop log.tag.thm_debug_non_htc VERBOSE
        public final static boolean DEBUG_HTC = isPropertyEnabled(PROPERTY_THEME_HTC);
    }

    /**
     * A helper method to dump d log
     * */
    public static void logd(String tag, String format, Object... args) {
        if(DEBUG) {
            android.util.Log.d(tag, String.format(format, args));
        }
    }

    /**
     * A helper method to dump w log
     * */
    public static void logw(String tag, String format, Object... args) {
        android.util.Log.w(tag, String.format(format, args));
    }


    /**
     * A helper method to getString for ThemeSettingProvider or SettingProvider.Depends on htc or nonhtc if statement.
     *
     * @param context context
     * @param name the key used to query value
     * */
    public static String getString(Context context, String name) {
        return getString(context, name, false);
    }

    /**
     * A helper method to getString for ThemeSettingProvider or SettingProvider.Depends on htc or nonhtc if statement.
     *
     * @param context context
     * @param name the key used to query value
     * @param forceGet force to get value from ThemeSettingProvider instead of cache.
     * */
    public static String getString(Context context, String name, boolean forceGet) {
        ContentResolver resolver = context.getContentResolver();
        if (isNonHtc(context)) {
            return ThemeSettings.System.getString(resolver, name, forceGet);
        } else {
            return android.provider.Settings.System.getString(resolver, name);
        }
    }

    /**
     * A helper method to getStringForUser for ThemeSettingProvider or SettingProvider.Depends on htc or nonhtc if statement.
     *
     * Because ThemeSettingProvider can not be singleuser, it cannot handle cross user query case.
     *
     * For example, Systemui with user 0 (Systemui is multiple user but always show user 0) will fail to query the setting of other current activie user.
     * But it's ok, because we don't have such kinds of application in nonhtc case.
     *
     * @param context context
     * @param name the key used to query value
     * @param userHandle the user id
     * */
    public static String getStringForUser(Context context, String name, int userHandle) {
        String srcValue = null;
        try {
            srcValue = getStringForUser(context, name, userHandle, false);
        } catch (Exception ex) {
            Log.w(LOG_TAG, "Exception occurs. fallback");
        }
        return srcValue;
    }

    /**
     * A helper method to getStringForUser for ThemeSettingProvider or SettingProvider.Depends on htc or nonhtc if statement.
     *
     * Because ThemeSettingProvider can not be singleuser, it cannot handle cross user query case.
     *
     * For example, Systemui with user 0 (Systemui is multiple user but always show user 0) will fail to query the setting of other current activie user.
     * But it's ok, because we don't have such kinds of application in nonhtc case.
     *
     * @param context context
     * @param name the key used to query value
     * @param userHandle the user id
     * @param forceGet force to get value from ThemeSettingProvider instead of cache.
     * */
    public static String getStringForUser(Context context, String name,
                                          int userHandle, boolean forceGet) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        ContentResolver resolver = context.getContentResolver();
        if (isNonHtc(context)) {
//            return ThemeSettings.System.getStringForUser(resolver, name, userHandle);
            return ThemeSettings.System.getString(resolver, name, forceGet);
        } else {
            return MethodCompat.getStringForUser(resolver, name, userHandle);
        }
    }

    /**
     * A helper method to putString for ThemeSettingProvider or SettingProvider.Depends on htc or nonhtc if statement.
     *
     * @param context context
     * @param name the key to of the valuse.
     * @param value the value to saved in provider.
     * */
    public static void putString(Context context, String name, String value) {
        ContentResolver resolver = context.getContentResolver();
        if (isNonHtc(context)) {
            ThemeSettings.System.putString(resolver, name, value);
        } else {
            android.provider.Settings.System.putString(resolver, name, value);
        }
    }

    /**
     * A helper method to putStringForUser for ThemeSettingProvider or SettingProvider.Depends on htc or nonhtc if statement.
     *
     * Because ThemeSettingProvider can not be singleuser, it cannot handle cross user query case.
     *
     * For example, Systemui with user 0 (Systemui is multiple user but always show user 0) will fail to query the setting of other current activie user.
     * But it's ok, because we don't have such kinds of application in nonhtc case.
     *
     * @param context context
     * @param name the key to of the valuse.
     * @param value the value to saved in provider.
     * @param userHandle the user id
     * */
    public static void putStringForUser(Context context, String name, String value, int userHandle) {
        ContentResolver resolver = context.getContentResolver();
        if (isNonHtc(context)) {
//            ThemeSettings.System.putString(resolver, name, value, userHandle);
            ThemeSettings.System.putString(resolver, name, value);
        } else {
            MethodCompat.putStringForUser(resolver, name, value, userHandle);
        }
    }

    /**
     * A helper method to distinguish htc/non-htc case.
     *
     * non-htc = !ODM && !HEP
     *
     * Please refer to the hic ticker:
     * https://hichub.htc.com/HomeLauncher/Prism/issues/10
     *
     * @param context context
     *
     **/
    private static boolean sIsHtcCompatibleDevice = false;
    private static boolean sInitHtcCompatibleDevice = false;

    public static boolean isNonHtc(Context context) {
        if (!sInitHtcCompatibleDevice) {
            sIsHtcCompatibleDevice = !Config.DEBUG_NONHTC && (Config.DEBUG_HTC || com.htc.lib0.HDKLib0Util.isODMDevice(context) || com.htc.lib0.HDKLib0Util.isHEPDevice(context));
            sInitHtcCompatibleDevice = true;
        }
        return !sIsHtcCompatibleDevice;
    }

    /**
     * A helper class to use reflection of hidden apis
     **/
    public static class MethodCompat {
        private static Method sGetStringForUser;
        private static Method sPutStringForUser;

        public static String getStringForUser(ContentResolver resolver, String name, int userHandle) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
            String retStr = null;
            if (sGetStringForUser == null) {
                Class<?> clz = android.provider.Settings.System.class;
                sGetStringForUser = clz.getDeclaredMethod("getStringForUser", ContentResolver.class, String.class, int.class);
            }
            if (sGetStringForUser == null) return null;

            Object retVal = sGetStringForUser.invoke(null, resolver, name, userHandle);
            if (retVal != null) retStr = retVal.toString();
            return retStr;
        }

        public static boolean putStringForUser(ContentResolver resolver, String name, String value, int userHandle) {
            boolean ret = false;
            try {
                if (sPutStringForUser == null) {
                    Class<?> clz = android.provider.Settings.System.class;
                    sPutStringForUser = clz.getDeclaredMethod("putStringForUser", ContentResolver.class, String.class,  String.class, int.class);
                }
                if (sPutStringForUser == null) return false;

                Object retVal = sPutStringForUser.invoke(null, resolver, name, value, userHandle);
                if (retVal != null) ret = (Boolean) retVal;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return ret;
        }

        public static void registerContentObserverForUser(Context context, Uri uri, boolean forDescendent, ContentObserver observer, int userHandle) {
            ThemeSettingUtil.logd(LOG_TAG, "[registerContentObserverForUser] context=" + context + ", uri=" + uri + ", observer=" + observer + ", userHandle=" + userHandle);
            try {
                Class<?> clz = context.getContentResolver().getClass();
                Method method = clz.getMethod("registerContentObserver", Uri.class, boolean.class, ContentObserver.class, int.class);
                method.invoke(context.getContentResolver(), uri, forDescendent, observer, userHandle);
            } catch (Exception ex) {
                Log.w(LOG_TAG, "[registerContentObserverForUser] Error while registering content observer: ", ex);
            }
        }
    }


    /**
     * A helper method to registerContentObserver for ThemeSettingProvider or SettingProvider.Depends on htc or nonhtc if statement.
     *
     *
     * @param context context
     * @param name the key to observe.
     * @param observer the observer to notify
     * */
    public static void registerContentObserver(Context context, String name, ContentObserver observer) {
        Uri uri = null;
        if (isNonHtc(context)) {
            try {
                uri = ThemeSettings.System.getUriFor(name);
                addThemeSettingsObserver(uri, observer);
                context.getContentResolver().registerContentObserver(uri, true, lazyGetThemeSettingsObserver(context));
            } catch (Exception ex) {
                ThemeSettingUtil.logd(LOG_TAG, "[registerContentObserver] " + ex.getMessage());
            }
        } else {
            uri = android.provider.Settings.System.getUriFor(name);
            context.getContentResolver().registerContentObserver(uri, true, observer);
        }
    }

    /**
     * A helper method to registerContentObserverForUser for ThemeSettingProvider or SettingProvider.Depends on htc or nonhtc if statement.
     *
     * Because ThemeSettingProvider can not be singleuser, it cannot handle cross user query case.
     *
     * @param context context
     * @param name the key to observe.
     * @param forDescendent When false, the observer will be notified whenever a
     * change occurs to the exact URI specified by <code>uri</code> or to one of the
     * URI's ancestors in the path hierarchy.  When true, the observer will also be notified
     * whenever a change occurs to the URI's descendants in the path hierarchy.
     * @param observer The object that receives callbacks when changes occur
     * @param userHandle the id of user.
     * */
    public static void registerContentObserverForUser(Context context, String name, boolean forDescendent, ContentObserver observer, int userHandle) {
        ThemeSettingUtil.logd(LOG_TAG, "[registerContentObserverForUser] context=" + context + ", name=" + name + ", observer=" + observer + ", userHandle=" + userHandle);
        Uri uri = null;
        if (isNonHtc(context)) {
            try {
                uri = ThemeSettings.System.getUriFor(name);
                addThemeSettingsObserver(uri, observer);
//            MethodCompat.registerContentObserverForUser(context, uri, forDescendent, lazyGetThemeSettingsObserver(context), userHandle);
                context.getContentResolver().registerContentObserver(uri, true, lazyGetThemeSettingsObserver(context));
            } catch (Exception ex) {
                ThemeSettingUtil.logd(LOG_TAG, "[registerContentObserverForUser] " + ex.getMessage());
            }
        } else {
            uri = android.provider.Settings.System.getUriFor(name);
            MethodCompat.registerContentObserverForUser(context, uri, forDescendent, observer, userHandle);
        }
    }

    /**
     * A helper method to unregisterContentObserver for ThemeSettingProvider or SettingProvider.Depends on htc or nonhtc if statement.
     *
     *
     * @param context context
     * @param name the key to observe.
     * @param observer The object that receives callbacks when changes occur
     * */
    public static void unregisterContentObserver(Context context, String name, ContentObserver observer) {
        Uri uri = null;
        if (isNonHtc(context)) {
            try {
                uri = ThemeSettings.System.getUriFor(name);
                removeThemeSettingsObserver(uri, observer);
                if (THEME_SETTINGS_OBSERVERS != null && THEME_SETTINGS_OBSERVERS.isEmpty())
                    context.getContentResolver().unregisterContentObserver(lazyGetThemeSettingsObserver(context));
            } catch (Exception ex) {
                ThemeSettingUtil.logd(LOG_TAG, "[unregisterContentObserver] " + ex.getMessage());
            }
        } else {
            context.getContentResolver().unregisterContentObserver(observer);
        }
    }

    /**
     * A helper method to unregisterContentObserverForUser for ThemeSettingProvider or SettingProvider.Depends on htc or nonhtc if statement.
     *
     * Not implemented currently
     * @param context context
     * @param name the key to observe.
     * @param forDescendent When false, the observer will be notified whenever a
     * change occurs to the exact URI specified by <code>uri</code> or to one of the
     * URI's ancestors in the path hierarchy.  When true, the observer will also be notified
     * whenever a change occurs to the URI's descendants in the path hierarchy.
     * @param observer The object that receives callbacks when changes occur
     * @param userHandle the id of user.
     * */
    public static void unregisterContentObserverForUser(Context context, String name, boolean forDescendent, ContentObserver observer, int userHandle) {
    }

    public static Uri getUriFor(Context context, String name) {
        Uri uri = null;
        if (isNonHtc(context)) {
            uri = ThemeSettings.System.getUriFor(name);
        } else {
            uri = android.provider.Settings.System.getUriFor(name);
        }
        return uri;
    }

    /**
     * A helper method to get theme type from the uri.
     */
    static String getThemeTypeFromUri(Uri uri) {
        if (uri == null) {
            ThemeSettingUtil.logd(LOG_TAG, "Theme type uri is null");
            return null;
        }

        String themeType = uri.getLastPathSegment();
        if (TextUtils.isEmpty(themeType)) {
            ThemeSettingUtil.logd(LOG_TAG, "Invalid theme type = %s", themeType);
            return null;
        }

        return themeType;
    }

    /**
     * A helper method to get nameValueSpace from the uri. This is for management of cache.
     */
    static String getThemeSettingNameValueSpaceUri(Uri uri) {
        if (uri == null) {
            ThemeSettingUtil.logd(LOG_TAG, "getThemeSettingNameValueSpaceUri uri is null");
            return null;
        }

        List<String> segments = uri.getPathSegments();
        if (segments.size() < 2) {
            ThemeSettingUtil.logd(LOG_TAG, "getThemeSettingNameValueSpaceUri uri fail %s", uri);
            return null;
        }
        return segments.get(segments.size() - 2);
    }

    /**
     *
     * To implements static cache of ThemeSettings
     *
     * The cache mechanism relies on the observer. If the user don't register observer, please use getString with forceGet == true to get instant value.
     * */
    private static HashMap<Uri, List<ContentObserver>> THEME_SETTINGS_OBSERVERS;
    static ThemeSettingsObserver sThemeSettingsObserver = null;

    static class ThemeSettingsObserver extends ContentObserver {
        public ThemeSettingsObserver(Handler handler) {
            super(handler);
        }

        public void onChange(boolean selfChange, Uri uri) {
            String themeType = ThemeSettingUtil.getThemeTypeFromUri(uri);
            String nameValueSpace = getThemeSettingNameValueSpaceUri(uri);
            ThemeSettingUtil.logd(LOG_TAG, "ThemeSettingsObserver onChange %s=%s", nameValueSpace, themeType);
            if (themeType == null || nameValueSpace == null)
                return;

            ThemeSettings.System.removeValueCache(nameValueSpace, themeType);
            if (THEME_SETTINGS_OBSERVERS == null)
                return;

            List<ContentObserver> observers = THEME_SETTINGS_OBSERVERS.get(uri);
            if (observers != null) {
                for (ContentObserver observer : observers) {
                    observer.onChange(selfChange, uri);
                }
            }
        }
    }

    static ContentObserver lazyGetThemeSettingsObserver(Context context) {
        if (sThemeSettingsObserver == null)
            sThemeSettingsObserver = new ThemeSettingsObserver(new Handler(context.getMainLooper()));
        return sThemeSettingsObserver;
    }

    static void addThemeSettingsObserver(Uri uri, ContentObserver observer) {
        if (observer == null)
            return;
        
        if (THEME_SETTINGS_OBSERVERS == null) {
            THEME_SETTINGS_OBSERVERS = new HashMap<Uri, List<ContentObserver>>();
        }

        List<ContentObserver> observers = THEME_SETTINGS_OBSERVERS.get(uri);

        if (observers == null) {
            observers = new ArrayList<ContentObserver>();
            THEME_SETTINGS_OBSERVERS.put(uri, observers);
        }

        if (!observers.contains(observer)) {
            observers.add(observer);
        }

    }

    static void removeThemeSettingsObserver(Uri uri, ContentObserver observer) {
        if (THEME_SETTINGS_OBSERVERS == null)
            return;

        List<ContentObserver> observers = THEME_SETTINGS_OBSERVERS.get(uri);
        if (observers != null && observers.contains(observer)) {
            observers.remove(observer);
            if (observers.isEmpty()) {
                THEME_SETTINGS_OBSERVERS.remove(uri);
            }
        }
    }
}
