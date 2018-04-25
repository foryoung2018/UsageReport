package com.htc.lib1.theme;

import android.content.Context;

import java.io.File;

/**
 * Created by jason on 8/19/16.
 */
public class ThemeCompatUtil {
    private static final String LOG_TAG = "ThemeCompatUtil";

    public static final String THEME_ARCHITECTURE = "htc_theme_architecture";

    public static final int PAID_THEME_ARCHITECTURE = 1;

    public static boolean isAppliedWeatherClockThemeChanged(Context context) {
        return isAppliedThemeChanged(context, ThemeFileUtil.ThemeFile.WeatherClock);
    }

    private static boolean isAppliedThemeChanged(Context context, ThemeFileUtil.ThemeFile themeFile) {
        boolean isDifferent = false;
        if (isPaidThemeArchitecture(context)) {
            isDifferent = ThemeFileUtil.isAppliedThemeChanged(context, themeFile.themeType);
            ThemeSettingUtil.logd(LOG_TAG, "theme info isDifferent %s", isDifferent);
        } else {
            isDifferent = isThemeFileDifferent(context, themeFile);
            ThemeSettingUtil.logd(LOG_TAG, "file checksum isDifferent %s", isDifferent);
        }

        return isDifferent;
    }

    /**
     * To judge if local theme file is different from theme file under current theme folder. Note that this cannot be used in ThemeFile.CResources which has more than one file name.
     * @param context context
     * @param themeFile Must be one of the ThemeFile
     * @return true means that the local theme for given themeType is different from it under current theme folder.
     * */
    private static boolean isThemeFileDifferent(Context context, ThemeFileUtil.ThemeFile themeFile) {
        String currentThemeFileChecksum = ThemeFileInnerHelper.getChecksum(ThemeSettingUtil.getString(context, ThemeType.KEY_APP_CURRENT_THEME_PATH) + themeFile.name[0]);
        ThemeSettingUtil.logd(LOG_TAG, "checksum current %s", currentThemeFileChecksum);

        String localThemeFileChecksum = ThemeFileInnerHelper.getChecksum(ThemeFileUtil.getAppsThemePath(context) + themeFile.name[0]);
        ThemeSettingUtil.logd(LOG_TAG, "checksum local %s", localThemeFileChecksum);
        return !localThemeFileChecksum.equals(currentThemeFileChecksum);
    }

    private static boolean isPaidThemeArchitecture(Context context) {
        try {
            String value = ThemeSettingUtil.getString(context, THEME_ARCHITECTURE);
            int version = value != null ? Integer.parseInt(value) : 0;
            ThemeSettingUtil.logd(LOG_TAG, "THEME_ARCHITECTURE %s", version);
            return version >= PAID_THEME_ARCHITECTURE;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return false;
        }
    }
}
