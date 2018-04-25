package com.htc.lib1.cc.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import android.content.Context;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;

import com.htc.lib1.cc.R;
import com.htc.lib1.cc.htcjavaflag.HtcBuildFlag;
import com.htc.lib1.theme.ThemeFileUtil;

/**
 * This class provide some utilities
 */
public class HtcCommonUtil {

    private static final String CLASS_NAME = "android.content.res.HtcConfiguration";

    final static String TAG = "HtcCommonUtil";

    /**
     * Baseline
     */
    public static final int BASELINE = HtcThemeUtils.COMMON_CATEGORY_BASELINE;

    /**
     * CATEGORY1
     */
    public static final int CATEGORYONE = HtcThemeUtils.COMMON_CATEGORY_ONE;

    /**
     * CATEGORY2
     */
    public static final int CATEGORYTWO = HtcThemeUtils.COMMON_CATEGORY_TWO;

    /**
     * CATEGORY3
     */
    public static final int CATEGORYTHREE = HtcThemeUtils.COMMON_CATEGORY_THREE;

    /**
     * Set the category if HTC Theme id change should be considered.
     */
    public static final String CATEGORY_CONFIG_THEMEID = "com.htc.intent.category.THEMEID";

    private static class ChangeObservable extends Observable {
        @Override
        public void notifyObservers(Object data) {
            setChanged();
            super.notifyObservers(data);
            clearChanged();
        }
    }

    private static final Map<Integer, ChangeObservable> OBSERVABLES = new HashMap<Integer, ChangeObservable>();

    /*
     * The type for the font size changed
     */
    public static final int TYPE_FONT_SIZE = 1;

    /*
     * The type for the font style changed
     */
    public static final int TYPE_FONT_STYLE = 2;

    /*
     * The type for the theme changed
     */
    public static final int TYPE_THEME = 4;

    private static final int[][] DEFAULTTHEMES = {
        {
            R.style.HtcDeviceDefault,
            R.style.HtcDeviceDefault_CategoryOne,
            R.style.HtcDeviceDefault_CategoryTwo,
            R.style.HtcDeviceDefault_CategoryThree
        }
    };

    private static int[][] mThemes = null;

    private static Method s_getHtcThemeId = null;

    static {
        try {
            // Load Class
            final Class className = Class.forName(CLASS_NAME);
            // Load Methods
            s_getHtcThemeId = className.getMethod("getHtcThemeId");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            android.util.Log.d(TAG, "Class not found");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            android.util.Log.d(TAG, "No such method");
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            android.util.Log.d(TAG, "Illegal arguments");
        }
    }

    /**
     * Get the resource id of HTC theme base on the category
     *
     * @param categoryId the id of category, such as {@link #BASELINE},
     *            {@link #CATEGORYONE}, {@link #CATEGORYTWO},
     *            {@link #CATEGORYTHREE}, {@link #CATEGORYFOUR}
     * @return the resource id
     * @throws RuntimeException throws RuntimeException if HDK invoke failed.
     */
    public static int getHtcThemeId(Context context, int categoryId) {
        int themeId = -1;
        int cId = categoryId;
        try {
            if (mThemes == null) {
                mThemes = generateThemes(context);
            }

            if (s_getHtcThemeId == null) {
                if (cId < 0 || cId > mThemes[0].length - 1)
                    cId = BASELINE;
                return mThemes[0][cId];
            }
            int id = -1;
            id = (Integer) s_getHtcThemeId.invoke(null);
            if (id < 0 || id > mThemes.length - 1)
                id = 0;
            if (cId < 0 || cId > mThemes[id].length - 1)
                cId = BASELINE;
            themeId = mThemes[id][cId];
        } catch (InvocationTargetException e) {
            android.util.Log.d(TAG, "Method Invoke failed with InvocationTargetException");
        } catch (IllegalAccessException e) {
            android.util.Log.d(TAG, "Method Invoke failed with IllegalAccessException");
        }
        if (themeId == -1) {
            if (cId < 0 || cId > DEFAULTTHEMES.length - 1)
                cId = BASELINE;
            return DEFAULTTHEMES[0][cId];
        } else
            return themeId;
    }

