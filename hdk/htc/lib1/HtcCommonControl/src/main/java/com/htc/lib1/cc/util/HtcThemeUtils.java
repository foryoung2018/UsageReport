package com.htc.lib1.cc.util;

import java.util.ArrayList;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;

import android.net.Uri;
import android.os.Handler;
import android.os.UserHandle;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.ContentObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.util.LongSparseArray;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;

import com.htc.lib1.cc.R;
import com.htc.lib1.cc.htcjavaflag.HtcBuildFlag;
import com.htc.lib1.cc.support.provider.SettingsCompat;
import com.htc.lib1.theme.ThemeFileUtil;
import com.htc.lib1.theme.ThemeType;
import com.htc.lib1.theme.ThemeSettingUtil;

/*package*/ final class HtcThemeUtils {
    private static final String TAG = "HtcThemeUtils";
    private static final String KEY_HTC_CURRENT_THEME_PATH = ThemeType.KEY_APP_CURRENT_THEME_PATH;
    private static final String KEY_HTC_OPTIONAL_THEME_PATH = ThemeType.KEY_SYS_CURRENT_THEME_PATH;
    private static final String INIT_ERR_MSG = "Must invoke HtcCommonUtil.initTheme(ContextThemeWrapper, int) before this action!";
    private static final String ARGS_ERR_MSG = "The context passed in is null!!!!!!!";

    private static final int[] CCR_CATEGORY_IDS = {
        R.style.HtcDeviceDefault,
        R.style.HtcDeviceDefault_CategoryOne,
        R.style.HtcDeviceDefault_CategoryTwo,
        R.style.HtcDeviceDefault_CategoryThree
    };

    /*package*/ static final int COMMON_CATEGORY_BASELINE = 0;
    /*package*/ static final int COMMON_CATEGORY_ONE = 1;
    /*package*/ static final int COMMON_CATEGORY_TWO = 2;
    /*package*/ static final int COMMON_CATEGORY_THREE = 3;

    /*package*/ interface ThemeChangeObserver {
        public void onThemeChange(int type);
    }

    /*package*/ interface ObtainThemeListener {
        public int onObtainThemeColor(int index, int themeColor);
    }

    private static final String COMMON_RES_FILE_NAME = "CResources";
    private static final String[] CATEGORY_RES_FILE_NAMES = {"CBaseline", "CCategoryOne", "CCategoryTwo", "CCategoryThree"};
    private static final String RES_FILE_EXTENSION = ".apk";
    private static final String KEY_SEPARATOR = ":";

    private static Context mAppContext;

    private static String mTargetAPKPath = null;
    private static String mCustomAPKPath = null;
    private static boolean mNeedToReloadThemePath = false;

    /* To observe if current theme path is changed or not. */
    private static ThemePathObserver mThemePathObserver = null;

    /* To observe if the theme key is changed or not */
    private static ThemeKeyObserver mThemeKeyObserver = null;

    /* A commander to observe the theme package is changed or not.
       If yes, it will notify all the observers(ThemeKeyObserverForUsers) that the change has happened. */
    private static ThemeKeyObserverForUsers mThemeContentObserver = null;

    private final static SparseArray<ArrayList<ThemeChangeObserver>> THEME_CHANGE_OBSERVERS;
    private final static SparseBooleanArray CONTENT_OBSERVERS_REGISTER_FLAGS;

    private static WeakReference<ObtainThemeListener> mObtainThemeListener;

    static {
        THEME_CHANGE_OBSERVERS = new SparseArray<ArrayList<ThemeChangeObserver>>();
        CONTENT_OBSERVERS_REGISTER_FLAGS = new SparseBooleanArray();
    }

    /*package*/ static void setObtainThemeListener(ObtainThemeListener listener) {
        mObtainThemeListener = listener==null ? null : new WeakReference<ObtainThemeListener>(listener);
    }

    private static class ThemePathObserver extends ContentObserver {
        public ThemePathObserver(Handler handler) {
            super(handler);
        }

        public void onChange (boolean selfChange, Uri uri) {
            Log.d(TAG, "[ThemePathObserver.onChange] selfChange="+selfChange+", uri="+uri);
            setIfNeedReloadPath(true);
        }
    };

    /**
     * To reload path where theme packages are stored at.
     */
    /*package*/ static String getCurrentThemePath() {
        if (getIfNeedReloadPath() && mAppContext != null) {
            mTargetAPKPath = getCurrentThemePackage(mAppContext, KEY_HTC_CURRENT_THEME_PATH);
            if (TextUtils.isEmpty(mCustomAPKPath)) mCustomAPKPath = getCurrentThemePackage(mAppContext, KEY_HTC_OPTIONAL_THEME_PATH);
            setIfNeedReloadPath(false);
        }
        return mTargetAPKPath;
    }

    /*package*/ static String getCurrentThemePath(Context context) {
        return context==null ? null : getCurrentThemePackage(context, KEY_HTC_CURRENT_THEME_PATH);
    }

    /**
     * To set the flag which indicates whether we need to recreate the resources instance or not. <br/>
     * The timing to set the flag is usually that the theme package has been changed. <br/>
     * Thus, we need to reload the theme package resources to apply theme change.
     *
     * @param need True to recreate, false to do nothing.
     */
    private static void setIfNeedReloadPath(boolean need) {
        mNeedToReloadThemePath = need;
    }

    private static boolean getIfNeedReloadPath() {
        return mNeedToReloadThemePath;
    }

    private static String getCurrentThemePackage(Context context, String themeType) {
        return ThemeSettingUtil.getString(context, themeType);
    }

    /*package*/ static String getHtcThemePackage(Context context, int type) {
        String themeType = ThemeType.getKey(type), value = null;

        if (context!=null && themeType!=null)
            value = getCurrentThemePackage(context, themeType);

        return value==null ? "" : value;
    }

    /*package*/ static boolean setHtcThemePackage(Context context, int type, String themePackage) {
        boolean ret = false;
        String themeType = ThemeType.getKey(type);

        if (context!=null && themeType!=null && themePackage!=null) {
            String originalPackage = getCurrentThemePackage(context, themeType);
            if (!themePackage.equals(originalPackage)) {
                ThemeSettingUtil.putString(context, themeType, themePackage);
                ret = themePackage.equals(getCurrentThemePackage(context, themeType));
                Log.d(TAG, "Set thm pkg from \'" + originalPackage + "\' to \'" + themePackage + "\' for type " + type);
            }
        }

        return ret;
    }

    /**
     * This observer is used to observe the keys' changes from ThemePicker, DeviceMaker, etc.
     * It is dedicated to HtcThemeUtils itself.
     */
    private static class ThemeKeyObserver extends ContentObserver {
        public ThemeKeyObserver(Handler handler) {
            super(handler);
        }

        public void onChange(boolean selfChange, Uri uri) {
            String themeType = getThemeTypeFromUri(uri);
            ThemeLogUtil.LogD("[ThemeKeyObserver][onChange] type=" + themeType);
            Log.d(TAG, "[ThemeKeyObserver][onChange] type=" + themeType);
            if (themeType == null) return;

            int themeTypeId;
            for (themeTypeId = 0; themeTypeId < ThemeType.getKeyCount(); themeTypeId++) {
                if (themeType.equals(ThemeType.getKey(themeTypeId))) {
                    switch (themeTypeId) {
                        case ThemeType.HTC_THEME_CC:
                            CommonCategoryResources.setRecreateFlag(true);
                            break;
                        case ThemeType.HTC_THEME_CT:
                            CommonThemeResources.setRecreateFlag(true);
                            break;
                    }
                    break;
                }
            }
        }
    }

    /**
     * This observer is used to observe the keys' changes from ThemePicker, DeviceMaker, etc.
     * It is dedicated to all users who need get theme change callback except HtcThemeUtils.
     */
    private static class ThemeKeyObserverForUsers extends ContentObserver {
        public ThemeKeyObserverForUsers(Handler handler) {
            super(handler);
        }

        public void onChange (boolean selfChange, Uri uri) {
            String themeType = getThemeTypeFromUri(uri);
            if (themeType == null) return;
            Log.d(TAG, "[ThemeKeyObserverForUsers][onChange] type=" + themeType);

            for (int typeId=0; typeId<ThemeType.getKeyCount(); typeId++) {
                String type = ThemeType.getKey(typeId);
                if (type!=null && type.equals(themeType)) {
                    notifyObservers(typeId);
                    break;
                }
            }
        }

        private void notifyObservers(int typeId) {
            ThemeType.ThemeValue value = ThemeType.getValue(mAppContext, typeId, USERHANDLE_CURRENT);
            ThemeLogUtil.LogD("[ThemeKeyObserverForUsers][notifyObservers] type="+typeId+
                    " wait="+(value!=null ? value.wait : "na"));

            switch (typeId) {
            case ThemeType.HTC_THEME_CC:
                if (value!=null && !value.wait)
                    notifyAllObservers(typeId);
                break;
            case ThemeType.HTC_THEME_CT:
                if (value!=null && !value.wait)
                    notifyAllObservers(ThemeType.HTC_THEME_CC);
                break;
            case ThemeType.HTC_THEME_FULL:
                notifyAllObservers(typeId);
                break;
            default:
                if (value!=null && !value.wait)
                    notifyAllObservers(typeId);
            }
        }

        private void notifyAllObservers(int typeId) {
            ArrayList<ThemeChangeObserver> observers = THEME_CHANGE_OBSERVERS.get(typeId);
            for (int i=0; i<observers.size(); i++) {
                ThemeChangeObserver observer = observers.get(i);
                observer.onThemeChange(typeId);
            }
        }
    };

    /*package*/ static void registerThemeChangeObserver(Context context, int type, ThemeChangeObserver observer) {
        if (observer == null) throw new IllegalArgumentException("The observer is null.");
        if (context == null) throw new IllegalArgumentException("The context is null.");
        if (type<0 || type>=ThemeType.getKeyCount()) throw new IllegalArgumentException("The type is illegal.");

        initTableIfNecessary(type);
        ArrayList<ThemeChangeObserver> observers = THEME_CHANGE_OBSERVERS.get(type);

        if (!observers.contains(observer)) {
            registerContentObserverIfNecessary(context, type);
            observers.add(observer);
            Log.d(TAG,"Register " + observer  + " for type " + type);
        } else {
            Log.d(TAG,"Observer " + observer + " for type " + type + " is already registered.");
        }
    }

    private static void initTableIfNecessary(int type) {
        if (THEME_CHANGE_OBSERVERS.indexOfKey(type) >= 0) return;
        THEME_CHANGE_OBSERVERS.put(type, new ArrayList<ThemeChangeObserver>());
        CONTENT_OBSERVERS_REGISTER_FLAGS.put(type, false);
    }

    private static void registerContentObserverIfNecessary(Context context, int type) {
        if (!CONTENT_OBSERVERS_REGISTER_FLAGS.get(type)) {
            if (mThemeContentObserver == null)
                mThemeContentObserver = new ThemeKeyObserverForUsers(new Handler(context.getMainLooper()));
            CONTENT_OBSERVERS_REGISTER_FLAGS.put(type, true);
            ThemeSettingUtil.registerContentObserver(context, ThemeType.getKey(type), mThemeContentObserver);
            if (type==ThemeType.HTC_THEME_CC) {
                ThemeSettingUtil.registerContentObserver(context, ThemeType.getKey(ThemeType.HTC_THEME_CT), mThemeContentObserver);
                CONTENT_OBSERVERS_REGISTER_FLAGS.put(ThemeType.HTC_THEME_CT, true);
            }
        }
    }

    /*package*/ static void unregisterThemeChangeObserver(int type, ThemeChangeObserver observer) {
        if (observer == null) throw new IllegalArgumentException("The observer is null.");
        if (type<0 || type>=ThemeType.getKeyCount()) throw new IllegalArgumentException("The type is illegal.");
        if (THEME_CHANGE_OBSERVERS.indexOfKey(type) < 0) {
            Log.d(TAG,"Observer " + observer + " for type " + type + " was not registered.");
            return;
        }

        ArrayList<ThemeChangeObserver> observers = THEME_CHANGE_OBSERVERS.get(type);
        int index = observers.indexOf(observer);
        if (!(index < 0)) {
            observers.remove(index);
            Log.d(TAG,"Unregister " + observer  + " for type " + type);
        } else {
            Log.d(TAG,"Observer " + observer + " for type " + type + " was not registered.");
        }
    }

    /**
     * This API is used to get a resource instance of specified APK but it is not belong to ActivityManager. <br/>
     * Please remember to update its configuration while configuration is changed to ensure the correctness.
     *
     * @param context The AP context.
     * @param apkName The name of the target apk exclusive the ".apk" extension.
     * @return The resource instance for current configuration.
     *
     * @see #updateCommonResConfiguration(Context)
     */
    /*package*/ static Resources getResources(Context context, String apkName) {
        if(mAppContext == null) {
            // We should handle the case when the specified apk is not for CC or CT. (initTheme() would not be invoked).
            mAppContext = context.getApplicationContext();
            setIfNeedReloadPath(true);
        }
        return getResources(context, apkName, getCurrentThemePath(), mCustomAPKPath);
    }

    /**
     * This API is used to get a resource instance of specified APK but it is not belong to ActivityManager. <br/>
     * Please remember to update its configuration while configuration is changed to ensure the correctness.
     *
     * @param context The AP context.
     * @param apkName The name of the target apk exclusive the ".apk" extension.
     * @param apkPath The specified path of the specified apkName.
     * @return The resource instance for current configuration.
     *
     * @see #updateCommonResConfiguration(Context)
     */
    /*package*/ static Resources getResources(Context context, String apkName, String apkPath) {
        return getResources(context, apkName, apkPath, null);
    }

    /**
     * This API is used to get a resource instance of specified APK but it is not belong to ActivityManager. <br/>
     * Please remember to update its configuration while configuration is changed to ensure the correctness.
     *
     * @param context The AP context.
     * @param apkName The name of the target apk exclusive the ".apk" extension.
     * @param apkPath The specified path of the specified apkName.
     * @return The resource instance for current configuration.
     *
     * @see #updateCommonResConfiguration(Context)
     */
    /* package */static Resources getResources(Context context, String apkName, String apkPath, String optionalPath) {
        Resources res = context.getResources();
        String target = apkPath + apkName + RES_FILE_EXTENSION;
        Log.d(TAG, "context=" + context + ", target=" + target);
        Log.d(TAG, "optionalPath=" + optionalPath);
        try {
            Class assetMagCls = Class.forName("android.content.res.AssetManager");
            Constructor assetMagCt = assetMagCls.getConstructor();
            Object assetMag = assetMagCt.newInstance();

            Method assetMag_addAssetPathMtd = assetMagCls.getDeclaredMethod("addAssetPath", new Class[] { String.class });
            Object cookieObj = assetMag_addAssetPathMtd.invoke(assetMag, new Object[] { target });

            if((cookieObj instanceof Integer) && (((Integer)cookieObj).intValue()==0)) {
                // The apk file does not exist or no read apk file permission.
                Log.d(TAG,"AssetMaanger addAssetPath " + apkName + " fail!");

                if (TextUtils.isEmpty(optionalPath)) return res;

                target = optionalPath + apkName + RES_FILE_EXTENSION;
                Log.d(TAG, "context=" + context + ", optional=" + target);
                cookieObj = assetMag_addAssetPathMtd.invoke(assetMag, new Object[] { target });
                if ((cookieObj instanceof Integer) && (((Integer) cookieObj).intValue() == 0)) {
                    Log.d(TAG, "AssetMaanger addOptionalPath " + apkName + " fail!");
                    return res;
                }
            }
            ThemeLogUtil.LogD("addAssetPath successfully: " + target, new Throwable());

            Constructor resCt = Resources.class.getConstructor(new Class[] { assetMag.getClass(), res.getDisplayMetrics().getClass(), res.getConfiguration().getClass() });
            res = (Resources) resCt.newInstance(new Object[] { assetMag, res.getDisplayMetrics(), res.getConfiguration() });
        } catch (Exception ex) {
            Log.d(TAG, "Something wrong within HtcThemeUtils.getResources(context, String), please check stack trace above!!!", ex);
        }
        return res;
    }


    /* package */static void init(Context context, int category, String customPath, ThemeFileUtil.FileCallback onTextureReady) {
        init(context, category, false, 0, false, customPath, onTextureReady);
    }

    /**
     * This is used to get the Theme of current common resources instance. <br/>
     * @return The Theme instance of current common resources instance.
     */
    /*package*/ static Resources.Theme getCurrentCommonTheme() {
        return CommonCategoryResources.getThemeInstance();
    }

    /**
     * To get specified color code in current common resources instance.
     *
     * @param context The AP context.
     * @param index The theme color you want to get.
     * @return The specified color code or 0x0 if anything wrong.
     */
    /*package*/ static int getCommonThemeColor(Context context, int index) {
        int retColor = CommonCategoryResources.getColor(index);
        ObtainThemeListener listener = mObtainThemeListener==null ? null : mObtainThemeListener.get();
        int finalColor = listener!=null ? listener.onObtainThemeColor(index, retColor) : retColor;
        ThemeLogUtil.LogD("[getCommonThemeColor] listener="+listener+", index="+index+", retColor=0x"+Integer.toHexString(retColor)+", finalColor=0x"+Integer.toHexString(finalColor));
        return finalColor;
    }

    /**
     * To update Configuration and DisplayMetrics using current ones of AP. <br/>
     * Cause we create another resources instance not belong to ActivityManager so it will never updated while Configuration is changed. <br/>
     * Please remember to invoke this API to update Configuration and DisplayMetrics each time Configuration may be changed such as rotation, etc.
     *
     * @param context Current AP context for us to get current Configuration and DisplayMetrics.
     */
    /*package*/ static void updateCommonResConfiguration(Context context) {
        CommonCategoryResources.updateResourcesConfiguration(context);
    }

    /**
     * To get the specified theme texture that fits current theme.
     *
     * @param context The AP context.
     * @param index The texture index you want to get.
     * @return The specified theme texture or null if not found.
     */
    /* package */static Drawable getCommonThemeTexture(Context context, int index, String customPath) {
        return CommonThemeResources.getTexture(context, index, customPath);
    }

    /**
     * To clear the Texture cache.
     */
    /*package*/ static void clearCaches() {
        Log.d(TAG,"Clear the texture cache.", new Throwable());
        CommonThemeResources.clearTextureCache();
    }

    /**
     * The common category resources.
     */
    private static class CommonCategoryResources {
        private static final int COLOR_SRC_UNKNOWN = 0;
        private static final int COLOR_SRC_DEFAULT_THEME = 1;
        private static final int COLOR_SRC_THEME_PACKAGE = 2;
        private static final int COLOR_SRC_DEVICE_OVERRIDE = 4;

        private static final int OVERRIDE_COLOR_AMOUNT_PER_GROUP = 3;

        private static final int OVERRIDE_DEFAULT_COLOR_OFFSET = 0;
        private static final int OVERRIDE_LIGHT_COLOR_OFFSET = 1;
        private static final int OVERRIDE_DARK_COLOR_OFFSET = 2;

        private static Resources mCategoryRes;
        private static Resources.Theme mCategoryTheme;

        private static int mCategorySrc;
        private static int mCCCategory;
        private static int mThemeId;
        private static boolean mNeedRecreate;

        private static int[] mColors = null;
        private static int[] mAlternativeColors = null;

        private static void init(Context context, int category, boolean byUser, int userHandle) {
            if (context == null) throw new IllegalArgumentException(ARGS_ERR_MSG);
            createResourceInstance(context, category, byUser, userHandle);
        }

        private static boolean checkColorSrc(int srcMode) {
            return (mCategorySrc & srcMode) != COLOR_SRC_UNKNOWN;
        }

        private static void createResourceInstance(Context context, int category, boolean byUser, int userHandle) {
            // For category theme package (color)
            if (checkIfNeedRecreate(context)) {
                checkResourceSrc(context, category, byUser, userHandle);
                if (checkColorSrc(COLOR_SRC_DEFAULT_THEME)) {
                    mCategoryRes = context.getResources();
                    mCategoryTheme = context.getTheme();
                    ThemeLogUtil.LogD("[CC][createResourceInstance] Both Resources and Theme instances are from context");
                } else {
                    mCategoryRes = getResourcesInternal(context, CATEGORY_RES_FILE_NAMES[mCCCategory], byUser, userHandle);
                    try {
                        String resName = mCategoryRes.getResourceName(mThemeId);
                        ThemeLogUtil.LogD("[CC][createResourceInstance] mThemeId found: 0x"+Integer.toHexString(mThemeId)+", resName="+resName);
                    } catch (Exception ex) {
                        mCategoryRes = context.getResources();
                        Log.w(TAG,"[CC][createResourceInstance] The theme res id 0x"+Integer.toHexString(mThemeId)+" is not found in the category resouces.", ex);
                    }

                    if (mCategoryRes == context.getResources()) {
                        mCategoryTheme = context.getTheme();
                    } else {
                        mCategoryTheme = mCategoryRes.newTheme();
                        mCategoryTheme.applyStyle(mThemeId, true);
                    }
                }

                refreshColorTable();
                setRecreateFlag(false);
            }
        }

        private static void checkResourceSrc(Context context, int category, boolean byUser, int userHandle) {
            ThemeType.ThemeValue value = null;
            if (byUser) {
                value = ThemeType.getValue(mAppContext, ThemeType.HTC_THEME_CC, userHandle);
            } else {
                value = ThemeType.getValue(mAppContext, ThemeType.HTC_THEME_CC);
            }

            String selfData = value==null ? null : value.selfData;

            ThemeLogUtil.LogD("[CC][checkResourceSrc] category="+category+", selfData="+selfData);

            if (selfData == null) {
                // Use category theme color package (Theme Picker case or Default case)
                mCCCategory = (category>=0 && category<CATEGORY_RES_FILE_NAMES.length) ? category : 0;
                mThemeId = CCR_CATEGORY_IDS[0];
                mCategorySrc = COLOR_SRC_THEME_PACKAGE;
            } else {
                // Device Maker case
                mCCCategory = (category>=0 && category<CCR_CATEGORY_IDS.length) ? category : 0;
                mThemeId = CCR_CATEGORY_IDS[mCCCategory];
                mCategorySrc = COLOR_SRC_DEFAULT_THEME;
            }

            // Check if need to use alternative colors to substitute the original ones (Device Maker case)
            boolean needOverride = (selfData!=null && selfData.matches("^[cC]:.*"));
            setAlternativeColor(needOverride, selfData, (selfData!=null ? selfData.split(KEY_SEPARATOR) : null));
            if (mAlternativeColors != null) mCategorySrc |= COLOR_SRC_DEVICE_OVERRIDE;

            ThemeLogUtil.LogD("[CC][checkResourceSrc] mCCCategory="+mCCCategory+
                    ", mThemeId=0x"+Integer.toHexString(mThemeId)+
                    ", mCategorySrc="+Integer.toBinaryString(mCategorySrc)+
                    ", needOverride="+needOverride);
        }

        private static boolean checkIfNeedRecreate(Context context) {
            ThemeLogUtil.LogD("[CC][checkIfNeedRecreate] mNeedRecreate="+mNeedRecreate+
                    ", mCategoryRes="+ mCategoryRes+
                    ", mCategoryTheme="+mCategoryTheme+
                    ", sameDefRes="+(checkColorSrc(COLOR_SRC_DEFAULT_THEME) && context.getResources()!=mCategoryRes));
            Log.d(TAG, "[CC][checkIfNeedRecreate] mNeedRecreate="+mNeedRecreate+
                    ", mCategoryRes="+ mCategoryRes+
                    ", mCategoryTheme="+mCategoryTheme+
                    ", sameDefRes="+(checkColorSrc(COLOR_SRC_DEFAULT_THEME) && context.getResources()!=mCategoryRes));
            return mNeedRecreate || mCategoryRes==null || mCategoryTheme==null ||
                    (checkColorSrc(COLOR_SRC_DEFAULT_THEME) && context.getResources()!=mCategoryRes);
        }

        private static void setRecreateFlag(boolean need) {
            mNeedRecreate = need;
        }

        private static void createColorTable() {
            if (mCategoryRes==null || mCategoryTheme==null)
                throw new IllegalStateException(INIT_ERR_MSG);

            TypedArray ta = mCategoryTheme.obtainStyledAttributes(R.styleable.ThemeColor);
            mColors = new int[ta.length()];
            for (int i = 0; i < mColors.length; i++) {
                mColors[i] = ta.getColor(i, 0x0);
                ThemeLogUtil.LogD("[CC][createColorTable] mColors["+i+"]=0x"+Integer.toHexString(mColors[i]));
            }
            ta.recycle();

            // Override colors by alternative colors if needed
            if (checkColorSrc(COLOR_SRC_DEVICE_OVERRIDE))
                overrideAlternativeColors();
        }

        private static void overrideAlternativeColors() {
            // Default Color: category_color, multiply_color, standard_color
            int defaultColor = mAlternativeColors[OVERRIDE_DEFAULT_COLOR_OFFSET];
            mColors[R.styleable.ThemeColor_multiply_color] = defaultColor;
            mColors[R.styleable.ThemeColor_standard_color] = defaultColor;

            // Light Color: light_category_color, overlay_color
            int lightColor = mAlternativeColors[OVERRIDE_LIGHT_COLOR_OFFSET];
            mColors[R.styleable.ThemeColor_category_color] = lightColor;
            mColors[R.styleable.ThemeColor_light_category_color] = lightColor;
            mColors[R.styleable.ThemeColor_overlay_color] = lightColor;

            // Dark Color: dark_ category_color
            int darkColor = mAlternativeColors[OVERRIDE_DARK_COLOR_OFFSET];
            mColors[R.styleable.ThemeColor_dark_category_color] = darkColor;

            ThemeLogUtil.LogD("[CC][overrideAlternativeColors] default=0x" + Integer.toHexString(defaultColor) +
                    ", light=0x" + Integer.toHexString(lightColor) +
                    ", dark=0x" + Integer.toHexString(darkColor));
        }

        private static void refreshColorTable() {
            mColors = null;
            createColorTable();
        }

        private static int getColor(int index) {
            if (mColors == null) createColorTable();
            return (mColors!=null && (index>=0 && index<mColors.length) ? mColors[index] : 0x0);
        }

        private static void setAlternativeColor(boolean enabled, String selfData, String[] colors) {
            mAlternativeColors = null;

            if (enabled && colors!=null) {
                int index = mCCCategory * OVERRIDE_COLOR_AMOUNT_PER_GROUP + 1;
                try {
                    int defaultColor, lightColor, darkColor;
                    defaultColor = Color.parseColor("#"+colors[index+OVERRIDE_DEFAULT_COLOR_OFFSET]);
                    lightColor = Color.parseColor("#"+colors[index+OVERRIDE_LIGHT_COLOR_OFFSET]);
                    darkColor = Color.parseColor("#"+colors[index+OVERRIDE_DARK_COLOR_OFFSET]);
                    mAlternativeColors = new int[] {defaultColor, lightColor, darkColor};
                } catch (Exception ex) {
                    mAlternativeColors = null;
                    Log.w(TAG, "Can not get alternative color from specificInfo:"+selfData+", with category("+mCCCategory+")", ex);
                }
            }
        }

        private static Resources.Theme getThemeInstance() {
            return mCategoryTheme;
        }

        private static void updateResourcesConfiguration(Context context) {
            Resources res = context.getResources();
            if (mCategoryRes!=null && mCategoryRes!=res)
                mCategoryRes.updateConfiguration(res.getConfiguration(), res.getDisplayMetrics());
        }
    }

    /**
     * The common theme textures in the the common res package.
     */
    private static class CommonThemeResources {
        private static final int TEXTURE_SRC_UNKNOWN = 0;
        private static final int TEXTURE_SRC_DEFAULT_THEME = 1;
        private static final int TEXTURE_SRC_THEME_PACKAGE = 2;
        private static final int TEXTURE_SRC_DEVICE_OVERRIDE = 4;

        private static final int[] mTargetThemeAttrs = new int[] {R.attr.themeTexture};

        private static Resources.Theme mCommonResTheme;

        private static int mTextureSrc;
        private static int mCTCategory;
        private static int mThemeId;
        private static boolean mNeedRecreate;

        private static String[] mAlternativeTextures;
        private static SparseIntArray mTextureAttrArray;
        private static final LongSparseArray<WeakReference<Drawable.ConstantState>> mCTCache;

        private static boolean mByUser = false;
        private static int mUserHandle = USERHANDLE_CURRENT;

        static {
            mTextureAttrArray = new SparseIntArray(3);

            // status_bar(Window) => action_bar(Header) => tab_bar(Panel)
            mTextureAttrArray.put(
                    R.styleable.CommonTexture_android_windowBackground, 0);
            mTextureAttrArray.put(
                    R.styleable.CommonTexture_android_headerBackground, 1);
            mTextureAttrArray.put(
                    R.styleable.CommonTexture_android_panelBackground, 2);

            mCTCache = new LongSparseArray<WeakReference<Drawable.ConstantState>>(0);
        }

        private static void init(Context context, int category, boolean byUser, int userHandle) {
            if (context == null) throw new IllegalArgumentException(ARGS_ERR_MSG);
            createResourceInstance(context, category, byUser, userHandle);
            mByUser = byUser;
            mUserHandle = userHandle;
        }

        private static boolean checkTextureSrc(int srcMode) {
            return (mTextureSrc & srcMode) != TEXTURE_SRC_UNKNOWN;
        }

        static void recreateResourceInstance(Context context, int category, boolean byUser, int userHandle) {
            checkResourceSrc(context, category, byUser, userHandle);
            if (checkTextureSrc(TEXTURE_SRC_DEFAULT_THEME)) {
                mCommonResTheme = context.getTheme();
                ThemeLogUtil.LogD("[CT][createResourceInstance] The Theme instance comes from context");
            } else {
                Resources res = getResourcesInternal(context, COMMON_RES_FILE_NAME, byUser, userHandle);
                if (res == context.getResources()) {
                    mCommonResTheme = context.getTheme();
                    ThemeLogUtil.LogD("[CT][createResourceInstance] The Theme instance comes from context");
                } else {
                    mCommonResTheme = res.newTheme();
                    mCommonResTheme.applyStyle(mThemeId, true);
                    ThemeLogUtil.LogD("[CT][createResourceInstance] apply theme id: 0x"+Integer.toHexString(mThemeId));
                }
            }
            clearTextureCache();
            setRecreateFlag(false);
        }

        private static void createResourceInstance(Context context, int category, boolean byUser, int userHandle) {
            // For common resources package (texture)
            if (checkIfNeedRecreate(context)) {
                recreateResourceInstance(context, category, byUser, userHandle);
            }
        }


        private static void checkResourceSrc(Context context, int category, boolean byUser, int userHandle) {
            ThemeType.ThemeValue value = null;
            if (byUser) {
                value = ThemeType.getValue(mAppContext, ThemeType.HTC_THEME_CT, userHandle);
            } else {
                value = ThemeType.getValue(mAppContext, ThemeType.HTC_THEME_CT);
            }

            String selfData = value==null ? null : value.selfData;

            ThemeLogUtil.LogD("[CT][checkResourceSrc] category="+category+", selfData="+selfData);

            if (selfData == null) {
                // Use common theme package (Theme Picker case or Default case)
                mCTCategory = 0;
                mThemeId = CCR_CATEGORY_IDS[mCTCategory];
                mTextureSrc = TEXTURE_SRC_THEME_PACKAGE;
            } else {
                // Device Maker case
                mCTCategory = (category>=0 && category<CCR_CATEGORY_IDS.length) ? category : 0;
                mThemeId = CCR_CATEGORY_IDS[mCTCategory];
                mTextureSrc = TEXTURE_SRC_DEFAULT_THEME;
            }

            boolean needOverride = (selfData!=null && selfData.matches("^[tT]:.*"));
            setAlternativeTexture(needOverride, selfData, (selfData!=null ? selfData.split(KEY_SEPARATOR) : null));
            if (mAlternativeTextures != null) mTextureSrc |= TEXTURE_SRC_DEVICE_OVERRIDE;

            ThemeLogUtil.LogD("[CT][checkResourceSrc] mCTCategory="+mCTCategory+
                    ", mThemeId=0x"+Integer.toHexString(mThemeId)+
                    ", mTextureSrc="+Integer.toBinaryString(mTextureSrc)+
                    ", needOverride="+needOverride);
        }

        private static boolean checkIfNeedRecreate(Context context) {
            ThemeLogUtil.LogD("[CT][checkIfNeedRecreate] mNeedRecreate="+mNeedRecreate+", mCommonResTheme="+mCommonResTheme);
            return mNeedRecreate || mCommonResTheme==null;
        }

        private static void setRecreateFlag(boolean need) {
            mNeedRecreate = need;
        }

        private static Drawable getTexture(Context context, int index, String customPath) {
            Drawable ret = null;
            WeakReference<Drawable.ConstantState> wr = mCTCache.get(index);

            if (wr != null) {
                Drawable.ConstantState cs = wr.get();
                if (cs != null) { // Cache hit!
                    ret = cs.newDrawable(context.getResources());
                    ThemeLogUtil.LogD("[CT][getTexture] index:"+index+" hit! ret="+ret);
                } else {
                    mCTCache.delete(index);
                }
            }

            // If ret is null, the cache is missed...
            if (ret == null) {
                String targetPath = (customPath == null ? ThemeFileUtil.getAppsThemePath(context) : customPath);
                ret = mAlternativeTextures==null ? getTextureFromCommonResTheme(index) :
                    getTextureFromAlternativeFile(context, targetPath, index);
                if (ret != null) {
                    Drawable.ConstantState cs = ret.getConstantState();
                    if (cs != null)
                        mCTCache.put(index, new WeakReference<Drawable.ConstantState>(cs));
                }
            }

            return ret;
        }

        private static Drawable getTextureFromCommonResTheme(int index) {
            int resId = 0;
            Drawable ret = null;

            try {
                TypedArray ta = mCommonResTheme.obtainStyledAttributes(mTargetThemeAttrs);
                resId = ta.getResourceId(0, 0x0);
                ta.recycle();
                TypedArray ta2 = mCommonResTheme.obtainStyledAttributes(resId, R.styleable.CommonTexture);
                ret = ta2.getDrawable(index);
                ta2.recycle();

                ThemeLogUtil.LogD("[CT][getTextureFromCommonResTheme] texture theme="+Integer.toHexString(resId)+
                        ", index="+index+", drawable="+ret);
            } catch (android.content.res.Resources.NotFoundException ex) {
                Log.w(TAG, "Can't find styled attrs, resId=0x"+Integer.toHexString(resId), ex);
            } catch (NullPointerException ex) {
                if (mCommonResTheme == null) throw new IllegalStateException(INIT_ERR_MSG);
            } catch (Exception ex) {
                Log.w(TAG, "Something wrong within HtcThemeUtils.getThemeTexture(context, int), please check stack trace below!!!", ex);
            }

            return ret;
        }

        private static Drawable getTextureFromAlternativeFile(Context context, String rootPath, int index) {
            Drawable ret = null;
            String targetPath = null;
            int targetTextureIndex = Integer.MIN_VALUE;

            try {
                targetTextureIndex = mTextureAttrArray.get(index, Integer.MIN_VALUE);
                targetPath = rootPath + mAlternativeTextures[targetTextureIndex];
                Bitmap bitmap = BitmapFactory.decodeFile(targetPath);
                ret = new BitmapDrawable(context.getResources(), bitmap);

                ThemeLogUtil.LogD("[CT][getTextureFromAlternativeFile] index="+targetTextureIndex+
                        ", path="+targetPath+", ret="+ret);
            } catch (NullPointerException ex) {
                if (context == null) throw new IllegalArgumentException(ARGS_ERR_MSG);
            } catch (ArrayIndexOutOfBoundsException ex) {
                if (targetTextureIndex < 0) Log.d(TAG, "The parameter index("+index+") is invalid!!!", ex);
            } catch (Exception ex) {
                Log.w(TAG, "Something wrong while loading alternative texture index=("+index+", "+
                        targetTextureIndex+")"+", texture path="+targetPath, ex);
            }

            return ret;
        }

        private static void clearTextureCache() {
            mCTCache.clear();
        }

        private static void setAlternativeTexture(boolean enabled, String selfData, String[] textures) {
            mAlternativeTextures = null;

            if (enabled && textures!=null) {
                if (textures.length > 1) {
                    // Ignore the head element to parse texture file names
                    mAlternativeTextures = new String[textures.length-1];
                    for (int i=0; i<mAlternativeTextures.length; i++)
                        mAlternativeTextures[i] = textures[i+1];
                } else {
                    Log.w(TAG, "Can not get alternative textures from ctKey:"+selfData);
                }
            }
        }
    }

    private static String getThemeTypeFromUri(Uri uri) {
        if (uri == null) {
            if (HtcBuildFlag.Htc_DEBUG_flag) Log.d(TAG, "Theme type uri is null", new Exception());
            return null;
        }

        if (HtcBuildFlag.Htc_DEBUG_flag) {
            if (uri.getPathSegments().size() < 2) Log.d(TAG, "Invalid theme type uri = " + uri, new Exception());
        }

        String themeType = uri.getLastPathSegment();
        if (TextUtils.isEmpty(themeType)) {
            if (HtcBuildFlag.Htc_DEBUG_flag) Log.d(TAG, "Invalid theme type = " + themeType, new Exception());
            return null;
        }

        return themeType;
    }

    private static class ThemeLogUtil {
        public static void dumpTheme(Resources.Theme theme) {
            if (theme != null)
                theme.dump(0, TAG, "");
        }

        public static void LogD(String msg) {
            LogD(msg, null);
        }

        public static void LogD(String msg, Throwable tr) {
            // open this log by following command:
            // adb shell setprop log.tag.HtcThemeUtils DEBUG
            if (Log.isLoggable(TAG, Log.DEBUG))
                Log.d(TAG, msg, tr);
        }
    }

    /*package*/ static int USERHANDLE_ALL;
    /*package*/ static int USERHANDLE_OWNER;
    /*package*/ static int USERHANDLE_CURRENT;

    static {
        try {
            Field user_all, user_owner, user_current;
            user_all = UserHandle.class.getDeclaredField("USER_ALL");
            user_owner = UserHandle.class.getDeclaredField("USER_OWNER");
            user_current = UserHandle.class.getDeclaredField("USER_CURRENT");
            USERHANDLE_ALL = user_all.getInt(user_all);
            USERHANDLE_OWNER = user_owner.getInt(user_owner);
            USERHANDLE_CURRENT = user_current.getInt(user_current);
        } catch (Exception ex) {
            // Below values are defined in file UserHandle.java
            USERHANDLE_ALL = -1;
            USERHANDLE_OWNER = 0;
            USERHANDLE_CURRENT = -2;
            Log.w(TAG, "Error while getting user handle from UserHandle: ", ex);
        }
    }

    /**
     * To reload path where theme packages are stored at.
     */
    /*package*/ static String getCurrentThemePath(boolean byUser, int userHandle) {
        return getCurrentThemePathInternal(byUser, userHandle);
    }

    private static String getCurrentThemePathInternal(boolean byUser, int userHandle) {
        if (getIfNeedReloadPath() && mAppContext != null) {
            mTargetAPKPath = getCurrentThemePackage(mAppContext, KEY_HTC_CURRENT_THEME_PATH, byUser, userHandle);
            if (TextUtils.isEmpty(mCustomAPKPath)) mCustomAPKPath = getCurrentThemePackage(mAppContext, KEY_HTC_OPTIONAL_THEME_PATH, byUser, userHandle);
            setIfNeedReloadPath(false);
        }
        return mTargetAPKPath;
    }

    /*package*/ static String getCurrentThemePath(Context context, boolean byUser, int userHandle) {
        return context==null ? null : getCurrentThemePackage(context, KEY_HTC_CURRENT_THEME_PATH, byUser, userHandle);
    }

    /*package*/ static String getHtcThemePackage(Context context, int type, boolean byUser, int userHandle) {
        return getCurrentThemePackage(context, ThemeType.getKey(type), byUser, userHandle);
    }

    private static String getCurrentThemePackage(Context context, String themeType, boolean byUser, int userHandle) {
        String value = null;

        if (context!=null && themeType!=null) {
            value = byUser ?
                    ThemeSettingUtil.getStringForUser(context, themeType, userHandle) :
                    ThemeSettingUtil.getString(context, themeType);
        }

        return value==null ? "" : value;
    }

    private static String getStringForUser(Context context, String themeType, int userHandle) {
        Log.d(TAG, "[getStringForUser] themeType=" + themeType + ", userHandle=" + userHandle);

        userHandle = userHandle == USERHANDLE_ALL ? USERHANDLE_CURRENT : userHandle;
        String retStr = SettingsCompat.SystemCompat.getStringForUser(context.getContentResolver(), themeType, userHandle);
        return retStr;
    }

    /*package*/ static boolean setHtcThemePackage(Context context, int type, String themePackage, boolean byUser, int userHandle) {
        return setCurrentThemePackageInternal(context, type, themePackage, byUser, userHandle);
    }

    private static boolean setCurrentThemePackageInternal(Context context, int type, String themePackage, boolean byUser, int userHandle) {
        boolean ret = false;
        String themeType = ThemeType.getKey(type);

        if (context!=null && themeType!=null && themePackage!=null) {
            String originalPackage = getCurrentThemePackage(context, themeType, byUser, userHandle);
            if (!themePackage.equals(originalPackage)) {
                if (byUser) ThemeSettingUtil.putStringForUser(context, themeType, themePackage, userHandle);
                else ThemeSettingUtil.putString(context, themeType, themePackage);
                ret = themePackage.equals(getCurrentThemePackage(context, themeType, byUser, userHandle));
                Log.d(TAG, "Set ["+themeType+"] from ["+originalPackage+"] to ["+themePackage+"]", new Throwable());
            }
        }

        return ret;
    }

    private static void putStringForUser(Context context, String key, String value, int userHandle) {
        Log.d(TAG, "[putStringForUser] key=" + key + ", value=" + value + ", userHandle=" + userHandle);

        userHandle = userHandle == USERHANDLE_ALL ? USERHANDLE_CURRENT : userHandle;
        SettingsCompat.SystemCompat.putStringForUser(context.getContentResolver(), key, value, userHandle);
    }

    /*package*/ static void registerThemeChangeObserver(Context context, int type, ThemeChangeObserver observer, boolean byUser, int userHandle) {
        registerThemeChangeObserverInternal(context, type, observer, byUser, userHandle);
    }

    private static void registerThemeChangeObserverInternal(Context context, int type, ThemeChangeObserver observer, boolean byUser, int userHandle) {
        if (observer == null) throw new IllegalArgumentException("The observer is null.");
        if (context == null) throw new IllegalArgumentException("The context is null.");
        if (type<0 || type>=ThemeType.getKeyCount()) throw new IllegalArgumentException("The type is illegal.");

        initTableIfNecessary(type);
        ArrayList<ThemeChangeObserver> observers = THEME_CHANGE_OBSERVERS.get(type);

        if (!observers.contains(observer)) {
            registerContentObserverIfNecessary(context, type, byUser, userHandle);
            observers.add(observer);
            Log.d(TAG,"Register " + observer  + " for type " + type);
        } else {
            Log.d(TAG,"Observer " + observer + " for type " + type + " is already registered.");
        }
    }

    private static void registerContentObserverIfNecessary(Context context, int type, boolean byUser, int userHandle) {
        if (!CONTENT_OBSERVERS_REGISTER_FLAGS.get(type)) {
            if (mThemeContentObserver == null)
                mThemeContentObserver = new ThemeKeyObserverForUsers(new Handler(context.getMainLooper()));

            if (!byUser) ThemeSettingUtil.registerContentObserver(context, ThemeType.getKey(type), mThemeContentObserver);
            else ThemeSettingUtil.registerContentObserverForUser(context, ThemeType.getKey(type), true, mThemeContentObserver, userHandle);

            CONTENT_OBSERVERS_REGISTER_FLAGS.put(type, true);

            if (type == ThemeType.HTC_THEME_CC) {
                if (!byUser) ThemeSettingUtil.registerContentObserver(context, ThemeType.getKey(ThemeType.HTC_THEME_CT), mThemeContentObserver);
                else ThemeSettingUtil.registerContentObserverForUser(context, ThemeType.getKey(ThemeType.HTC_THEME_CT), true, mThemeContentObserver, userHandle);
                CONTENT_OBSERVERS_REGISTER_FLAGS.put(ThemeType.HTC_THEME_CT, true);
            }
        }
    }

    private static void registerContentObserverForUser(Context context, Uri uri, boolean forDescendent, ContentObserver observer, int userHandle) {
        Log.d(TAG, "[registerContentObserverForUser] context=" + context + ", uri=" + uri + ", observer=" + observer + ", userHandle=" + userHandle);

        try {
            Class<?> clz = context.getContentResolver().getClass();
            Method method = clz.getMethod("registerContentObserver", Uri.class, boolean.class, ContentObserver.class, int.class);
            method.invoke(context.getContentResolver(), uri, forDescendent, observer, userHandle);
        } catch (Exception ex) {
            Log.w(TAG, "[registerContentObserverForUser] Error while registering content observer: ", ex);
        }
    }

    /* package */static void unregisterThemeChangeObserverForUser(int type, ThemeChangeObserver observer, int userHandle) {
        if (observer == null) throw new IllegalArgumentException("The observer is null.");
        if (type < 0 || type >= ThemeType.getKeyCount()) throw new IllegalArgumentException("The type is illegal.");
        if (THEME_CHANGE_OBSERVERS.indexOfKey(type) < 0) {
            Log.d(TAG, "Observer " + observer + " for type " + type + " was not registered.");
            return;
        }

        ArrayList<ThemeChangeObserver> observers = THEME_CHANGE_OBSERVERS.get(type);
        int index = observers.indexOf(observer);
        if (!(index < 0)) {
            observers.remove(index);
            Log.d(TAG, "Unregister " + observer + " for type " + type);
        } else {
            Log.d(TAG, "Observer " + observer + " for type " + type + " was not registered.");
        }
    }

    /**
     * This API is used to get a resource instance of specified APK but it is not belong to ActivityManager. <br/>
     * Please remember to update its configuration while configuration is changed to ensure the correctness.
     *
     * @param context The AP context.
     * @param apkName The name of the target apk exclusive the ".apk" extension.
     * @return The resource instance for current configuration.
     *
     * @see #updateCommonResConfiguration(Context)
     */
    /*package*/ static Resources getResources(Context context, String apkName, boolean byUser, int userHandle) {
        return getResourcesInternal(context, apkName, byUser, userHandle);
    }

    private static Resources getResourcesInternal(Context context, String apkName, boolean byUser, int userHandle) {
        if(mAppContext == null) {
            // We should handle the case when the specified apk is not for CC or CT. (initTheme() would not be invoked).
            mAppContext = context.getApplicationContext();
            setIfNeedReloadPath(true);
        }

        String themePath = ( apkName == COMMON_RES_FILE_NAME )?ThemeFileUtil.getAppsThemePath(context):getCurrentThemePathInternal(byUser, userHandle);
        ThemeLogUtil.LogD("getResourcesInternal apkName = "+ apkName+ " themePath = "+ themePath);
        return getResources(context, apkName, themePath, mCustomAPKPath);
    }



    static class MyThemeFileCopyHandler extends ThemeFileUtil.FileCallback {
        private static MyThemeFileCopyHandler s_OneCopyHandler = null;
        private ThemeFileUtil.FileCallback mUserFileCallBack = null;
        private int mCategory;
        private boolean mByUser;
        private int mUserHandle;

        public MyThemeFileCopyHandler(ThemeFileUtil.FileCallback mUserFileCallBack, int mCategory, boolean mByUser, int mUserHandle) {
            this.mUserFileCallBack = mUserFileCallBack;
            this.mCategory = mCategory;
            this.mByUser = mByUser;
            this.mUserHandle = mUserHandle;
        }

        static synchronized private void checkIfNeedCreate(Context context, int category, boolean byUser, int userHandle, boolean success) {
            if ( success && ThemeFileUtil.isAppliedThemeChanged(context, ThemeType.HTC_THEME_CT) ) {
                ThemeFileUtil.saveAppliedThemeInfo(context, ThemeType.HTC_THEME_CT);
                CommonThemeResources.setRecreateFlag(true);
            }

            CommonThemeResources.init(context, category, byUser, userHandle);
        }

        @Override
        public void onCompleted(Context context, ThemeFileUtil.ThemeFileTaskInfo result) {
            ThemeLogUtil.LogD("MyThemeFileCopyHandler onCompleted");
            boolean success = false;
            if ( null != result && result.isCopyFileSuccess() ) {
                success = true;
            } else {
                ThemeLogUtil.LogD("copy file fail " + ((null!=result)?result.getAppLocalThemePath():"") + " cost = "+ ((null!=result)?result.getTimeCost():"") );
            }

            checkIfNeedCreate(context, mCategory, mByUser, mUserHandle, success);

            if ( null != mUserFileCallBack ) {
                mUserFileCallBack.onCompleted(context, result);
            }
        }

        private void checkAndAsyncThemeFiles(Context context, boolean recreate) {
            boolean themeChanged;
            themeChanged = ThemeFileUtil.isAppliedThemeChanged(context, ThemeType.HTC_THEME_CT);
            ThemeLogUtil.LogD("MyThemeFileCopyHandler checkAndAsyncThemeFiles ThemeFileUtil.isAppliedThemeChanged(context, ThemeType.HTC_THEME_CT) = "+
                    themeChanged);
            if ( themeChanged ) {
                ThemeLogUtil.LogD("MyThemeFileCopyHandler checkAndAsyncThemeFiles");
                CommonThemeResources.init(context, mCategory, mByUser, mUserHandle);
                if ( recreate ) {
                    CommonThemeResources.setRecreateFlag(true);
                }
                ThemeFileUtil.getThemeFilesAsync(context, this, ThemeFileUtil.ThemeFile.CResources);
            } else {
                onCompleted(context, null);

                ThemeLogUtil.LogD("MyThemeFileCopyHandler Not call checkAndAsyncThemeFiles. ThemeFileUtil.isAppliedThemeChanged(context, ThemeType.HTC_THEME_CT) = "+ ThemeFileUtil.isAppliedThemeChanged(context, ThemeType.HTC_THEME_CT));
            }
        }
    }

    /* package */static void init(Context context, int category, boolean byUser, int userHandle, boolean recreate, String customPath, ThemeFileUtil.FileCallback onTextureReady) {
        mCustomAPKPath = customPath;

        if (recreate) {
            setIfNeedReloadPath(true);
            CommonCategoryResources.setRecreateFlag(true);
            CommonThemeResources.setRecreateFlag(true);
        }
        initInternal(context, category, byUser, userHandle, onTextureReady, recreate);
    }

    private static void initInternal(Context context, int category, boolean byUser, int userHandle, ThemeFileUtil.FileCallback onTextureReady, boolean recreate) {
        Log.d(TAG, "context=" + context + ", category=" + category + ", byUser=" + byUser + ", userHandle=" + userHandle);
        if (context == null) throw new IllegalArgumentException(ARGS_ERR_MSG);

        if (mAppContext == null) {
            mAppContext = context.getApplicationContext();
            ThemeLogUtil.LogD("[init] context="+context+", category="+category+", package="+mAppContext.getPackageName()+", pid="+android.os.Process.myPid());
        }

        if (mThemePathObserver==null) {
            mThemePathObserver = new ThemePathObserver(new Handler(context.getMainLooper()));
            if (byUser) {
                ThemeSettingUtil.registerContentObserverForUser(context, KEY_HTC_CURRENT_THEME_PATH, true, mThemePathObserver, userHandle);
            } else {
                ThemeSettingUtil.registerContentObserver(context, KEY_HTC_CURRENT_THEME_PATH, mThemePathObserver);
            }
            setIfNeedReloadPath(true);
        }

        if (mThemeKeyObserver==null) {
            mThemeKeyObserver = new ThemeKeyObserver(new Handler(context.getMainLooper()));
            int[] observeTypes = new int[] {ThemeType.HTC_THEME_CC, ThemeType.HTC_THEME_CT};
            for (int type : observeTypes) {
                if (byUser) {
                    ThemeSettingUtil.registerContentObserverForUser(context, ThemeType.getKey(type), true, mThemeKeyObserver, userHandle);
                } else {
                    ThemeSettingUtil.registerContentObserver(context, ThemeType.getKey(type), mThemeKeyObserver);
                }
            }
        }

        CommonCategoryResources.init(context, category, byUser, userHandle);

        MyThemeFileCopyHandler mtfch = new MyThemeFileCopyHandler(onTextureReady, category, byUser, userHandle);
        mtfch.checkAndAsyncThemeFiles(context, recreate);
        Log.d(TAG, "init done");
    }

}
