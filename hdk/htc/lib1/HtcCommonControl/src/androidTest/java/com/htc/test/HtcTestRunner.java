package com.htc.test;

import android.app.Activity;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.test.runner.AndroidJUnitRunner;
import android.util.Log;

import com.robotium.solo.Solo;

import java.nio.charset.Charset;
import java.util.Locale;

public class HtcTestRunner extends AndroidJUnitRunner {
    private static final String TAG = "HtcTestRunner";
    private static final Charset UTF8 = Charset.forName("UTF-8");

    String mTheme = null;
    int mOrientation = Solo.PORTRAIT;
    String mFontStyle = null;
    int mCategoryId = 0;
    private Bundle mArguments;
    private int mDensity = 0;

    @Override
    public void callActivityOnCreate(Activity activity, Bundle icicle) {
        Locale preferredLocale = getPreferredLocale();
        if (!preferredLocale.equals(Locale.getDefault())) {
            Resources res = getTargetContext().getResources();
            Configuration config = res.getConfiguration();
            config.locale = preferredLocale;
            Locale.setDefault(preferredLocale);
            res.updateConfiguration(config, res.getDisplayMetrics());
        }
        super.callActivityOnCreate(activity, icicle);
    }

    @Override
    public void onCreate(Bundle arguments) {
        super.onCreate(arguments);
        mArguments = arguments;
        mTheme = arguments.getString("theme");
        mFontStyle = arguments.getString("font");
        String orientation = arguments.getString("orientation");
        mCategoryId = arguments.getInt("category");
        String strDensity =  arguments.getString("density");
        if (null != strDensity) {
            mDensity = Integer.valueOf(strDensity);
        }

        if ("portrait".equals(orientation)) {
            mOrientation = Solo.PORTRAIT;
        } else if ("landscape".equals(orientation)) {
            mOrientation = Solo.LANDSCAPE;
        }

        Log.d(TAG, "mFontStyle=" + mFontStyle);
        Log.d(TAG, "orientation=" + orientation + " " + mOrientation);
    }

    public String getThemeName() {
        return mTheme;
    }

    public int getOrientation() {
        return mOrientation;
    }

    public String getFontStyle() {
        return mFontStyle;
    }

    public int getCategoryId() {
        return mCategoryId;
    }

    protected Locale getPreferredLocale() {
        return Locale.ENGLISH;
    }

    public Bundle getArguments() {
        return mArguments;
    }

    public int getDensity() {
        return mDensity;
    }

}