    private static int[][] generateThemes(Context context) {
        if (context == null)
            throw new IllegalArgumentException("The Context is null");
        Resources res = context.getResources();
        int[][] tmpThemes = null;
        TypedArray a = res.obtainTypedArray(R.array.multipleColorThemes);
        TypedArray b;
        if (a != null) {
            int aId = -1;
            for (int i = 0; i < a.length(); i++) {
                if (tmpThemes == null) tmpThemes = new int[a.length()][];
                aId = a.getResourceId(i, -1);
                if (aId != -1) {
                    b = res.obtainTypedArray(aId);
                    if (b != null) {
                        int bId = -1;
                        for (int j = 0; j < b.length(); j++) {
                            if (tmpThemes[i] == null)
                                tmpThemes[i] = new int[b.length()];
                            bId = b.getResourceId(j, -1);
                            if (bId != -1)
                                tmpThemes[i][j] = bId;
                            else
                                android.util.Log.d(TAG, "Can't get resource base on id = " + j);
                        }
                        b.recycle();
                    }
                }
            }
            a.recycle();
            if (tmpThemes == null)
                android.util.Log.d(TAG, "Can't find resources");
            else
                return tmpThemes;
        }
        android.util.Log.d(TAG, "Can't parse the typedArray");
        return DEFAULTTHEMES;
    }

    /**
     * To notify the change for different types.
     * @param context The Context the view is running in, through which it can access the current
     *            theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param type the types of different cases, such as {@link #TYPE_FONT_SIZE},
     *            {@link #TYPE_FONT_STYLE}, {@link #TYPE_THEME},
     */
    public static void notifyChange(Context context, int type) {
        for (Integer i : OBSERVABLES.keySet()) {
            if ((i & type) != 0) {
                ChangeObservable observable = OBSERVABLES.get(i);
                observable.notifyObservers(context);
            }
        }
    }

    /*
     * @hide [module internal use] Only use in CC, please do not use it
     */
    @Deprecated
    public static void addObserver(int type, Observer observer) {
        ChangeObservable observable = OBSERVABLES.get(type);
        if (observable == null) {
            OBSERVABLES.put(type, observable = new ChangeObservable());
        }
        observable.addObserver(observer);
    }

    /**
     * Callback class for receiving the theme package change for each theme type.
     */
    public interface ThemeChangeObserver extends HtcThemeUtils.ThemeChangeObserver {
        /**
         * This method is called when a theme change occurs.
         *
         * @param type The type of the changed theme.
         */
        @Override public void onThemeChange(int type);
    }

    /**
     * A listener for users to hook the {@link com.htc.lib1.cc.util.HtcCommonUtil#getCommonThemeColor(Context, int)}. <br/>
     * This listener make users can set custom color instead of the themed color.
     */
    public static class ObtainThemeListener implements HtcThemeUtils.ObtainThemeListener {
        @Override public int onObtainThemeColor(int index, int themeColor) { return themeColor; }
    }

    /**
     * To set {@link com.htc.lib1.cc.util.HtcCommonUtil.ObtainThemeListener}. <br/>
     * Please note the internal implementation uses <B>WeakReference</B> to your listener. <br/>
     * It will be GCed very soon if there is no StrongReference to the listener. <br/>
     * Please avoid this kind of usage: <B>setObtainThemeListener(new ObtainThemeListener())</B>;
     * @param listener The listener user defined, null to clear.
     */
    public static void setObtianThemeListener(ObtainThemeListener listener) {
        HtcThemeUtils.setObtainThemeListener(listener);
    }

    /**
     * Get the current theme package for the specified type.
     * @param context The Context the view is running in, through which it can access the current
     *            theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param type The theme type defined in {@link com.htc.lib1.theme.ThemeType}.
     * @return The current theme package for the specified type.
     */
    public static String getHtcThemePackage(Context context, int type) {
        return HtcThemeUtils.getHtcThemePackage(context, type);
    }

