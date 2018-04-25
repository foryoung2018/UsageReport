package com.htc.lib1.theme;

import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.Executor;

/**
 * APIs for android N change and paid theme protection mechanism.
 *
 * https://hichub.htc.com/HomeLauncher/ThemeSelector/wikis/ThemeFileAccessPermissionChanged
 *
 * */

public class ThemeFileUtil {
    private static String LOG_TAG = "ThemeFileUtil";

    private static HashMap<String, ArrayList<ThemeFileUtil.ContextAndFileCallbackWrapper>> sRunningTaskAndCallbacks = new HashMap<String, ArrayList<ThemeFileUtil.ContextAndFileCallbackWrapper>>();
    /**
     * Constants for ThemeFileProvider
     * */
    private static final String AUTHORITY_OF_THEMEFILESPROVIDER = "com.htc.themepicker.themeFile";
    public static final String KEYS_FILE_NAME = "KEYS_FILE_NAME";
    public static final String COMMAND_GET_FDS = "COMMAND_GET_FDS";
    public static final String RESPONSE_KEYS_FDS = "RESPONSE_KEYS_FDS";

    /**
     * Constants for detecting theme changed.
     * */
    public static final String KEY_SP_THEME_FILE_INFO = "KEY_SP_THEME_FILE_INFO";
    public static final String KEY_SP_NAME_PREFIX_PER_THEME_TYPE_ = "KEY_SP_NAME_PREFIX_PER_THEME_TYPE_";

    private static String CUSTOMIZED_PATH_PREFIX = "";
    private static String CUSTOMIZED_SHARED_PREFERENCE_PREFIX = "";

    public static void setCustomizedPrefix(String prefix) {
        CUSTOMIZED_PATH_PREFIX = prefix;
        CUSTOMIZED_SHARED_PREFERENCE_PREFIX = prefix;
    }

    public static void setCustomizedPathPrefix(String prefix) {
        CUSTOMIZED_PATH_PREFIX = prefix;
    }

    public static void setCustomizedSharedPreferencePrefix(String prefix) {
        CUSTOMIZED_SHARED_PREFERENCE_PREFIX = prefix;
    }

    /**
     * Identify the theme concept term with actual file names
     * The enum type is as a parameter for requesting to copy theme files which the client app cares.
     * */
    public enum ThemeFile {
        //common color themes
        CBaseline(ThemeType.HTC_THEME_CC, "CBaseline.apk"),
        CCategoryOne(ThemeType.HTC_THEME_CC, "CCategoryOne.apk"),
        CCategoryTwo(ThemeType.HTC_THEME_CC,"CCategoryTwo.apk"),
        CCategoryThree(ThemeType.HTC_THEME_CC,"CCategoryThree.apk"),
        //common texture theme
        CResources(ThemeType.HTC_THEME_CT,"CResources.apk", "CResources/action_bar_bkg.png", "CResources/status_bar_bkg.png", "CResources/tab_bar_bkg.png"),
        //launcher icon
        Icons(ThemeType.HTC_THEME_ICON_SET, "Icons.apk"),
        //default contact/message portrait image
        Avatar(ThemeType.HTC_THEME_AVATAR, "Avatar.apk"),
        //color for dialer
        PhoneDialer(ThemeType.HTC_THEME_DIALER, "PhoneDialer.apk"),
        //wallpapers
        WallpaperAllapps(ThemeType.HTC_THEME_WALLPAPER_ALLAPPS, "wallpaper/allapps.jpg"),
        WallpaperLockscreen(ThemeType.HTC_THEME_WALLPAPER_LOCKSCREEN, "wallpaper/lockscreen.jpg"),
        WallpaperMessage(ThemeType.HTC_THEME_WALLPAPER_MESSAGE, "wallpaper/message.jpg"),
        //IME, Sense7 supports only
        IME(ThemeType.HTC_THEME_IME, "IME.apk"),
        //Weather assets. Masthead on blinkfeed, WeatherClock widget on home, Masthead on lockscreen, reminder views (alarm, dialer, calendar)
        WeatherClock(ThemeType.HTC_THEME_WEATHER_CLOCK, "WeatherClock.apk"),
        //Dotview, Sense7 supports only
        Dotview(ThemeType.HTC_THEME_DOTVIEW, "DotView.apk"),
        //SystemUI
        Navigation(ThemeType.HTC_THEME_NAVIGATION, "NavigationKey.apk"),
        //Message theme
        Message(ThemeType.HTC_THEME_MESSAGE, "Message.apk");

