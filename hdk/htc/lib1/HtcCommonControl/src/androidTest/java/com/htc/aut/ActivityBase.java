
package com.htc.aut;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import com.htc.aut.util.ActivityUtil;

/**
 * @author BiWei
 */
public class ActivityBase extends Activity {

    protected String mThemeName = null;
    protected int mThemeResId = 0;
    protected int mOrientation = 0;
    protected String mFontStyle = null;
    protected int mCategoryId = 0;
    protected int mDensity = 0;

    private void getValueFromIntent() {
        final Intent i = getIntent();
        if (null == i) {
            return;
        }

        mThemeName = i.getStringExtra(ActivityUtil.THEMENAME);
        mOrientation = i.getIntExtra(ActivityUtil.ORIENTATION,
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mFontStyle = i.getStringExtra(ActivityUtil.FONT);
        mCategoryId = i.getIntExtra(ActivityUtil.CATEGORYID, 0);
        mDensity = i.getIntExtra(ActivityUtil.DENSITY, 0);

    }

    private void initOrientation() {
        setRequestedOrientation(mOrientation);
    }

    protected boolean isInitOrientation() {
        return false;
    }

    protected boolean isInitCategory() {
        return true;
    }

    protected boolean isDisableDynamicTheme() {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getValueFromIntent();

        if (0 != mDensity) {
            ActivityUtil.setDensity(getResources(), mDensity);
        }

        mThemeResId = ActivityUtil.initTheme(mThemeName, this);

        if (isInitCategory()) {
            ActivityUtil.initCategory(this, mCategoryId);
        }

        if (isDisableDynamicTheme()) {
            ActivityUtil.disableDynamicTheme(this);
        }

        if (isInitOrientation()) {
            initOrientation();
        }
    }
}
