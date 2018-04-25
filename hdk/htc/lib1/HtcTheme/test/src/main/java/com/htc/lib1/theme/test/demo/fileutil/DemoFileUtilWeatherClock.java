package com.htc.lib1.theme.test.demo.fileutil;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.htc.lib1.cc.util.HtcCommonUtil;
import com.htc.lib1.theme.ThemeCompatUtil;
import com.htc.lib1.theme.ThemeFileUtil;
import com.htc.lib1.theme.ThemeType;
import com.htc.lib1.theme.test.R;

public class DemoFileUtilWeatherClock extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
       // ThemeFileUtil.getThemeFiles(this, ThemeFileUtil.ThemeFile.CBaseline);
        HtcCommonUtil.initTheme(this, 0, new ThemeFileUtil.FileCallback() {
            @Override
            public void onCompleted(Context context, ThemeFileUtil.ThemeFileTaskInfo result) {
                super.onCompleted(context, result);
                Drawable actionBarTexture = getActionBarTexture(context);
                Drawable statusBarTexture = getStatusBarTexture(context);
                android.util.Log.d("jasonlai0805", "1 actionBarTexture " + actionBarTexture + ", statusBarTexture " + statusBarTexture);

//                int color = getOverlayColor(context);
//                android.util.Log.d("jasonlai0805", "1 getOverlayColor " + color);
            }
        });

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_file_util_weather_clock);
        View v = findViewById(R.id.content_container);
        v.setBackgroundColor(getOverlayColor(this));
        checkAndApplyWeatherClockTheme();

        registerThemeObserver();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterThemeObserver();
    }

    private void checkAndApplyWeatherClockTheme() {
        //What WeatherClock activity cares are HTC_THEME_WEATHER_CLOCK and HTC_THEME_FULL.
        boolean weatherThemeChanged = ThemeFileUtil.isAppliedThemeChanged(this, ThemeType.HTC_THEME_WEATHER_CLOCK);

        if (weatherThemeChanged) {
            onWeatherThemeChanged();
        } else {
            onApplyWeatherTheme(ThemeFileUtil.getAppsThemePath(this));
        }
    }

    private HtcCommonUtil.ThemeChangeObserver mThemeChangeObserver = new HtcCommonUtil.ThemeChangeObserver() {
        @Override
        public void onThemeChange(int type) {
            if (type == ThemeType.HTC_THEME_FULL || type == ThemeType.HTC_THEME_CC) {
                //please refer to the sample code CC has provided in Sense7
                HtcCommonUtil.notifyChange(DemoFileUtilWeatherClock.this, HtcCommonUtil.TYPE_THEME);
                recreate();
            } else if (type == ThemeType.HTC_THEME_WEATHER_CLOCK) {
                checkAndApplyWeatherClockTheme();
            }

        }
    };

    private void registerThemeObserver() {
        HtcCommonUtil.registerThemeChangeObserver(this, ThemeType.HTC_THEME_FULL, mThemeChangeObserver);
        HtcCommonUtil.registerThemeChangeObserver(this, ThemeType.HTC_THEME_CC, mThemeChangeObserver);
        HtcCommonUtil.registerThemeChangeObserver(this, ThemeType.HTC_THEME_WEATHER_CLOCK, mThemeChangeObserver);
    }

    private void unregisterThemeObserver() {
        HtcCommonUtil.unregisterThemeChangeObserver(ThemeType.HTC_THEME_FULL, mThemeChangeObserver);
        HtcCommonUtil.registerThemeChangeObserver(this, ThemeType.HTC_THEME_CC, mThemeChangeObserver);
        HtcCommonUtil.unregisterThemeChangeObserver(ThemeType.HTC_THEME_WEATHER_CLOCK, mThemeChangeObserver);
    }

    private void onWeatherThemeChanged() {
        ThemeFileUtil.getThemeFilesAsync(this, new ThemeFileUtil.FileCallback() {
            public void onCompleted(Context context, ThemeFileUtil.ThemeFileTaskInfo result) {

                //WeatherClock.apk is copied to app side now.
                String appThemePath = result.getAppLocalThemePath();
                Toast.makeText(DemoFileUtilWeatherClock.this, "File copy to " + appThemePath + " cost " + result.getTimeCost() + "ms", Toast.LENGTH_SHORT).show();

                //update the applied theme info since the files are in app's side.
                ThemeFileUtil.saveAppliedThemeInfo(DemoFileUtilWeatherClock.this, ThemeType.HTC_THEME_WEATHER_CLOCK);
                onApplyWeatherTheme(appThemePath);
            }
        }, ThemeFileUtil.ThemeFile.WeatherClock);
    }

    private void onApplyWeatherTheme(String appThemePath) {
    }

    public static int getOverlayColor(Context context) {
        final int color = HtcCommonUtil.getCommonThemeColor(context, R.styleable.ThemeColor_overlay_color);
        return color;
    }

    public static Drawable getStatusBarTexture(Context context) {
        return HtcCommonUtil.getCommonThemeTexture(context, R.styleable.CommonTexture_android_windowBackground);
    }

    public static Drawable getActionBarTexture(Context context) {
        return HtcCommonUtil.getCommonThemeTexture(context, R.styleable.CommonTexture_android_headerBackground);
    }

    public void onClickCompatTest(View v) {
        v.setBackgroundColor(getOverlayColor(this));
        boolean isDifferent = ThemeCompatUtil.isAppliedWeatherClockThemeChanged(v.getContext());
        Toast.makeText(DemoFileUtilWeatherClock.this, "File is different " + isDifferent, Toast.LENGTH_SHORT).show();
    }
}