        public final String[] name;
        public final int themeType;
        ThemeFile(int themeType, String... name) {
            this.themeType = themeType;
            this.name = name;
        }
    }

    /**
     * Helper class for communicating with ThemeFileProvider via IPC.
     * Each ThemeFileTaskFd is associated with single ThemeFile. It means that when client request a ThemeFile, ThemeFileProvider will response a ThemeFileTaskFd.
     * The length variable is the value of requesting ThemeFile.name.length
     * */
    public static class ThemeFileTaskFd implements Parcelable {

        public String[] fileName;
        public ParcelFileDescriptor[] fds;
        public int length;

        public ThemeFileTaskFd(int length) {
            this.length = length;
            fileName = new String[length];
            fds = new ParcelFileDescriptor[length];
        }

        public ThemeFileTaskFd(Parcel in) {
            length = in.readInt();
            fileName = new String[length];
            in.readStringArray(fileName);
            fds = in.createTypedArray(ParcelFileDescriptor.CREATOR);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(length);
            dest.writeStringArray(fileName);
            dest.writeTypedArray(fds, flags);
        }

        public final static Parcelable.Creator<ThemeFileTaskFd> CREATOR = new Parcelable.Creator<ThemeFileTaskFd>() {
            public ThemeFileTaskFd createFromParcel(Parcel in) {
                return new ThemeFileTaskFd(in);
            }

            public ThemeFileTaskFd[] newArray(int size) {
                return new ThemeFileTaskFd[size];
            }
        };
    }

    /**
     * Response info of copying theme file from ThemeFileProvider
     * */
    public static class ThemeFileTaskInfo {
        boolean copyFileSuccess;
        String path;
        ThemeFile[] theFilestoCopy;
        long timeCost;

        /**
         * The theme path of each client app. It is supposed to be /data/data/"${packageName}"/files/.htc_theme"
         * */
        public String getAppLocalThemePath () {
            return path;
        }
        public ThemeFile[] getFileToCopy () {
            return theFilestoCopy;
        }
        public boolean isCopyFileSuccess () {return  copyFileSuccess;}
        public long getTimeCost () {return  timeCost;}
    }

    /**
     * Callback when copying theme file completed for async api.
     */
    public abstract static class FileCallback {
        public void onCompleted(Context context, ThemeFileTaskInfo result) {};

        @Deprecated
        public void onCanceled(Context context, ThemeFileTaskInfo result) {};
    }

    public static class ContextAndFileCallbackWrapper {
        WeakReference<Context> contextWeakReference;
        FileCallback mfileCallback;

        public ContextAndFileCallbackWrapper(Context context, FileCallback fileCallback) {
            contextWeakReference = new WeakReference<Context>(context);
            mfileCallback = fileCallback;
        }

        boolean hasWrappedContext() {
            return getWrappedContext() != null;
        }

        boolean hasWrappedFileCallback() {
            return mfileCallback != null;
        }

        Context getWrappedContext() {
            return contextWeakReference == null ? null : contextWeakReference.get();
        }

        FileCallback getWrappedFileCallback() {
            return mfileCallback;
        }
    }

