package com.htc.lib2.configuration;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Binder;
import android.provider.Settings;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.htc.lib0.HDKLib0Util;
import com.htc.lib0.HDKLib0Util.HDKException;

/**
 * This class describes all information of HTC Configuration that can
 * impact the resources the application retrieves. (such as simple mode)
 */
public class HtcWrapConfiguration {
    private static final String CLASS_NAME = "android.content.res.HtcConfiguration";
    private static final String HTC_PERMISSION_APP_DEFAULT = "com.htc.permission.APP_DEFAULT";
    private static final String STRING_HTC_SIMPLE_MODE = "htc_simple_mode";
    private static final String STRING_HTC_FONT_SCALE = "htc_font_scale";

    /**
     * Broadcast Action: Sent after the current HTC configuration has changed.
     */
    public static final String ACTION_HTC_CONFIG_CHANGED;

    /**
     * Used as a boolean extra field with {@link #ACTION_HTC_CONFIG_CHANGED}
     * to indicate the Simple Mode enabled or not.
     */
    public static final String EXTRA_CONFIG_SIMPLE;

    /**
     * Set the category if Simple Mode change should be considered.
     */
    public static final String CATEGORY_CONFIG_SIMPLE;


    /**
     * HDK level = 1 supported mode.
     */
    public static final int HTC_SIMPLE_MODE = 1;

    /**
     * Used as a float extra field with {@link #ACTION_HTC_CONFIG_CHANGED}
     * to indicate the HTC Fontscale size.
     */
    public static final String EXTRA_CONFIG_FONTSCALE;

    /**
     * Set the category if HTC Fontscale change should be considered.
     */
    public static final String CATEGORY_CONFIG_FONTSCALE;

    /**
     * HDK level = 1 methods.
     */
    private static Method s_isSimpleMode = null, s_setSimpleMode = null;
    private static Method s_applyHtcFontscale = null, s_checkHtcFontscaleChanged = null, s_checkHtcFontscaleChanged2 = null;
    private static Method s_getHtcFontscale = null, s_getHtcFontscale2 = null, s_setHtcFontscale = null;

