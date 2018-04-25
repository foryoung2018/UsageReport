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

package com.htc.sense.commoncontrol.demo.setupwizard;

import android.os.Bundle;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.htc.lib1.cc.widget.HtcOverlapLayout;
import com.htc.lib1.cc.widget.HtcRimButton;
import com.htc.lib1.cc.widget.setupwizard.HtcWizardActivity;
import com.htc.sense.commoncontrol.demo.R;
import com.htc.sense.commoncontrol.demo.util.CommonUtil;

/**
 *
 * Demo HtcWizardActivity (normal)
 *
 */
public class DemoActivity1 extends HtcWizardActivity
{
    private boolean mbProgressBarHide = false;
    private boolean mbBackBtnHide     = false;
    private boolean mbNextBtnHide     = false;
    private boolean mbFooterHide      = false;
    private boolean mbNextBtnEnabled  = true;

    protected int mInitPageIndex = 1;
    protected int mPageIndex     = mInitPageIndex;
    protected int mPageMaxNumber = 4;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState,false);
        CommonUtil.reloadDemoTheme(this, savedInstanceState);
        CommonUtil.initHtcActionBar(this, true, true);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        ViewGroup vg =(ViewGroup) findViewById(android.R.id.content);
        HtcOverlapLayout overlay = (HtcOverlapLayout) vg.getChildAt(0);
        overlay.isActionBarVisible(true);
        overlay.setInsetStatusBar(true);

//        getWindow().setBackgroundDrawable(new ColorDrawable(Color.RED));
//        getActionBar().hide();
//
        setProgressBar(mPageIndex, mPageMaxNumber);
    }

    @Override
    public void onDelayUIUpdate() {
        initialize();
    }

    private void initialize()
    {
        setSubContentView(R.layout.wizard_activity1);

        HtcRimButton button;

        // Progress bar
        button = (HtcRimButton) findViewById(R.id.show_hide_progress);
        if(button != null) {
            button.setOnClickListener(this);
        }
        button = (HtcRimButton) findViewById(R.id.progress_minus);
        if(button != null) {
            button.setOnClickListener(this);
        }
        button = (HtcRimButton) findViewById(R.id.progress_plus);
        if(button != null) {
            button.setOnClickListener(this);
        }

        // Back button
        button = (HtcRimButton) findViewById(R.id.show_hide_back);
        if(button != null) {
            button.setOnClickListener(this);
        }

        // Next button
        button = (HtcRimButton) findViewById(R.id.show_hide_next);
        if(button != null) {
            button.setOnClickListener(this);
        }
        button = (HtcRimButton) findViewById(R.id.enable_disable_next);
        if(button != null) {
            button.setOnClickListener(this);
        }

        // Footer
        button = (HtcRimButton) findViewById(R.id.show_hide_footer);
        if(button != null) {
            button.setOnClickListener(this);
        }

        // Reset
        button = (HtcRimButton) findViewById(R.id.reset_screen);
        if(button != null) {
            button.setOnClickListener(this);
        }
    }

    @Override
    protected void onClick(int id)
    {
        switch(id) {
            // Progress bar
            case R.id.show_hide_progress:
                mbProgressBarHide = !mbProgressBarHide;
                hideProgress(mbProgressBarHide);
                break;
            case R.id.progress_minus:
                if(mPageIndex > 0) {
                    setProgressBar(--mPageIndex, mPageMaxNumber);
                }
                break;
            case R.id.progress_plus:
                if(mPageIndex < mPageMaxNumber) {
                    setProgressBar(++mPageIndex, mPageMaxNumber);
                }
                break;

            // Back button
            case R.id.show_hide_back:
                mbBackBtnHide = !mbBackBtnHide;
                hideBackBtn(mbBackBtnHide);
                break;

            // Next button
            case R.id.show_hide_next:
                mbNextBtnHide = !mbNextBtnHide;
                hideNextBtn(mbNextBtnHide);
                break;
            case R.id.enable_disable_next:
                mbNextBtnHide = false;
                hideNextBtn(mbNextBtnHide);
                mbNextBtnEnabled = !mbNextBtnEnabled;
                setNextBtnEnabled(mbNextBtnEnabled);
                break;

            // Footer
            case R.id.show_hide_footer:
                mbFooterHide = !mbFooterHide;
                hideFooter(mbFooterHide);
                break;

            case R.id.reset_screen:
                resetScreen();
                break;

            default:
                super.onClick(id);
                break;
        }
    }

    private void resetScreen() {
        mbProgressBarHide = false;
        mbBackBtnHide     = false;
        mbNextBtnHide     = false;
        mbFooterHide      = false;
        mbNextBtnEnabled  = true;
        mPageIndex        = mInitPageIndex;
        hideProgress(mbProgressBarHide);
        setProgressBar(mPageIndex, mPageMaxNumber);
        hideBackBtn(mbBackBtnHide);
        hideNextBtn(mbNextBtnHide);
        setNextBtnEnabled(mbNextBtnEnabled);
        hideFooter(mbFooterHide);
    }
}