    /**
     * Get the current theme package for the specified type.
     * <BR/><B>Warring: Do not use this API if you never discussed with owner of HtcCommonUtil.</B>
     * @param context The Context the view is running in, through which it can access the current
     *            theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param type The theme type defined in {@link com.htc.lib1.theme.ThemeType}.
     * @param userHandle it should be USERHANDLE_CURRENT.
     * @return The current theme package for the specified type.
     */
    public static String getHtcThemePackage(Context context, int type, int userHandle) {
        return HtcThemeUtils.getHtcThemePackage(context, type, true, userHandle);
    }

    /**
     * Set the current theme package for the specified type. The default theme package is an empty
     * string. This will notify the registered {@link ThemeChangeObserver} if the theme changes.
     * @param context The Context the view is running in, through which it can access the current
     *            theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param type The theme type defined in {@link com.htc.lib1.theme.ThemeType}.
     * @param themePackage The theme package.
     * @return True if set the theme package successfully, false otherwise.
     */
    public static boolean setHtcThemePackage(Context context, int type, String themePackage) {
        return HtcThemeUtils.setHtcThemePackage(context, type, themePackage);
    }

    /**
     * Set the current theme package for the specified type. The default theme package is an empty
     * string. This will notify the registered {@link ThemeChangeObserver} if the theme changes.<BR/>
     * <B>Warring: Do not use this API if you never discussed with owner of HtcCommonUtil.</B>
     * @param context The Context the view is running in, through which it can access the current
     *            theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param type The theme type defined in {@link com.htc.lib1.theme.ThemeType}.
     * @param themePackage The theme package.
     * @param userHandle it should be USERHANDLE_CURRENT.
     * @return True if set the theme package successfully, false otherwise.
     */
    public static boolean setHtcThemePackage(Context context, int type, String themePackage, int userHandle) {
        return HtcThemeUtils.setHtcThemePackage(context, type, themePackage, true, userHandle);
    }

    /**
     * Register the theme change observers that get callback when the theme package for the given
     * type is changed. The observer SHOULD be unregistered when it is out of use. (e.g.
     * Activity.onDestroy()). This method MUST be called in UI thread.
     * @param context The Context the view is running in, through which it can access the current
     *            theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param type The theme type to watch for the changes. The type is defined in
     *            {@link com.htc.lib1.theme.ThemeType}.
     * @param observer The object that receives callback when the theme changes occur.
     */
    public static void registerThemeChangeObserver(Context context, int type, ThemeChangeObserver observer) {
        HtcThemeUtils.registerThemeChangeObserver(context, type, observer);
    }

    /**
     * Register the theme change observers that get callback when the theme package for the given
     * type is changed. The observer SHOULD be unregistered when it is out of use. (e.g.
     * Activity.onDestroy()). This method MUST be called in UI thread.<BR/>
     * <B>Warring: Do not use this API if you never discussed with owner of HtcCommonUtil.</B>
     * @param context The Context the view is running in, through which it can access the current
     *            theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param type The theme type to watch for the changes. The type is defined in
     *            {@link com.htc.lib1.theme.ThemeType}.
     * @param userHandle it must be USERHANDLE_ALL.
     * @param observer The object that receives callback when the theme changes occur.
     */
    public static void registerThemeChangeObserver(Context context, int type, ThemeChangeObserver observer, int userHandle) {
        HtcThemeUtils.registerThemeChangeObserver(context, type, observer, true, userHandle);
    }

    /**
     * Unregister the theme change observers.
     * This method MUST be called in UI thread.
     *
     * @param type The theme type that the observers watch for the changes.
     *             The type is defined in {@link com.htc.lib1.theme.ThemeType}.
     * @param observer The object that receives callback when the theme changes occur.
     */
    public static void unregisterThemeChangeObserver(int type, ThemeChangeObserver observer) {
        HtcThemeUtils.unregisterThemeChangeObserver(type, observer);
    }

    /**
     * Unregister the theme change observers.
     * This method MUST be called in UI thread.
     *
     * @param type The theme type that the observers watch for the changes.
     *             The type is defined in {@link com.htc.lib1.theme.ThemeType}.
     * @param observer The object that receives callback when the theme changes occur.
     * @param userHandle it must be USERHANDLE_ALL.
     */
    public static void unregisterThemeChangeObserver(int type, ThemeChangeObserver observer, int userHandle) {
        HtcThemeUtils.unregisterThemeChangeObserverForUser(type, observer, userHandle);
    }

