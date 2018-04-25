package com.htc.lib1.theme;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;

import com.htc.lib0.htcdebugflag.HtcWrapHtcDebugFlag;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ThemeType {
    private static String LOG_TAG = "ThemeType";
    private static boolean securedLOGD = HtcWrapHtcDebugFlag.Htc_SECURITY_DEBUG_flag;
    public static boolean DEBUG = securedLOGD && (BuildConfig.DEBUG || HtcWrapHtcDebugFlag.Htc_DEBUG_flag);

    /**
     * The full theme type
     */
    public final static int HTC_THEME_FULL = 0;
    /**
     * The color theme type
     */
    public final static int HTC_THEME_CC = 1;
    /**
     * The icon set theme type
     */
    public final static int HTC_THEME_ICON_SET = 2;
    /**
     * The all-apps wallpaper theme type
     */
    public final static int HTC_THEME_WALLPAPER_ALLAPPS = 3;
    /**
     * The lockscreen wallpaper theme type
     */
    public final static int HTC_THEME_WALLPAPER_LOCKSCREEN = 4;
    /**
     * The message wallpaper theme type
     */
    public final static int HTC_THEME_WALLPAPER_MESSAGE = 5;
    /**
     * The dotview wallpaper theme type
     */
    public final static int HTC_THEME_WALLPAPER_DOTVIEW = 6;
    /**
     * The dotview theme type
     */
    public final static int HTC_THEME_DOTVIEW = 7;
    /**
     * The common texture theme type
     */
    public final static int HTC_THEME_CT = 8;
    /**
     * The dialer theme type
     */
    public final static int HTC_THEME_DIALER = 9;

    /**
     * The home wallpaper themes (custom/dynamic/multiple/single) since sense 8
     */
    public final static int HTC_THEME_WALLPAPER_HOME = 10;

    /**
     * The weather clock widget themes since sense 8
     */
    public final static int HTC_THEME_WEATHER_CLOCK = 11;

    /**
     * The avatar themes for N porting, specify a field to record Avatar.apk is changed or not. Be used in ThemeFileUtil.isAppliedThemeChanged
     */
    public final static int HTC_THEME_AVATAR = 12;

    /**
     * The avatar themes for N porting, specify a field to record IME.apk is changed or not. Be used in ThemeFileUtil.isAppliedThemeChanged
     */
    public final static int HTC_THEME_IME = 13;

    /**
     * The navigation bar themes for N porting, specify a field to record NavigationKey.apk is changed or not. Be used in ThemeFileUtil.isAppliedThemeChanged
     */
    public final static int HTC_THEME_NAVIGATION = 14;

    /**
     * The message themes for N porting, specify a field to record NavigationKey.apk is changed or not. Be used in ThemeFileUtil.isAppliedThemeChanged
     */
    public final static int HTC_THEME_MESSAGE = 15;

    /**
     * The sound ringtone themes for mixing theme, specify a field to record ringtone information.
     */
    public final static int HTC_THEME_SOUND_RINGTONE = 16;

    /**
     * The sound notification themes for mixing theme, specify a field to record notification information.
     */
    public final static int HTC_THEME_SOUND_NOTIFICATION = 17;

    /**
     * The sound alarm themes for mixing theme, specify a field to record alarm information.
     */
    public final static int HTC_THEME_SOUND_ALARM = 18;

    /**
     * The font themes for mixing theme, specify a field to record font information.
     */
    public final static int HTC_THEME_FONT = 19;

    private final static SparseArray<String> THEME_KEY_TABLE;

    public static final String THEME_CURRENT_THEME_FOLDER_NAME = ".htc_theme";

    public static final String KEY_APP_CURRENT_THEME_PATH = "htc_current_theme";
    public static final String KEY_SYS_CURRENT_THEME_PATH = "htc_current_theme_sys";

    private static final ThemeTypeValueParser s_OldFormatParser;
    private static final ThemeTypeValueParser s_JSONV1Parser;
    private static List<ThemeTypeValueParser> sParsers = null;

    static {
        THEME_KEY_TABLE = new SparseArray<String>();
        addSettingsProviderKey();

        s_OldFormatParser = new ParserOldFashion();
        s_JSONV1Parser = new ParserJSONV1();

        sParsers = new ArrayList<ThemeTypeValueParser>();
        //!!!!!  New parser must be added in HEAD of list.!!!!!
        sParsers.add(s_JSONV1Parser);
        sParsers.add(s_OldFormatParser);
    }

    public static String getKey(int type) {
        return THEME_KEY_TABLE.get(type);
    }

    public static int getKeyCount() {
        return THEME_KEY_TABLE.size();
    }

    private static void addSettingsProviderKey() {
        THEME_KEY_TABLE.put(HTC_THEME_FULL, "htc_theme_full");
        THEME_KEY_TABLE.put(HTC_THEME_CC, "htc_theme_cc");
        THEME_KEY_TABLE.put(HTC_THEME_ICON_SET, "htc_theme_icon");
        THEME_KEY_TABLE.put(HTC_THEME_WALLPAPER_ALLAPPS, "htc_theme_wallpaper_allapps");
        THEME_KEY_TABLE.put(HTC_THEME_WALLPAPER_LOCKSCREEN, "htc_theme_wallpaper_lockscreen");
        THEME_KEY_TABLE.put(HTC_THEME_WALLPAPER_MESSAGE, "htc_theme_wallpaper_message");
        THEME_KEY_TABLE.put(HTC_THEME_WALLPAPER_DOTVIEW, "htc_theme_wallpaper_dotview");
        THEME_KEY_TABLE.put(HTC_THEME_DOTVIEW, "htc_theme_dotview");
        THEME_KEY_TABLE.put(HTC_THEME_CT, "htc_theme_ct");
        THEME_KEY_TABLE.put(HTC_THEME_DIALER, "htc_theme_dialer");
        THEME_KEY_TABLE.put(HTC_THEME_WALLPAPER_HOME, "htc_theme_wallpaper_home");
        THEME_KEY_TABLE.put(HTC_THEME_WEATHER_CLOCK, "htc_theme_weather_clock");
        THEME_KEY_TABLE.put(HTC_THEME_AVATAR, "htc_theme_avatar");
        THEME_KEY_TABLE.put(HTC_THEME_IME, "htc_theme_ime");
        THEME_KEY_TABLE.put(HTC_THEME_NAVIGATION, "htc_theme_navi");
        THEME_KEY_TABLE.put(HTC_THEME_MESSAGE, "htc_theme_message");
        THEME_KEY_TABLE.put(HTC_THEME_SOUND_RINGTONE, "htc_theme_sound_ringtone");
        THEME_KEY_TABLE.put(HTC_THEME_SOUND_NOTIFICATION, "htc_theme_sound_notification");
        THEME_KEY_TABLE.put(HTC_THEME_SOUND_ALARM, "htc_theme_sound_alarm");
        THEME_KEY_TABLE.put(HTC_THEME_FONT, "htc_theme_font");
    }

    private static boolean needBackwardCompatible(int themeTypeId) {
        //The theme type value need backward compatible
        boolean need = false;
        switch (themeTypeId) {
            case HTC_THEME_FULL:
            case HTC_THEME_CC:
            case HTC_THEME_WALLPAPER_LOCKSCREEN:
            case HTC_THEME_WALLPAPER_MESSAGE:
            case HTC_THEME_WALLPAPER_DOTVIEW:
            case HTC_THEME_DOTVIEW:
            case HTC_THEME_CT:
            case HTC_THEME_DIALER:
            case HTC_THEME_WEATHER_CLOCK:
            case HTC_THEME_AVATAR:
            case HTC_THEME_IME:
            case HTC_THEME_NAVIGATION:
            case HTC_THEME_MESSAGE:
                need = true;
                break;
            default:
                break;
        }
        return need;
    }

    public static class ValueTag {
        public final static String KEY_SEPARATOR = ":";
        public final static String VALUE_SEPARATOR = "=";
        public final static String SELFDATA_SEPARATOR = "#";

        public final static String SELF_DATA = "selfData";
        public final static String THEME_ID = "themeId";
        public final static String THEME_TITLE = "themeTitle";
        public final static String TIME = "time";
        public final static String WAIT = "wait";
        public final static String IS_FILE = "isFile";
        public final static String THEME_UPDATE_TIME = "themeUpdateTime";
        public final static String SELLABLE = "sellable";
        public final static String MATERIAL_TYPE = "materialType";

        public final static String FORMAT = "format";

        public final static String PRE_STRING_THEME_TITLE = ValueTag.KEY_SEPARATOR + ValueTag.THEME_TITLE + ValueTag.VALUE_SEPARATOR;
        public final static String POST_STRING_THEME_TITLE = ValueTag.KEY_SEPARATOR + ValueTag.TIME + ValueTag.VALUE_SEPARATOR;
    }

    public static class ThemeValue {
        public String selfData = null;

        public String themeId = null;
        public String themeTitle = null;
        public String time = null;
        public boolean wait = false;
        public boolean isFile = false;
        public String themeUpdateTime = null;
        public boolean sellable = false;
        public String materialType = null;

        public ThemeValue() {

        }
    }

    public static ThemeValue getValue(Context context, int type) {
        return getValue(context, type, false);
    }

    public static ThemeValue getValue(Context context, int type, int userHandle) {
        return getValue(context, type, userHandle, false);
    }

    public static ThemeValue getValue(Context context, int type, boolean forceGetValue) {
        String themeType = getKey(type), srcValue = null;

        if (context != null && themeType != null)
            srcValue = ThemeSettingUtil.getString(context, themeType, forceGetValue);
        return parseThemeValue(srcValue);
    }

    public static ThemeValue getValue(Context context, int type, int userHandle, boolean forceGetValue) {
        String themeType = getKey(type), srcValue = null;

        if (context != null && themeType != null) {
            try {
                srcValue = ThemeSettingUtil.getStringForUser(context, themeType, userHandle, forceGetValue);
            } catch (Exception ex) {
                ex.printStackTrace();
                Log.w(LOG_TAG, "Exception occurs. fallback");
                srcValue = ThemeSettingUtil.getString(context, themeType);
            }
        }

        return parseThemeValue(srcValue);
    }

    private static ThemeValue parseThemeValue(String src) {
        ThemeTypeValueParser parser = findHandleParser(src);
        return parser.parseThemeValue(src);
    }

    @Deprecated
    public static String genThemeValue(String strSelfData, String strThemeId, String strThemeTitle,
                                       String strTimeStamp,
                                       boolean isWait, boolean isFile) {
        return genThemeValue(strSelfData, strThemeId, strThemeTitle, strTimeStamp, isWait, isFile, null);
    }

    @Deprecated
    public static String genThemeValue(String strSelfData, String strThemeId, String strThemeTitle,
                                       String strTimeStamp,
                                       boolean isWait, boolean isFile, String themeUpdateTime) {
        return genThemeValue(strSelfData, strThemeId, strThemeTitle, strTimeStamp, isWait, isFile, themeUpdateTime, false, null);
    }

    @Deprecated
    public static String genThemeValue(String strSelfData, String strThemeId, String strThemeTitle,
                                       String strTimeStamp,
                                       boolean isWait, boolean isFile, String themeUpdateTime, boolean isSellable, String strMaterialType) {
        //force use old format to gen theme value
        return s_OldFormatParser.genValue(strSelfData, strThemeId, strThemeTitle, strTimeStamp, isWait, isFile, themeUpdateTime, isSellable, strMaterialType);
    }

    public static String genThemeValue(int themeTypeId, String strSelfData, String strThemeId, String strThemeTitle,
                                       String strTimeStamp,
                                       boolean isWait, boolean isFile, String themeUpdateTime, boolean isSellable, String strMaterialType) {
        boolean needBackwardCompatible = needBackwardCompatible(themeTypeId);

        if(needBackwardCompatible) {
            return genThemeValue(strSelfData, strThemeId, strThemeTitle, strTimeStamp, isWait, isFile, themeUpdateTime, isSellable, strMaterialType);
        }

        ThemeTypeValueParser parser = getCurrentParser();
        return parser.genValue(strSelfData, strThemeId, strThemeTitle, strTimeStamp, isWait, isFile, themeUpdateTime, isSellable, strMaterialType);
    }

    private static ThemeTypeValueParser getCurrentParser() {
        //TODO: use android platform version to distinguish new format or old format to write.
        //We need to consider the version of lib in SenseHome and other apps. It will easily cause gen/parse failure.
        return sParsers.get(0);
    }

    private static ThemeTypeValueParser findHandleParser(String src) {
        int length = sParsers.size();
        for (int i = 0; i < length; ++i) {
            ThemeTypeValueParser parser = sParsers.get(i);
            if (parser.handle(src))
                return parser;
        }
        return getCurrentParser();
    }

    private interface ThemeTypeValueParser {
        String genValue(String strSelfData, String strThemeId, String strThemeTitle,
                        String strTimeStamp,
                        boolean isWait, boolean isFile, String themeUpdateTime, boolean isSellable, String strMaterialType);
        ThemeValue parseThemeValue(String src);
        boolean handle(String src);
    }

    static class ParserJSONV1 implements ThemeTypeValueParser {
        private final static String MY_FORMAT = "JSON_V1";
        public String genValue(String strSelfData, String strThemeId, String strThemeTitle,
                               String strTimeStamp,
                               boolean isWait, boolean isFile, String themeUpdateTime, boolean isSellable, String strMaterialType) {
            JSONObject json = new JSONObject();
            try {
                json.put(ThemeType.ValueTag.SELF_DATA, strSelfData);
                json.put(ThemeType.ValueTag.THEME_ID, strThemeId);
                json.put(ThemeType.ValueTag.THEME_TITLE, strThemeTitle);
                json.put(ValueTag.TIME, strTimeStamp);
                json.put(ThemeType.ValueTag.WAIT, isWait);
                json.put(ThemeType.ValueTag.IS_FILE, isFile);
                json.put(ThemeType.ValueTag.THEME_UPDATE_TIME, themeUpdateTime);
                json.put(ThemeType.ValueTag.SELLABLE, isSellable);
                json.put(ThemeType.ValueTag.MATERIAL_TYPE, strMaterialType);
                json.put(ThemeType.ValueTag.FORMAT, MY_FORMAT);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return json.toString();
        }

        public ThemeValue parseThemeValue(String src) {
            ThemeValue themeValue = new ThemeValue();
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(src);
                themeValue.selfData = jsonObject.has(ValueTag.SELF_DATA) ? jsonObject.getString(ValueTag.SELF_DATA) : null;
                themeValue.themeId = jsonObject.has(ValueTag.THEME_ID) ? jsonObject.getString(ValueTag.THEME_ID) : null;
                themeValue.themeTitle = jsonObject.has(ValueTag.THEME_TITLE) ? jsonObject.getString(ValueTag.THEME_TITLE) : null;
                themeValue.time = jsonObject.has(ValueTag.TIME) ? jsonObject.getString(ValueTag.TIME) : null;
                themeValue.wait = jsonObject.has(ValueTag.WAIT) ? Boolean.parseBoolean(jsonObject.getString(ValueTag.WAIT)) : false;
                themeValue.isFile = jsonObject.has(ValueTag.IS_FILE) ? Boolean.parseBoolean(jsonObject.getString(ValueTag.IS_FILE)) : false;
                themeValue.themeUpdateTime = jsonObject.has(ValueTag.THEME_UPDATE_TIME) ? jsonObject.getString(ValueTag.THEME_UPDATE_TIME) : null;
                themeValue.sellable = jsonObject.has(ValueTag.SELLABLE) ? Boolean.parseBoolean(jsonObject.getString(ValueTag.SELLABLE)) : false;
                themeValue.materialType = jsonObject.has(ValueTag.MATERIAL_TYPE) ? jsonObject.getString(ValueTag.MATERIAL_TYPE) : null;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return themeValue;
        }

        public boolean handle(String src) {
            if (src == null)
                return false;

            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(src);
                return MY_FORMAT.equals(jsonObject.getString(ValueTag.FORMAT));
            } catch (JSONException e) {
            }
            return false;
        }
    }

    static class ParserOldFashion implements ThemeTypeValueParser {
        public String genValue(String strSelfData, String strThemeId, String strThemeTitle,
                               String strTimeStamp,
                               boolean isWait, boolean isFile, String themeUpdateTime, boolean isSellable, String strMaterialType) {
            StringBuilder data = new StringBuilder();

            if (strSelfData != null && !strSelfData.isEmpty()) {
                data.append(strSelfData);
            }


            data.append(ThemeType.ValueTag.SELFDATA_SEPARATOR);

            if (strThemeId != null && !strThemeId.isEmpty()) {
                data.append(ThemeType.ValueTag.KEY_SEPARATOR)
                        .append(ThemeType.ValueTag.THEME_ID)
                        .append(ThemeType.ValueTag.VALUE_SEPARATOR)
                        .append(strThemeId);
            }

            if (strThemeTitle != null && !strThemeTitle.isEmpty()) {
                data.append(ThemeType.ValueTag.KEY_SEPARATOR)
                        .append(ThemeType.ValueTag.THEME_TITLE)
                        .append(ThemeType.ValueTag.VALUE_SEPARATOR)
                        .append(strThemeTitle);
            }

            if (strTimeStamp != null && !strTimeStamp.isEmpty()) {
                data.append(ThemeType.ValueTag.KEY_SEPARATOR)
                        .append(ThemeType.ValueTag.TIME)
                        .append(ThemeType.ValueTag.VALUE_SEPARATOR)
                        .append(strTimeStamp);
            }
            if (isWait) {
                data.append(ThemeType.ValueTag.KEY_SEPARATOR)
                        .append(ThemeType.ValueTag.WAIT)
                        .append(ThemeType.ValueTag.VALUE_SEPARATOR)
                        .append(isWait);
            }

            if (isFile) {
                data.append(ThemeType.ValueTag.KEY_SEPARATOR)
                        .append(ThemeType.ValueTag.IS_FILE)
                        .append(ThemeType.ValueTag.VALUE_SEPARATOR)
                        .append(isFile);
            }

            if (themeUpdateTime != null && !themeUpdateTime.isEmpty()) {
                data.append(ThemeType.ValueTag.KEY_SEPARATOR)
                        .append(ThemeType.ValueTag.THEME_UPDATE_TIME)
                        .append(ThemeType.ValueTag.VALUE_SEPARATOR)
                        .append(themeUpdateTime);
            }

            if (isSellable) {
                data.append(ThemeType.ValueTag.KEY_SEPARATOR)
                        .append(ThemeType.ValueTag.SELLABLE)
                        .append(ThemeType.ValueTag.VALUE_SEPARATOR)
                        .append(isSellable);
            }

            if (strMaterialType != null && !strMaterialType.isEmpty()) {
                data.append(ThemeType.ValueTag.KEY_SEPARATOR)
                        .append(ThemeType.ValueTag.MATERIAL_TYPE)
                        .append(ThemeType.ValueTag.VALUE_SEPARATOR)
                        .append(strMaterialType);
            }

            return data.toString();

        }

        public ThemeValue parseThemeValue(String src) {
            ThemeValue themeValue = new ThemeValue();

            String srcValue = src == null ? "" : src;

            // fetching themeTitle first.
            // avoiding special characters cause parsing fail, ex: "#", ":", "=" ...etc.
            // test
            String themeTitle = null;
            if (!srcValue.equals("")) {
                int indexof_preString = srcValue.indexOf(ValueTag.PRE_STRING_THEME_TITLE);
                int indexof_postString = srcValue.indexOf(ValueTag.POST_STRING_THEME_TITLE);
                if (indexof_preString != -1 && indexof_postString != -1 && indexof_preString < indexof_postString) {
                    int startIndex_themeTitle = indexof_preString + ValueTag.PRE_STRING_THEME_TITLE.length();
                    int endIndex_themeTitle = indexof_postString;
                    themeTitle = srcValue.substring(startIndex_themeTitle, endIndex_themeTitle);
                    srcValue = srcValue.substring(0, startIndex_themeTitle) + srcValue.substring(endIndex_themeTitle);
                    Log.w(LOG_TAG, "srcValue " + srcValue);
                }
            }

            // split selfData
            String[] selfData = (srcValue != null ? srcValue.split(ValueTag.SELFDATA_SEPARATOR) : null);
            String secValue = null;

            if (selfData != null && selfData.length == 2) {
                if (!selfData[0].isEmpty())
                    themeValue.selfData = selfData[0];
                secValue = selfData[1];
            }


            // split themeInfo
            // ex: self#themeId=t140d6999-59ad:time=1230:waitFull=true:isFile=false
            String[] themeInfoSets = (secValue != null ? secValue.split(ValueTag.KEY_SEPARATOR) : null);
            HashMap<String, String> themeInfoMap = new HashMap<String, String>();
            if (themeInfoSets != null) {
                for (String themeInfoSet : themeInfoSets) {
                    String[] themeInfo = (themeInfoSet != null ? themeInfoSet.split(ValueTag.VALUE_SEPARATOR) : null);
                    if (themeInfo != null && themeInfo.length == 2) {
                        themeInfoMap.put(themeInfo[0], themeInfo[1]);
                    }
                }
            }

            themeValue.themeId = themeInfoMap.get(ValueTag.THEME_ID);
            themeValue.themeTitle = themeTitle;
            themeValue.time = themeInfoMap.get(ValueTag.TIME);
            themeValue.wait = Boolean.parseBoolean(themeInfoMap.get(ValueTag.WAIT));
            themeValue.isFile = Boolean.parseBoolean(themeInfoMap.get(ValueTag.IS_FILE));
            themeValue.themeUpdateTime = themeInfoMap.get(ValueTag.THEME_UPDATE_TIME);
            themeValue.sellable = Boolean.parseBoolean(themeInfoMap.get(ValueTag.SELLABLE));
            themeValue.materialType = themeInfoMap.get(ValueTag.MATERIAL_TYPE);

            return themeValue;
        }

        public boolean handle(String src) {
            return true;
        }
    }
}