    static {
        String strActionHtcConfigChange = "", strExtraConfigSimple = "",
                strCategoryConfigSimple = "", strExtraConfigFontscale = "",
                strCategoryConfigFontscale = "";

        try {
            if (HDKLib0Util.getHDKBaseVersion() > 0) {
                // Load Class
                final Class className = Class.forName(CLASS_NAME);
                // Load Methods
                s_isSimpleMode = className.getMethod("isSimpleMode");
                s_setSimpleMode = className.getMethod("setSimpleMode", new Class[] {Context.class, boolean.class});
                s_applyHtcFontscale = className.getMethod("applyHtcFontscale", new Class[] {Context.class});
                s_checkHtcFontscaleChanged = className.getMethod("checkHtcFontscaleChanged", new Class[] {Context.class, boolean.class});
                s_checkHtcFontscaleChanged2 = className.getMethod("checkHtcFontscaleChanged", new Class[] {Context.class, float.class});
                s_getHtcFontscale = className.getMethod("getHtcFontscale");
                s_getHtcFontscale2 = className.getMethod("getHtcFontscale", new Class[] {Context.class});
                s_setHtcFontscale = className.getMethod("setHtcFontscale", new Class[] {Context.class, float.class, float.class});
                // Load Intent info
                strActionHtcConfigChange = (String) className.getDeclaredField("ACTION_HTC_CONFIG_CHANGED").get(className);
                strExtraConfigSimple = (String) className.getDeclaredField("EXTRA_CONFIG_SIMPLE").get(className);
                strCategoryConfigSimple = (String) className.getDeclaredField("CATEGORY_CONFIG_SIMPLE").get(className);
                strExtraConfigFontscale = (String) className.getDeclaredField("EXTRA_CONFIG_FONTSCALE").get(className);
                strCategoryConfigFontscale = (String) className.getDeclaredField("CATEGORY_CONFIG_FONTSCALE").get(className);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        ACTION_HTC_CONFIG_CHANGED = strActionHtcConfigChange != "" ?
                strActionHtcConfigChange : "com.htc.intent.action.CONFIGURATION_CHANGED";
        EXTRA_CONFIG_SIMPLE = strExtraConfigSimple != "" ?
                strExtraConfigSimple : "config.extra.SIMPLE_MODE";
        CATEGORY_CONFIG_SIMPLE = strCategoryConfigSimple != "" ?
                strCategoryConfigSimple : "com.htc.intent.category.SIMPLE_MODE";
        EXTRA_CONFIG_FONTSCALE = "".equals(strExtraConfigFontscale) ?
                "config.extra.FONTSCALE" : strExtraConfigFontscale;
        CATEGORY_CONFIG_FONTSCALE = "".equals(strCategoryConfigFontscale) ?
                "com.htc.intent.category.FONTSCALE" : strCategoryConfigFontscale;
    }

    /**
     * To enable or disable HTC Modes, such as {@link #HTC_SIMPLE_MODE}.
     * It will send broadcast {@link #ACTION_HTC_CONFIG_CHANGED} with category
     * {@link #CATEGORY_CONFIG_SIMPLE} if Simple Mode has changed, The intent
     * contains an extra {@link #EXTRA_CONFIG_SIMPLE} with the currently Simple Mode state.
     *
     * Caller must have permission "com.htc.permission.APP_DEFAULT" and used System UID.
     *
     * @param context the context used to send broadcast if Simple Mode has changed.
     * @param mode one of HTC Modes, {@link #HTC_SIMPLE_MODE}.
     * @param enable true to enable the mode, false to disable.
     *
     * @return true if set successfully, false otherwise.
     *
     * @throws SecurityException throws SecurityException if
     *     the caller does not hold the "com.htc.permission.APP_DEFAULT" permission.
     * @throws IllegalArgumentException throws IllegalArgumentException if the mode is not supported.
     * @throws HDKException throws HDKException if HDK invoke failed.
     */
    public static boolean setMode(Context context, int mode, boolean enable) throws HDKException {
        boolean success = false;
        if (context == null) return false;
        final ContentResolver resolver = context.getContentResolver();
        switch (mode) {
            case HTC_SIMPLE_MODE:
                boolean org_enabled = getMode(context, mode);
                if (enable != org_enabled) {
                    success = Settings.System.putInt(resolver, STRING_HTC_SIMPLE_MODE, enable ? 1 : 0);
                    if (success) {
                        //send ACTION_HTC_CONFIG_CHANGED.
                        Intent intent = new Intent(ACTION_HTC_CONFIG_CHANGED);
                        intent.addCategory(CATEGORY_CONFIG_SIMPLE);
                        intent.putExtra(EXTRA_CONFIG_SIMPLE, enable);
                        context.sendBroadcast(intent, HTC_PERMISSION_APP_DEFAULT);
                        android.util.Log.d("HtcWrapConfiguration", "setSimpleMode to " + enable + " from pid=" + Binder.getCallingPid());
                    }
                }
                break;
            default:
                throw new IllegalArgumentException("The mode is not supported:" + mode);
        }
        return success;
    }

    /**
     * Check Simple Mode has been set or not.
     *
     * @return true if Simple Mode has been set, false otherwise.
     *
     * @throws HDKException throws HDKException if HDK invoke failed.
     *
     * @deprecated [use getMode(Context context, int mode) instead]
     */
    @Deprecated
    public static boolean isSimpleMode() throws HDKException {
        boolean enabled = false;
        if (HDKLib0Util.getHDKBaseVersion() > 0) {
            try {
                if (s_isSimpleMode == null) {
                    throw new HDKLib0Util.HDKException();
                }
                enabled = (Boolean) s_isSimpleMode.invoke(null);
            } catch (InvocationTargetException e) {
                throw new HDKLib0Util.HDKException("Method Invoke failed with InvocationTargetException");
            } catch (IllegalAccessException e) {
                throw new HDKLib0Util.HDKException("Method Invoke failed with IllegalAccessException");
            }
        }
        return enabled;
    }

    /**
     * Check Mode has been set or not.
     *
     * @param context the context used to get ContentResolver.
     * @param mode one of HTC Modes, {@link #HTC_SIMPLE_MODE}.
     * @return true if Mode has been set, false otherwise.
     *
     * @throws HDKException throws HDKException if HDK invoke failed.
     */
    public static boolean getMode(Context context, int mode) throws HDKException {
        boolean enabled = false;
        if (HDKLib0Util.getHDKBaseVersion() > 0) {
            if (context == null) return false;
            final ContentResolver resolver = context.getContentResolver();
            switch (mode) {
                case HTC_SIMPLE_MODE: {
                    int result = Settings.System.getInt(resolver, STRING_HTC_SIMPLE_MODE, -1);
                    if(result != -1) {
                        enabled = (result == 1);
                    } else {
                        if (s_isSimpleMode == null) {
                            throw new HDKLib0Util.HDKException();
                        }
                        try {
                            enabled = (Boolean) s_isSimpleMode.invoke(null);
                        } catch (InvocationTargetException e) {
                            throw new HDKLib0Util.HDKException("Method Invoke failed with InvocationTargetException");
                        } catch (IllegalAccessException e) {
                            throw new HDKLib0Util.HDKException("Method Invoke failed with IllegalAccessException");
                        }
                    }
                } break;
                default: {
                    throw new IllegalArgumentException("The mode is not supported:" + mode);
                }
            }
        }
        else {
            if (context == null) return false;
            final ContentResolver resolver = context.getContentResolver();
            switch (mode) {
                case HTC_SIMPLE_MODE:
                    int result = Settings.System.getInt(resolver, STRING_HTC_SIMPLE_MODE, 0);
                    enabled = (result == 1);
                    break;
                default:
                    throw new IllegalArgumentException("The mode is not supported:" + mode);
            }
        }
        return enabled;
    }

    /**
     * get Htc Fontscale
     *
     * @param context the context used to get ContentResolver.
     *
     * @return Float value of htc FontScale
     *
     * @throws RuntimeException throws RuntimeException if HDK invoke failed.
     */
    public static float getHtcFontscale(Context context) throws RuntimeException {
        float value = 0.0f;
        if (HDKLib0Util.getHDKBaseVersion() > 0) {
            if (context == null) return value;
            final ContentResolver resolver = context.getContentResolver();
            value = Settings.System.getFloat(resolver, STRING_HTC_FONT_SCALE, -1.0f);
            if (value == -1.0f) {
                try {
                    if (s_getHtcFontscale2 == null) {
                        if (s_getHtcFontscale == null) {
                            throw new RuntimeException();
                        }
                        value = (Float) s_getHtcFontscale.invoke(null);
                    } else {
                        final Object[] paramObjs = {context};
                        value = (Float) s_getHtcFontscale2.invoke(null, paramObjs);
                    }
                } catch (InvocationTargetException e) {
                    throw new RuntimeException("Method Invoke failed with InvocationTargetException");
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Method Invoke failed with IllegalAccessException");
                }
            }
        }
        else {
            if (context == null) return value;
            final ContentResolver resolver = context.getContentResolver();
            value = Settings.System.getFloat(resolver, STRING_HTC_FONT_SCALE, 0.0f);
        }
        return value;
    }

    /**
     * Apply HTC specific Fontscale Configuration
     *
     * Caller must have permission "com.htc.permission.APP_DEFAULT" and used System UID.
     *
     * @param context the context which want to apply HTC specific fontscale size configuration.
     *
     * @return true if HTC specific fontscale size applied successfully, false otherwise.
     *
     * @throws RuntimeException throws RuntimeException if HDK invoke failed.
     */
    public static boolean applyHtcFontscale(Context context) throws RuntimeException {
        boolean success = false;
        if (context == null) return false;
        Resources res = context.getResources();
        Configuration conf = res.getConfiguration();
        float htcfontscale = getHtcFontscale(context);
        if (htcfontscale != 0.0f && htcfontscale != conf.fontScale) {
            conf.fontScale = htcfontscale;
            res.updateConfiguration(conf, null);
            success = true;
        }
        return success;
    }

    /**
     * Check if HTC specific Fontscale Configuration should be applied?
     *
     * Caller must have permission "com.htc.permission.APP_DEFAULT" and used System UID.
     *
     * @param context the context which want to check if HTC specific fontscale size configuration should be applied.
     *
     * @param applied if the activity already applied the htc fontscale size.
     *
     * @return true if HTC new specific fontscale size should be applied, false otherwise.
     *
     * @throws RuntimeException throws RuntimeException if HDK invoke failed.
     *
     * @deprecated [use checkHtcFontscaleChanged(Context context, float fontScale) instead]
     */
    @Deprecated
    public static boolean checkHtcFontscaleChanged(Context context, boolean applied) throws RuntimeException {
        boolean success = false;
        if (HDKLib0Util.getHDKBaseVersion() > 0) {
            try {
                if (s_checkHtcFontscaleChanged == null) {
                    throw new RuntimeException();
                }
                final Object[] paramObjs = {context, applied};
                success = (Boolean) s_checkHtcFontscaleChanged.invoke(null, paramObjs);
            } catch (InvocationTargetException e) {
                throw new RuntimeException("Method Invoke failed with InvocationTargetException");
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Method Invoke failed with IllegalAccessException");
            }
        }
        return success;
    }

    /**
     * Check if HTC specific Fontscale Configuration should be applied?
     *
     * Caller must have permission "com.htc.permission.APP_DEFAULT" and used System UID.
     *
     * @param context the context which want to check if HTC specific fontscale size configuration should be applied.
     *
     * @param fontScale the fontScale applied last time
     *
     * @return true if HTC new specific fontscale size should be applied, false otherwise.
     *
     * @throws RuntimeException throws RuntimeException if HDK invoke failed.
     */
    public static boolean checkHtcFontscaleChanged(Context context, float fontScale) throws RuntimeException {
        boolean success = false;
        if (context == null) return false;
        float htcfontscale = getHtcFontscale(context);
        if (htcfontscale != 0.0f && htcfontscale != fontScale) {
            success = true;
        }
        return success;
    }

    /**
     * Set HTC specific Fontscale size
     *
     * Caller must have permission "com.htc.permission.APP_DEFAULT" and used System UID.
     *
     * @param context the context used to send broadcast if HTC specific fontscale size was changed.
     *
     * @param fontscale the HTC specific fontscale size.
     *
     * @param applyfontscale the fontscale size application should apply it.
     *
     * @return true if set HTC specific fontscale size successfully, false otherwise.
     *
     * @throws RuntimeException throws RuntimeException if HDK invoke failed.
     */
    public static boolean setHtcFontscale(Context context, float fontscale, float applyfontscale) throws RuntimeException {
        boolean success = false;
        if (context == null) return false;
        float org_fontscale = getHtcFontscale(context);

        if (org_fontscale != fontscale) {
            final ContentResolver resolver = context.getContentResolver();
            boolean result = Settings.System.putFloat(resolver, STRING_HTC_FONT_SCALE, fontscale);

            if (result && fontscale != 0.0f) {
                //send ACTION_HTC_CONFIG_CHANGED.
                Intent intent = new Intent(ACTION_HTC_CONFIG_CHANGED);
                intent.addCategory(CATEGORY_CONFIG_FONTSCALE);
                intent.putExtra(EXTRA_CONFIG_FONTSCALE, Float.valueOf(applyfontscale).toString());
                context.sendBroadcast(intent);
                android.util.Log.d("HtcWrapConfiguration", "setHtcFontscale to " + fontscale + "/" + applyfontscale + " from pid=" + Binder.getCallingPid());
                success = true;
            }
        }
        return success;
    }
}