    /**
     * To initialize the necessary environment for theme change mechanism. <br/>
     * Must invoke this API before anything related to theme change or you will get an
     * IllegalStateException.
     * @param context The Context the view is running in, through which it can access the current
     *            theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param category Specify the theme category used by this AP. <br/>
     *            Please assign the same category with android:theme you assigned in the
     *            AndroidManifest.xml.
     */
    public static void initTheme(ContextThemeWrapper context, int category) {
        initTheme(context, category, null, null);
    }

    /**
     * To initialize the necessary environment for theme change mechanism. <br/>
     * Must invoke this API before anything related to theme change or you will get an
     * IllegalStateException.
     * @param context The Context the view is running in, through which it can access the current
     *            theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param category Specify the theme category used by this AP. <br/>
     *            Please assign the same category with android:theme you assigned in the
     *            AndroidManifest.xml.
     * @param onTextureReady Specify the callback that will be called after the texture is ready<br/>
     *            if the application will have fine gained control, you can do it by assign this call back.
     */
    public static void initTheme(ContextThemeWrapper context, int category, ThemeFileUtil.FileCallback onTextureReady) {
        initTheme(context, category, null, onTextureReady);
    }


    /**
     * To initialize the necessary environment for theme change mechanism. <br/>
     * Must invoke this API before anything related to theme change or you will get an
     * IllegalStateException.
     *
     * @param context The Context the view is running in, through which it can access the current
     *            theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param category Specify the theme category used by this AP. <br/>
     *            Please assign the same category with android:theme you assigned in the
     *            AndroidManifest.xml.
     * @param customPath the custom path
     */
    public static void initTheme(ContextThemeWrapper context, int category, String customPath) {
        TraceCompat.beginSection("[" + TAG + "] initTheme");
        initTheme(context, category, customPath, null);
        TraceCompat.endSection();
    }

    /**
     * To initialize the necessary environment for theme change mechanism. <br/>
     * Must invoke this API before anything related to theme change or you will get an
     * IllegalStateException.
     *
     * @param context The Context the view is running in, through which it can access the current
     *            theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param category Specify the theme category used by this AP. <br/>
     *            Please assign the same category with android:theme you assigned in the
     *            AndroidManifest.xml.
     * @param customPath the custom path
     * @param onTextureReady Specify the callback that will be called after the texture is ready<br/>
     *            if the application will have fine gained control, you can do it by assign this call back.
     */
    public static void initTheme(ContextThemeWrapper context, int category, String customPath, ThemeFileUtil.FileCallback onTextureReady) {
        TraceCompat.beginSection("[" + TAG + "] initTheme");
        HtcThemeUtils.init(context, category, customPath, onTextureReady);
        TraceCompat.endSection();
    }

    /**
     * To initialize the necessary environment for theme change mechanism. <br/>
     * Must invoke this API before anything related to theme change or you will get an
     * IllegalStateException. <BR/>
     * <B>Warring: Do not use this API if you never discussed with owner of HtcCommonUtil.</B>
     * @param context The Context the view is running in, through which it can access the current
     *            theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param category Specify the theme category used by this AP. <br/>
     *            Please assign the same category with android:theme you assigned in the
     *            AndroidManifest.xml.
     * @param userHandle it should be USERHANDLE_CURRENT.
     */
    public static void initTheme(ContextThemeWrapper context, int category, int userHandle, boolean recreate) {
        TraceCompat.beginSection("["+TAG+"] initThemeForUser");
        initTheme(context, category, userHandle, recreate, null);
        TraceCompat.endSection();
    }