    /**
     * The theme path of each client app. It is supposed to be /data/data/"${packageName}"/files/.htc_theme"
     * */
    public static String getAppsThemePath(Context context) {
        try {
            if (context == null) {
                ThemeSettingUtil.logw(LOG_TAG, "empty current theme path");
                return "";
            }
            return context.getFilesDir().toString() + File.separator + ThemeType.THEME_CURRENT_THEME_FOLDER_NAME + CUSTOMIZED_PATH_PREFIX + File.separator;
        } catch (Exception e) {
            ThemeSettingUtil.logd(LOG_TAG, "cannot get AppsThemePath %s", e.getMessage());
        }
        return "";
    }

    /**
     * Support multiple user version to get the theme path of each client app.
     *
     * If no more request from app, we don't plan to complete it.
     * */
    public static String getAppsThemePath(Context context, int userHandle) {
        return getAppsThemePath(context);
    }

    /** The different order of nameList will be viewed as different key
     */
    private static String composeAsyncTaskId(ThemeFile... nameList) {
        if (nameList == null)
            return "";

        StringBuilder sb = new StringBuilder();
        int length = nameList.length;
        for (int i = 0; i < length; ++i) {
            if (nameList[i] != null)
                sb.append(nameList[i].name[0]);
        }
        ThemeSettingUtil.logd(LOG_TAG, "composeAsyncTaskId %s", sb.toString());
        return sb.toString();
    }

    /**
     * Async APIs for copying theme file
     *
     * @param context context
     * @param callback When the AsyncTask is completed, it will fire the callback.
     * @param nameLists The files to copy from ThemeFileProvider
     * **/
    public static void getThemeFilesAsync(final Context context, final FileCallback callback, ThemeFile... nameLists) {
        getThemeFilesAsync(context, callback, null, nameLists);
    }

    /**
     * Async APIs for copying theme file
     *
     * @param context context
     * @param callback When the AsyncTask is completed, it will fire the callback.
     * @param executor Client app can assign the executor for handling the AsyncTask.Null means the default executor.
     * @param nameLists The files to copy from ThemeFileProvider
     * **/
    public static void getThemeFilesAsync(final Context context, final FileCallback callback, Executor executor, final ThemeFile... nameLists) {
        ThemeFileTaskInfo params = new ThemeFileTaskInfo();
        params.theFilestoCopy = nameLists;
        params.timeCost = System.currentTimeMillis();

        if (isPathSame(getAppsThemePath(context), ThemeSettingUtil.getString(context, ThemeType.KEY_APP_CURRENT_THEME_PATH))) {
            ThemeSettingUtil.logd(LOG_TAG, "getThemeFiles but same src dst path");
            return;
        }

        final String key = composeAsyncTaskId(nameLists);

        //we don't allow multiple tasks to request same files are running.
        synchronized (sRunningTaskAndCallbacks) {
            ArrayList<ContextAndFileCallbackWrapper> list = sRunningTaskAndCallbacks.get(key);
            if (list != null) {
                ContextAndFileCallbackWrapper wrapper = new ContextAndFileCallbackWrapper(context, callback);
                list.add(wrapper);
                ThemeSettingUtil.logd(LOG_TAG, "getThemeFilesAsync duplicate task %s, size %s", key, list.size());
                return;
            } else {
                list = new ArrayList<ContextAndFileCallbackWrapper>();
                ContextAndFileCallbackWrapper wrapper = new ContextAndFileCallbackWrapper(context, callback);
                list.add(wrapper);
                sRunningTaskAndCallbacks.put(key, list);
            }
        }

        AsyncTask<ThemeFileTaskInfo, Void, ThemeFileTaskInfo> task = new AsyncTask<ThemeFileTaskInfo, Void, ThemeFileTaskInfo>() {
            @Override
            protected void onPreExecute() {
            }

            @Override
            protected ThemeFileTaskInfo doInBackground(ThemeFileTaskInfo... params) {
                ThemeFileTaskInfo result = getThemeFiles(context, params[0], params[0].theFilestoCopy);
                return result;
            }

            @Override
            protected void onPostExecute(ThemeFileTaskInfo result) {
                result.timeCost = System.currentTimeMillis() - result.timeCost;

                ArrayList<ContextAndFileCallbackWrapper> list;
                synchronized (sRunningTaskAndCallbacks) {
                    list = sRunningTaskAndCallbacks.remove(key);
                }

                if (list == null) {
                    ThemeSettingUtil.logd(LOG_TAG, "no callback found %s", key);
                    return;
                }

                for (ContextAndFileCallbackWrapper wrapper : list) {
                    if (wrapper != null) {
                        Context contextOnComplete = wrapper.hasWrappedContext() ? wrapper.getWrappedContext() : context;
                        FileCallback fileCallbackOnComplete = wrapper.getWrappedFileCallback();
                        ThemeSettingUtil.logd(LOG_TAG, "onComplete callback %s, context %b %s, cbk %s", key, wrapper.hasWrappedContext(), contextOnComplete, fileCallbackOnComplete);
                        if (fileCallbackOnComplete != null) {
                            fileCallbackOnComplete.onCompleted(contextOnComplete, result);
                        }
                    }
                }
            }
        };

        if (executor != null) {
            task.executeOnExecutor(executor, params);
        } else {
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
        }
    }

