/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.htc.sense.commoncontrol.demo;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;

import com.htc.lib1.cc.util.ActionBarUtil;
import com.htc.lib1.cc.util.HtcCommonUtil;
import com.htc.lib1.cc.util.HtcCommonUtil.ThemeChangeObserver;
import com.htc.lib1.cc.widget.ActionBarDropDown;
import com.htc.lib1.cc.widget.ActionBarExt;
import com.htc.lib1.theme.ThemeFileUtil;
import com.htc.lib1.theme.ThemeType;
import com.htc.sense.commoncontrol.demo.util.CommonUtil;

public class CommonDemoActivityBase extends Activity {
    protected int mThemeResId = R.style.HtcDeviceDefault;
    protected int mCategoryId = HtcCommonUtil.BASELINE;
    protected Bundle mThemeBundle;

    protected ActionBarExt mActionBarExt;
    private ActionBarDropDown mTitleDropDown;

    private ThemeChangeObserver mThemeChangeObserver = new ThemeChangeObserver() {

        @Override
        public void onThemeChange(int type) {
            recreate();
        }
    };

    public ThemeFileUtil.FileCallback mThemeFileCallBack = new ThemeFileUtil.FileCallback() {
        @Override
        public void onCompleted(Context context, ThemeFileUtil.ThemeFileTaskInfo result) {
            onThemeFileComplete();
            Log.d("CommonDemoActivityBase", "onThemeFileComplete()");
        }
    };

    protected void onThemeFileComplete() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mThemeBundle = CommonUtil.applyDemoTheme(this, savedInstanceState);
        mThemeResId = mThemeBundle.getInt(CommonUtil.EXTRA_THEME_KEY);
        mCategoryId = mThemeBundle.getInt(CommonUtil.EXTRA_CATEGORY_KEY);

        HtcCommonUtil.registerThemeChangeObserver(this, ThemeType.HTC_THEME_CC, mThemeChangeObserver);
        HtcCommonUtil.registerThemeChangeObserver(this, ThemeType.HTC_THEME_CT, mThemeChangeObserver);
        HtcCommonUtil.registerThemeChangeObserver(this, ThemeType.HTC_THEME_FULL, mThemeChangeObserver);

        applyCustomWindowFeature();

        if (shouldApplyHtcActionBar()) {
            mActionBarExt = CommonUtil.initHtcActionBar(this, shouldEnableBackup(), false);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mThemeBundle.putInt(CommonUtil.EXTRA_THEME_KEY, mThemeResId);
        mThemeBundle.putInt(CommonUtil.EXTRA_CATEGORY_KEY, mCategoryId);
        outState.putBundle(CommonUtil.EXTRA_THEME_BUNDLE_KEY, mThemeBundle);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        HtcCommonUtil.unregisterThemeChangeObserver(ThemeType.HTC_THEME_CC, mThemeChangeObserver);
        HtcCommonUtil.unregisterThemeChangeObserver(ThemeType.HTC_THEME_CT, mThemeChangeObserver);
        HtcCommonUtil.unregisterThemeChangeObserver(ThemeType.HTC_THEME_FULL, mThemeChangeObserver);
    }

    protected boolean shouldApplyHtcActionBar() {
        return true;
    }

    protected boolean shouldEnableBackup() {
        return true;
    }

    protected boolean shouldEnableTitle() {
        return true;
    }

    protected void applyCustomWindowFeature() {
    }

    protected void buildThemeIntent(Intent intent) {
        if (intent != null) {
            intent.putExtra(CommonUtil.EXTRA_THEME_BUNDLE_KEY, mThemeBundle);
        }
    }

    @Override
    public void onActionModeStarted(ActionMode mode) {
        super.onActionModeStarted(mode);
        ActionBarUtil.setActionModeBackground(this, mode, new ColorDrawable(HtcCommonUtil.getCommonThemeColor(this, R.styleable.ThemeColor_multiply_color)));
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode, Bundle options) {
        buildThemeIntent(intent);

        super.startActivityForResult(intent, requestCode, options);
    }

    @Override
    public void startActivityFromFragment(Fragment fragment, Intent intent, int requestCode, Bundle options) {
        buildThemeIntent(intent);

        super.startActivityFromFragment(fragment, intent, requestCode, options);
    }

    @Override
    protected void onTitleChanged(CharSequence title, int color) {
        super.onTitleChanged(title, color);
        if (shouldApplyHtcActionBar() && shouldEnableTitle()) {
            mTitleDropDown = CommonUtil.updateCommonTitle(this, mActionBarExt, mTitleDropDown, title);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        HtcCommonUtil.updateCommonResConfiguration(this);
    }
}