    /**
     * To initialize the necessary environment for theme change mechanism. <br/>
     * Must invoke this API before anything related to theme change or you will get an
     * IllegalStateException. <BR/>
     * <B>Warring: Do not use this API if you never discussed with owner of HtcCommonUtil.</B>
     * @param context The Context the view is running in, through which it can access the current
     *            theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param category Specify the theme category used by this AP. <br/>
     *            Please assign the same category with android:theme you assigned in the
     *            AndroidManifest.xml.
     * @param userHandle it should be USERHANDLE_CURRENT.
     * @param recreate to make theme recreate or not
     * @param onTextureReady Specify the callback that will be called after the texture is ready<br/>
     *            if the application will have fine gained control, you can do it by assign this call back.
     */
    public static void initTheme(ContextThemeWrapper context, int category, int userHandle, boolean recreate, ThemeFileUtil.FileCallback onTextureReady) {
        TraceCompat.beginSection("["+TAG+"] initThemeForUser");
        HtcThemeUtils.init(context, category, true, userHandle, recreate, null, onTextureReady);
        TraceCompat.endSection();
    }

    /**
     * This API is used to get a resource instance of specified APK but it is not belong to
     * ActivityManager. <br/>
     * Please remember to update its configuration while configuration is changed to ensure the
     * correctness. Note: If the specified APK file does not exist or no permission to access it,
     * this would return context.getResources().
     * @param context The Context the view is running in, through which it can access the current
     *            theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param apkName The name of the target apk exclusive the ".apk" extension.
     * @return The resource instance for current configuration.
     * @see #updateCommonResConfiguration(Context)
     */
    public static Resources getResources(Context context, String apkName) {
        TraceCompat.beginSection("[" + TAG + "] getResources(Context, String)");
        Resources ret = HtcThemeUtils.getResources(context, apkName);
        TraceCompat.endSection();
        return ret;
    }

    /**
     * This API is used to get a resource instance of specified APK but it is not belong to
     * ActivityManager. <br/>
     * Please remember to update its configuration while configuration is changed to ensure the
     * correctness. Note: If the specified APK file does not exist or no permission to access it,
     * this would return context.getResources(). <BR/>
     * <B>Warring: Do not use this API if you never discussed with owner of HtcCommonUtil.</B>
     * @param context The Context the view is running in, through which it can access the current
     *            theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param apkName The name of the target apk exclusive the ".apk" extension.
     * @param userHandle it should be USERHANDLE_CURRENT.
     * @return The resource instance for current configuration.
     * @see #updateCommonResConfiguration(Context)
     */
    public static Resources getResources(Context context, String apkName, int userHandle) {
        TraceCompat.beginSection("["+TAG+"] getResources(Context, String, int)");
        Resources ret = HtcThemeUtils.getResources(context, apkName, true, userHandle);
        TraceCompat.endSection();
        return ret;
    }

    /**
     * This API is used to get a resource instance of specified APK but it is not belong to
     * ActivityManager. <br/>
     * Please remember to update its configuration while configuration is changed to ensure the
     * correctness. Note: If the specified APK file does not exist or no permission to access it,
     * this would return context.getResources().
     * @param context The Context the view is running in, through which it can access the current
     *            theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param apkName The name of the target apk exclusive the ".apk" extension.
     * @param apkPath The specified path of the specified apkName.
     * @return The resource instance for current configuration.
     * @see #updateCommonResConfiguration(Context)
     */
    public static Resources getResources(Context context, String apkName, String apkPath) {
        TraceCompat.beginSection("["+TAG+"] getResources(Context, String, String)");
        Resources ret = HtcThemeUtils.getResources(context, apkName, apkPath);
        TraceCompat.endSection();
        return ret;
    }

    /**
     * This is used to get the Theme of current common resources instance. <br/>
     * @return The Theme instance of current common resources instance.
     * @throws IllegalStateException If {@link #initTheme(Context, int)} has never been invoked.
     */
    public static Resources.Theme getCategoryTheme(Context context) {
        return HtcThemeUtils.getCurrentCommonTheme();
    }

    /**
     * Get the absolute path of the directory where the current theme apks and files are located. <br/>
     * Please remember to invoke {@link #initTheme(ContextThemeWrapper, int)} before this method or you will get nothing!!!
     * @return The current theme directory path.
     */
    public static String getCurrentThemePath() {
        return HtcThemeUtils.getCurrentThemePath();
    }

