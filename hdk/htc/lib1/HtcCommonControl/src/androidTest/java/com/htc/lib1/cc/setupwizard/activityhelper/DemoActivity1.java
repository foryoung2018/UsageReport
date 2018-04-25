/*
 * HTC Corporation Proprietary Rights Acknowledgment
 *
 * Copyright (C) 2008 HTC Corporation
 *
 * All Rights Reserved.
 *
 * The information contained in this work is the exclusive property of HTC Corporation
 * ("HTC").  Only the user who is legally authorized by HTC ("Authorized User") has
 * right to employ this work within the scope of this statement.  Nevertheless, the
 * Authorized User shall not use this work for any purpose other than the purpose
 * agreed by HTC.  Any and all addition or modification to this work shall be
 * unconditionally granted back to HTC and such addition or modification shall be
 * solely owned by HTC.  No right is granted under this statement, including but not
 * limited to, distribution, reproduction, and transmission, except as otherwise
 * provided in this statement.  Any other usage of this work shall be subject to the
 * further written consent of HTC.
 */

package com.htc.lib1.cc.setupwizard.activityhelper;


import android.os.Bundle;
import android.view.View;
import com.htc.lib1.cc.test.R;

import com.htc.lib1.cc.widget.setupwizard.HtcWizardActivity;
import com.htc.lib1.cc.setupwizard.activityhelper.util.SetupWizardUtil;

/**
 *
 * Demo HtcWizardActivity (normal)
 *
 */
public class DemoActivity1 extends HtcWizardActivity {

    protected int mInitPageIndex = 1;
    protected int mPageIndex = mInitPageIndex;
    protected int mPageMaxNumber = 5;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        SetupWizardUtil.initThemeAndCategory(this);
        super.onCreate(savedInstanceState);

        setTitleText("Action Bar Title");
        setTitleText(R.string.title_demo_activity1);
        setProgressBar(mPageIndex, mPageMaxNumber);

        setSubTitle("Test");
        setSubTitle(R.string.title_demo_activity1);
        setBackBtnEnabled(true);
        setBackBtnIcon(R.drawable.ic_launcher);
        setBackBtnIcon(null);
        setBackBtnText("Test");
        setBackBtnText(R.string.va_ok);
        setNextBtnEnabled(true);
        setNextBtnIcon(R.drawable.ic_launcher);
        setNextBtnIcon(null);
        setNextBtnText("Test");
        setNextBtnText(R.string.va_cancel);
        hideBackBtn(false);
        hideNextBtn(false);
        hideProgress(false);
        getFooter();
        setBackBtnOnClickListener(null);
        setNextBtnOnClickListener(null);
        onKeyDown(0, null);
        onClick(R.id.footer);
        View view = findViewById(R.id.footer);
        onClick(view);
        isOrientationChanged(getResources().getConfiguration(),getResources().getConfiguration());
    }

}