    /**
     * Sync APIs for copying theme file.
     * Please don't call it in main thread.
     *
     * @param context context
     * @param nameLists The files to copy from ThemeFileProvider
     * */
    public static ThemeFileTaskInfo getThemeFiles(Context context, ThemeFile... nameLists) {
        ThemeFileTaskInfo result = new ThemeFileTaskInfo();
        result.timeCost = System.currentTimeMillis();
        getThemeFiles(context, result, nameLists);
        result.timeCost = System.currentTimeMillis() - result.timeCost;
        return result;
    }

    private static ThemeFileTaskInfo getThemeFiles(Context context, ThemeFileTaskInfo result, ThemeFile... nameLists) {
        result.path = getAppsThemePath(context);
        result.theFilestoCopy = nameLists;

        if (isPathSame(result.path, ThemeSettingUtil.getString(context, ThemeType.KEY_APP_CURRENT_THEME_PATH))) {
            ThemeSettingUtil.logd(LOG_TAG, "getThemeFiles but same src dst path");
            return result;
        }

        try {
            result.copyFileSuccess = copyFileToLocalPath(context, result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private static boolean copyFileToLocalPath(Context context, ThemeFileTaskInfo info) throws IOException {
        String appThemePath = info.path;
        ThemeFile[] theFilestoCopy = info.theFilestoCopy;

        int theFilestoCopyLength = theFilestoCopy == null ? 0 : theFilestoCopy.length;
        ThemeSettingUtil.logd(LOG_TAG, "copyFileToLocalPath + %d, %s", theFilestoCopyLength, appThemePath);

        for (int i = 0; i < theFilestoCopyLength; ++i) {
            ThemeSettingUtil.logd(LOG_TAG, "theFilestoCopy %s", theFilestoCopy[i]);
            for (int j = 0; j < theFilestoCopy[i].name.length; ++j) {
                ThemeFileInnerHelper.forceDelete(appThemePath, theFilestoCopy[i].name[j]);
            }

        }

        Bundle callResponse = null;
        boolean bSuccess = true;
        try {

            if (theFilestoCopyLength <= 0 || context == null) {
                ThemeSettingUtil.logw(LOG_TAG, "theFilestoCopy %d, context %s", theFilestoCopyLength, context);
                return false;
            }

            ContentResolver resolver = context.getContentResolver();
            callResponse = getThemeFileFds(resolver, theFilestoCopy);

            if (callResponse != null) {
                callResponse.setClassLoader(ThemeFileTaskFd.class.getClassLoader());
                Parcelable[] themeFDs = callResponse.getParcelableArray(RESPONSE_KEYS_FDS);
                int themeFDsLength = themeFDs == null ? 0 : themeFDs.length;

                ThemeSettingUtil.logd(LOG_TAG, "copy response fd length %s", themeFDsLength);

                for (int i = 0; i < themeFDsLength; ++i) {
                    if (themeFDs[i] instanceof ThemeFileTaskFd) {
                        ThemeFileTaskFd themeFileFd = (ThemeFileTaskFd) themeFDs[i];
                        int themeFDsFileNameLength = themeFileFd.fileName.length;
                        for (int j = 0; j < themeFDsFileNameLength; ++j) {
                            if (themeFileFd.fds[j] instanceof ParcelFileDescriptor) {
                                ParcelFileDescriptor.AutoCloseInputStream fis = null;
                                try {
                                    fis = new ParcelFileDescriptor.AutoCloseInputStream(themeFileFd.fds[j]);
                                    File dstTmp = new File(appThemePath, themeFileFd.fileName[j] + UUID.randomUUID().toString() + "tmp");
                                    File dst = new File(appThemePath, themeFileFd.fileName[j]);
                                    bSuccess &= ThemeFileInnerHelper.copyToFile(fis, dstTmp, dst);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    ThemeSettingUtil.logd(LOG_TAG, "copy exception %s, %s", themeFileFd.fileName[j], e.getMessage());
                                    bSuccess = false;
                                } finally {
                                    if (fis != null)
                                        fis.close();
                                }
                            } else {
                                ThemeSettingUtil.logw(LOG_TAG, "themeFileFd.fds[%d] %s", j, themeFileFd.fds[j]);
                            }
                        }
                    } else {
                        ThemeSettingUtil.logw(LOG_TAG, "themeFDs[%d] %s", i, themeFDs[i]);
                    }

                }

            } else {
                ThemeSettingUtil.logw(LOG_TAG, "getThemeFileFds is null");
                for (int i = 0; i < theFilestoCopyLength; ++i) {
                    String currentThemePath = ThemeSettingUtil.getString(context, ThemeType.KEY_APP_CURRENT_THEME_PATH);
                    for (int j = 0; j < theFilestoCopy[i].name.length; ++j) {
                        bSuccess &= ThemeFileInnerHelper.copyToFile(new File(currentThemePath, theFilestoCopy[i].name[j]), new File(appThemePath, theFilestoCopy[i].name[j] + UUID.randomUUID().toString() + "tmp"), new File(appThemePath, theFilestoCopy[i].name[j]));
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            ThemeSettingUtil.logw(LOG_TAG, "Exception at copyFileToLocalPath %s", e.getMessage());
            bSuccess = false;
        }


        ThemeSettingUtil.logd(LOG_TAG, "copyFileToLocalPath- bSuccess %b", bSuccess);
        return bSuccess;
    }



    private static Bundle getThemeFileFds(ContentResolver resolver, ThemeFile[] children) {
        Bundle result = null;
        ContentProviderClient cp = null;
        try {
            Bundle extras = new Bundle();
            extras.putSerializable(KEYS_FILE_NAME, children);

            cp = resolver.acquireUnstableContentProviderClient(AUTHORITY_OF_THEMEFILESPROVIDER);
            result = cp.call(COMMAND_GET_FDS, null, extras);
        } catch (Exception e) {
            ThemeSettingUtil.logw(LOG_TAG, "getFDsFromCP %s", e);
            return result;
        } finally {
            if (cp != null) {
                cp.release();
            }
        }
        return result;
    }


    private static void deleteThemeFiles(Context context) {
        ThemeFileInnerHelper.deleteFolderFile(getAppsThemePath(context), false);
    }



    /**
     * Below apis are for detecting the applied theme has been changed since last saveAppliedThemeInfo(context, themeType)
     * */

    /**
     * To save current applied theme info to shared preference based on themeType which client app cares.
     * @param context context
     * @param themeType Must be one of the ThemeType, such as ThemeType.HTC_THEME_FULL, ThemeType.HTC_THEME_CC, HTC_THEME_WEATHER_CLOCK and so on.
     * */
    public static void saveAppliedThemeInfo(Context context, int themeType) {
        saveAppliedThemeInfo(context, themeType, ThemeSettings.myUserId());
    }

    /**
     * To save current applied theme info to shared preference based on themeType which client app cares.
     * @param context context
     * @param themeType Must be one of the ThemeType, such as ThemeType.HTC_THEME_FULL, ThemeType.HTC_THEME_CC, HTC_THEME_WEATHER_CLOCK and so on.
     * @param userHandle for multiple user case.
     * */
    public static void saveAppliedThemeInfo(Context context, int themeType, int userHandle) {
        try {
            ThemeSettingUtil.logd(LOG_TAG, "saveLastAppliedThemeInfo +");
            SharedPreferences prefs = context.getSharedPreferences(KEY_SP_THEME_FILE_INFO, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();

            String SP_NAME = KEY_SP_NAME_PREFIX_PER_THEME_TYPE_ + CUSTOMIZED_SHARED_PREFERENCE_PREFIX + ThemeType.getKey(themeType);
            String info = getCurrentAppliedThemeInfo(context, themeType, userHandle);

            editor.putString(SP_NAME, info);
            editor.commit();
            ThemeSettingUtil.logd(LOG_TAG, "saveLastAppliedThemeInfo - %s type %s, %s, %s", CUSTOMIZED_SHARED_PREFERENCE_PREFIX, themeType, SP_NAME, info);
        } catch (Exception e) {
            ThemeSettingUtil.logd(LOG_TAG, "cannot get sharedpreference %s", e.getMessage());
        }
    }

    /**
     * To clear current applied theme info based on themeType which client app cares.
     * @param context context
     * @param themeType Must be one of the ThemeType, such as ThemeType.HTC_THEME_FULL, ThemeType.HTC_THEME_CC, HTC_THEME_WEATHER_CLOCK and so on.
     * */
    public static void clearAppliedThemeInfo(Context context, int themeType) {
        clearAppliedThemeInfo(context, themeType, ThemeSettings.myUserId());
    }

    /**
     * To clear current applied theme info based on themeType which client app cares.
     * @param context context
     * @param themeType Must be one of the ThemeType, such as ThemeType.HTC_THEME_FULL, ThemeType.HTC_THEME_CC, HTC_THEME_WEATHER_CLOCK and so on.
     * @param userHandle for multiple user case.
     * */
    public static void clearAppliedThemeInfo(Context context, int themeType, int userHandle) {
        try {
            ThemeSettingUtil.logd(LOG_TAG, "clearAppliedThemeInfo +");
            SharedPreferences prefs = context.getSharedPreferences(KEY_SP_THEME_FILE_INFO, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();

            String SP_NAME = KEY_SP_NAME_PREFIX_PER_THEME_TYPE_ + CUSTOMIZED_SHARED_PREFERENCE_PREFIX + ThemeType.getKey(themeType);

            editor.remove(SP_NAME);
            editor.commit();
            ThemeSettingUtil.logd(LOG_TAG, "clearAppliedThemeInfo - %s type %s, %s", CUSTOMIZED_SHARED_PREFERENCE_PREFIX, themeType, SP_NAME);
        } catch (Exception e) {
            ThemeSettingUtil.logd(LOG_TAG, "cannot get sharedpreference %s", e.getMessage());
        }
    }

    /**
     * To judge if theme has been changed since last time the client app saved theme info.
     * @param context context
     * @param themeType Must be one of the ThemeType, such as ThemeType.HTC_THEME_FULL, ThemeType.HTC_THEME_CC, HTC_THEME_WEATHER_CLOCK and so on.
     * @return true means that the theme for given themeType has been changed. Client app should update the theme file from ThemeFileProvider; false means no need to update theme file.
     * */
    public static boolean isAppliedThemeChanged(Context context, int themeType) {
        return isAppliedThemeChanged(context, themeType, ThemeSettings.myUserId());
    }

    /**
     * To judge if theme has been changed since last time the client app saved theme info.
     * @param context context
     * @param themeType Must be one of the ThemeType, such as ThemeType.HTC_THEME_CC, HTC_THEME_WEATHER_CLOCK and so on.
     * @param userHandle for multiple user case.
     * @return true means that the theme for given themeType has been changed. Client app should update the theme file from ThemeFileProvider; false means no need to update theme file.
     * */
    public static boolean isAppliedThemeChanged(Context context, int themeType, int userHandle) {
        boolean result = false;

        String info = getCurrentAppliedThemeInfo(context, themeType, userHandle);
        String lastInfo = getSprefAppliedThemeInfo(context, themeType);

        //We use null as exception case that app may not get permission to access current theme info or shared preference.
        if (lastInfo != null && info != null && !TextUtils.equals(info, lastInfo)) {
            result = true;
        }

        ThemeSettingUtil.logd(LOG_TAG, "isAppliedThemeChanged - %b, type %s, %s, %s", result, themeType, info, lastInfo);
        return result;
    }

    /**
     * To get the applied theme info from shared preference.
     * @param context context
     * @param themeType Must be one of the ThemeType, such as ThemeType.HTC_THEME_FULL, ThemeType.HTC_THEME_CC, HTC_THEME_WEATHER_CLOCK and so on.
     * @return the saved theme info from shared preference for comparing.
     * */
    public static String getSprefAppliedThemeInfo (Context context, int themeType) {
        try {
            String SP_NAME = KEY_SP_NAME_PREFIX_PER_THEME_TYPE_ + CUSTOMIZED_SHARED_PREFERENCE_PREFIX + ThemeType.getKey(themeType);
            SharedPreferences prefs = context.getSharedPreferences(KEY_SP_THEME_FILE_INFO, Context.MODE_PRIVATE);
            String lastInfo = prefs.getString(SP_NAME, "");
            ThemeSettingUtil.logd(LOG_TAG, "getSprefAppliedThemeInfo - %s themeType %s, %s", CUSTOMIZED_SHARED_PREFERENCE_PREFIX, themeType, lastInfo);

            return lastInfo;
        }  catch (Exception e) {
            ThemeSettingUtil.logd(LOG_TAG, "cannot get sharedpreference %s", e.getMessage());
        }
        return null;
    }

    /**
     * To get the applied theme info from current theme setting.
     * @param context context
     * @param themeType Must be one of the ThemeType, such as ThemeType.HTC_THEME_FULL, ThemeType.HTC_THEME_CC, HTC_THEME_WEATHER_CLOCK and so on.
     * @return the current applied theme info for comparing.
     * */
    public static String getCurrentAppliedThemeInfo (Context context, int themeType, int userHandle) {
        try {
            ThemeType.ThemeValue value = userHandle == ThemeSettings.myUserId() ? ThemeType.getValue(context, themeType) : ThemeType.getValue(context, themeType, userHandle);
            String currentInfo = value.themeId == null && value.themeUpdateTime == null && value.time == null ? null : value.themeId + value.themeUpdateTime + value.time;
            ThemeSettingUtil.logd(LOG_TAG, "getCurrentAppliedThemeInfo - type %s, %s", themeType, currentInfo);

            return currentInfo;
        }  catch (Exception e) {
            ThemeSettingUtil.logd(LOG_TAG, "cannot get currentAppliedThemeInfo %s", e.getMessage());
        }
        return null;
    }

    private static boolean isPathSame(String path1, String path2){
        boolean equal = false;
        try {
            File pathFile1 = new File(path1);
            File pathFile2 = new File(path2);
            equal = pathFile1.getCanonicalFile().equals(pathFile2.getCanonicalFile());//pathFile1.equals(pathFile2);
            ThemeSettingUtil.logd(LOG_TAG, "isPathSame + %b %s, %s", equal, pathFile1.getCanonicalFile(), pathFile2.getCanonicalFile());
        } catch (Exception e) {
        }
        ThemeSettingUtil.logd(LOG_TAG, "isPathSame - %b %s, %s", equal, path1, path2);
        return equal;
    }
}