    /**
     * Get the absolute path of the directory where the current theme apks and files are located. <br/>
     * Please remember to invoke {@link #initTheme(ContextThemeWrapper, int)} before this method or you will get nothing!!!
     * <BR/><B>Warring: Do not use this API if you never discussed with owner of HtcCommonUtil.</B>
     * @param userHandle it should be USERHANDLE_CURRENT.
     * @return The current theme directory path.
     */
    public static String getCurrentThemePath(int userHandle) {
        return HtcThemeUtils.getCurrentThemePath(true, userHandle);
    }

    /**
     * Get the absolute path of the directory where the current theme apks and files are located.
     * @param context The Context the view is running in, through which it can access the current
     *            theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @return The current theme directory path.
     */
    public static String getCurrentThemePath(Context context) {
        return HtcThemeUtils.getCurrentThemePath(context);
    }

    /**
     * Get the absolute path of the directory where the current theme apks and files are located.
     * <BR/><B>Warring: Do not use this API if you never discussed with owner of HtcCommonUtil.</B>
     * @param context The Context the view is running in, through which it can access the current
     *            theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param userHandle it should be USERHANDLE_CURRENT.
     * @return The current theme directory path.
     */
    public static String getCurrentThemePath(Context context, int userHandle) {
        return HtcThemeUtils.getCurrentThemePath(context, true, userHandle);
    }

    /**
     * To get specified color code in current common resources instance.
     * @param context The Context the view is running in, through which it can access the current
     *            theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param index The theme color you want to get.
     * @return The specified color code or 0x0 if anything wrong.
     * @throws IllegalStateException If {@link #initTheme(Context, int)} has never been invoked.
     */
    public static int getCommonThemeColor(Context context, int index) {
        TraceCompat.beginSection("["+TAG+"] getCommonThemeColor");
        int ret = HtcThemeUtils.getCommonThemeColor(context, index);
        if (HtcBuildFlag.Htc_DEBUG_flag && ret == 0 ) {
            Log.e(TAG,
                    "get Color value  is 0, please check Activity Theme include HtcDeviceDefault!",
                    new Exception());
        }
        TraceCompat.endSection();
        return ret;
    }

    /**
     * To get the specified theme texture that fits current theme.
     * @param context The Context the view is running in, through which it can access the current
     *            theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param index The texture index you want to get.
     * @return The specified theme texture or null if not found.
     * @throws IllegalStateException If {@link #initTheme(Context, int)} has never been invoked.
     * @throws IllegalArgumentException If the parameter context is invalid (such as null).
     */
    public static Drawable getCommonThemeTexture(Context context, int index) {
        Drawable ret = getCommonThemeTexture(context, index, null);
        return ret;
    }

    /**
     * To get the specified theme texture that fits current theme.
     *
     * @param context The Context the view is running in, through which it can access the current
     *            theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param index The texture index you want to get.
     * @return The specified theme texture or null if not found.
     * @throws IllegalStateException If {@link #initTheme(Context, int)} has never been invoked.
     * @throws IllegalArgumentException If the parameter context is invalid (such as null).
     */
    public static Drawable getCommonThemeTexture(Context context, int index, String customPath) {
        TraceCompat.beginSection("[" + TAG + "] getCommonThemeTexture");
        Drawable ret = HtcThemeUtils.getCommonThemeTexture(context, index, customPath);
        TraceCompat.endSection();
        return ret;
    }

    /**
     * To update Configuration and DisplayMetrics using current ones of AP. <br/>
     * Cause we create another resources instance not belong to ActivityManager so it will never
     * updated while Configuration is changed. <br/>
     * Please remember to invoke this API to update Configuration and DisplayMetrics each time
     * Configuration may be changed such as rotation, etc.
     * @param context The Context the view is running in, through which it can access the current
     *            theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     */
    public static void updateCommonResConfiguration(Context context) {
        HtcThemeUtils.updateCommonResConfiguration(context);
    }

    /**
     * To clear the Texture cache.
     */
    public static void clearCache() {
        HtcThemeUtils.clearCaches();
    }

    public static int getStatusBarHeight(Context context) {
        // first set default 25 dp and then convert it to px
        float statusBarHeight = (float) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 25, context.getResources().getDisplayMetrics());
        int id = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (id > 0) {
            statusBarHeight = context.getResources().getDimension(id);
        }
        return (int) Math.ceil(statusBarHeight);
    }
}
