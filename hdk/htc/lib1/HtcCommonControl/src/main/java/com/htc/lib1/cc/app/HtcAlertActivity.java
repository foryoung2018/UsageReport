/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.htc.lib1.cc.app;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import com.htc.lib1.cc.htcjavaflag.HtcBuildFlag;
import com.htc.lib1.cc.util.HtcWrapConfigurationUtil;

/**
 * An activity that follows the visual style of an AlertDialog.
 *
 * @see #mAlert
 * @see #mAlertParams
 * @see #setupAlert()
 */
public abstract class HtcAlertActivity extends Activity implements DialogInterface {

    private static final String TAG = "HtcAlertActivity";

    /**
     * The model for the alert.
     *
     * @see #mAlertParams
     */
    protected HtcAlertController mAlert;

    /**
     * The parameters for the alert.
     */
    protected HtcAlertController.AlertParams mAlertParams;

    private float mAfterFontScale; // Htc font scale

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        applyHtcFontscale(); // Htc font scale

        mAlert = new HtcAlertController(this, this, getWindow());
        mAlertParams = new HtcAlertController.AlertParams(this);
    }

    /**
     * Htc font scale
     * apply htc huge font size change you may override this
     * if you do NOT want this
     */
    protected void applyHtcFontscale() {
        boolean applied = HtcWrapConfigurationUtil.applyHtcFontscale(this);
        mAfterFontScale = getResources().getConfiguration().fontScale;
        if (HtcBuildFlag.Htc_DEBUG_flag) {
            Log.d(TAG, "applyHtcFontscale: applied=" + applied + " mAfterFontScale=" + mAfterFontScale);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onResume() {
        super.onResume();

        checkHtcFontscaleChanged(); // Htc font scale
    }

    /**
     * Htc font scale
     * check and recreate activity if font size changed to huge
     * you may override this if you do NOT want this
     */
    protected void checkHtcFontscaleChanged() {
        boolean res = HtcWrapConfigurationUtil.checkHtcFontscaleChanged(this, mAfterFontScale);
        if (res) {
            getWindow().getDecorView().postOnAnimation(new Runnable() {
                @Override
                public void run() {
                    if (HtcBuildFlag.Htc_DEBUG_flag) {
                        Log.d(TAG, "checkHtcFontscaleChanged: recreating...");
                    }
                    recreate();
                }
            });
        }
    }

    /**
     * {@inheritDoc}
     *
     * @deprecated [Not use any longer]
     * @hide
     */
    @Override
    public void cancel() {
        finish();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dismiss() {
        // This is called after the click, since we finish when handling the
        // click, don't do that again here.
        if (!isFinishing()) {
            finish();
        }
    }

    /**
     * Sets up the alert, including applying the parameters to the alert model,
     * and installing the alert's content.
     *
     * @see #mAlert
     * @see #mAlertParams
     */
    protected void setupAlert() {
        mAlertParams.apply(mAlert);
        mAlert.installContent();
    }

    /**
     * {@inheritDoc}
     *
     * @deprecated [Module internal use] should marked (at)hide
     * @hide
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mAlert.onKeyDown(keyCode, event)) return true;
        return super.onKeyDown(keyCode, event);
    }

    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @deprecated [Module internal use] should marked (at)hide
     * @hide
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (mAlert.onKeyUp(keyCode, event)) return true;
        return super.onKeyUp(keyCode, event);
    }
}
