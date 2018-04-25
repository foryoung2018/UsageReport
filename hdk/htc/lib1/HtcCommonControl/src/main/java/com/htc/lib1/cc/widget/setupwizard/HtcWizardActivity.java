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

package com.htc.lib1.cc.widget.setupwizard;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.htc.lib1.cc.htcjavaflag.HtcBuildFlag;
import com.htc.lib1.cc.util.HtcWrapConfigurationUtil;
import com.htc.lib1.cc.view.util.HtcProgressBarUtil;
import com.htc.lib1.cc.widget.ActionBarContainer;
import com.htc.lib1.cc.widget.ActionBarExt;
import com.htc.lib1.cc.widget.ActionBarText;
import com.htc.lib1.cc.widget.HtcFooter;
import com.htc.lib1.cc.widget.HtcFooterButton;
import com.htc.lib1.cc.widget.HtcOverlapLayout;
import com.htc.lib1.cc.widget.HtcListView;
import com.htc.lib1.cc.widget.HtcProgressBar;

/**
 * This is the base class contain SetupWizard framework UI contain Action Bar,
 * Progress Bar, HtcFooter Bar, HtcFooter button and extend by
 * {@link com.htc.lib1.cc.widget.setupwizard.HtcButtonWizardActivity},
 * {@link com.htc.lib1.cc.widget.setupwizard.HtcRadioListWizardActivity} and
 * {@link com.htc.lib1.cc.widget.setupwizard.HtcSpecialWizardActivity} can change inner
 * layout by API setSubContentView()
 *
 * @author chriswang
 *
 */
public abstract class HtcWizardActivity extends Activity implements
        OnClickListener {
    private final String TAG = "Common_" + this.getClass().getSimpleName();

    private ActionBarExt mActionBarExt = null;
    private ActionBarText mActionBarText = null;
    private TextView mTip = null;
    private ProgressBar mProgressBar = null;
    private HtcFooter mFooter = null;
    private HtcFooterButton mBtnBack = null;
    private HtcFooterButton mBtnNext = null;
    private CharSequence mBtnBackString = null;
    private CharSequence mBtnNextString = null;
    private OnClickListener mBtnBackOnClickListener = null;
    private OnClickListener mBtnNextOnClickListener = null;

    private View mContentView = null;
    private ViewGroup mBaseLayout = null;
    private ViewGroup mSubContentView = null;
    private HtcListView mListView = null;
    private int mSubContentViewResId = 0;

    private boolean mbEnableBackBtn = true;
    private boolean mbEnableNextBtn = true;
    private boolean mbHideBackBtn = false;
    private boolean mbHideNextBtn = false;
    private boolean mbActionBarVisible = true;
    private boolean mbHideFooter = false;
    private boolean mbHideProgress = false;
    private int mPageIndex = -1;
    private int mProgressBarMaxNumber = -1;
    private float mAfterFontScale; // Htc font scale
    /**
     * status of UI init on onCreate stage
     */
    public boolean mCreateInit = false;

    private static final int MSG_ID_ANIMATION_DONE = 400;
    private boolean mIsAnimating = true;
    private Handler mHandler = new Handler() {
        public void handleMessage(final Message msg) {
            switch (msg.what) {
            case MSG_ID_ANIMATION_DONE:
                mIsAnimating = false;
                break;
            }
        }
    };

    /**
     * @hide {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        onCreate(savedInstanceState, true);
    }

    /**
     * Called when the activity is starting.
     *
     * @param savedInstanceState
     *            If the activity is being re-initialized after previously being
     *            shut down then this Bundle contains the data it most recently
     *            supplied in onSaveInstanceState(Bundle)
     * @param actionBarVisible
     *            If the activity contain action bar
     */
    protected void onCreate(Bundle savedInstanceState, boolean actionBarVisible) {
        Log.i(TAG, "onCreate");
        applyHtcFontscale(); // Htc font scale
        requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);

        super.onCreate(savedInstanceState);

        mbActionBarVisible = actionBarVisible;
        initUI();
        mCreateInit = true;
    }

    /**
     * Htc font scale apply htc huge font size you may override this if you do
     * NOT want this
     */
    protected void applyHtcFontscale() {
        boolean applied = HtcWrapConfigurationUtil.applyHtcFontscale(this);
        if (HtcBuildFlag.Htc_DEBUG_flag) {
            Log.d(TAG, "applyHtcFontscale: applied=" + applied);
        }
        mAfterFontScale = getResources().getConfiguration().fontScale;
        if (HtcBuildFlag.Htc_DEBUG_flag) {
            Log.d(TAG, "applyHtcFontscale: applied=" + applied
                    + " mAfterFontScale=" + mAfterFontScale);
        }
    }

    private void initUI() {
        Log.i(TAG, "initUI");
        mContentView = LayoutInflater.from(this).inflate(
                com.htc.lib1.cc.R.layout.wizard_activity, null);

        if (!mbActionBarVisible) {
            HtcOverlapLayout layout = (HtcOverlapLayout) mContentView
                    .findViewById(com.htc.lib1.cc.R.id.overlap_layout);
            if (layout != null) {
                layout.isActionBarVisible(false);
            }
        } else {
            if (mActionBarExt == null) {
                mActionBarExt = new ActionBarExt(this, getActionBar());
                // mActionBarExt.enableHTCLandscape(true);
            }

            if (mActionBarText == null) {
                mActionBarText = new ActionBarText(this);
                ActionBarContainer actionBarContainer = mActionBarExt
                        .getCustomContainer();
                actionBarContainer.addCenterView(mActionBarText);
            }
        }

        if (mFooter == null) {
            mFooter = (HtcFooter) mContentView
                    .findViewById(com.htc.lib1.cc.R.id.footer);
            mFooter.ReverseLandScapeSequence(true);
            hideFooter(mbHideFooter);
        }

        /* set for back button */
        mBtnBack = (HtcFooterButton) mContentView
                .findViewById(com.htc.lib1.cc.R.id.back);
        if (mBtnBack != null) {
            /* set text */
            if (!TextUtils.isEmpty(mBtnBackString)) {
                mBtnBack.setText(mBtnBackString);
            }

            /* set listener */
            if (mBtnBackOnClickListener != null) {
                mBtnBack.setOnClickListener(mBtnBackOnClickListener);
            } else {
                mBtnBack.setOnClickListener(this);
            }
        }

        /* set for next/finish button */
        mBtnNext = (HtcFooterButton) mContentView
                .findViewById(com.htc.lib1.cc.R.id.next);
        if (mBtnNext != null) {
            /* set text */
            if (!TextUtils.isEmpty(mBtnNextString)) {
                mBtnNext.setText(mBtnNextString);
            }

            /* set listener */
            if (mBtnNextOnClickListener != null) {
                mBtnNext.setOnClickListener(mBtnNextOnClickListener);
            } else {
                mBtnNext.setOnClickListener(this);
            }
        }

        mProgressBar = (HtcProgressBar) mContentView
                .findViewById(com.htc.lib1.cc.R.id.progress_bar);
        HtcProgressBarUtil.setProgressBarMode(this, mProgressBar, HtcProgressBarUtil.DISPLAY_MODE_FULL);
        setProgressBar(mPageIndex, mProgressBarMaxNumber);
        hideProgress(mbHideProgress);

        mTip = (TextView) mContentView.findViewById(com.htc.lib1.cc.R.id.tip);
        mBaseLayout = (ViewGroup) mContentView
                .findViewById(com.htc.lib1.cc.R.id.base_layout);
        mListView = (HtcListView) mContentView.findViewById(android.R.id.list);
        setSubContentView(mSubContentViewResId);
        setContentView(mContentView);
    }

    /**
     * @hide {@inheritDoc}
     */
    @Override
    protected void onResume() {
        super.onResume();
        checkHtcFontscaleChanged(); // Htc font scale
        // make sure window is not in scrolling kepp from user press back key or
        // next while scrolling
        if (isAnimating()) {
            mHandler.removeMessages(MSG_ID_ANIMATION_DONE);
            mHandler.sendEmptyMessageDelayed(
                    MSG_ID_ANIMATION_DONE,
                    getResources().getInteger(
                            android.R.integer.config_mediumAnimTime) + 150);
        }
    }

    /**
     * Htc font scale check and recreate activity if font size changed to huge.
     * you may override this if you do NOT want this.
     */
    protected void checkHtcFontscaleChanged() {
        boolean checked = HtcWrapConfigurationUtil.checkHtcFontscaleChanged(this,
                mAfterFontScale);
        if (HtcBuildFlag.Htc_DEBUG_flag) {
            Log.d(TAG, "checkHtcFontscaleChanged: checked=" + checked);
        }
        if (checked) {
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
     * @hide {@inheritDoc}
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            if (true == mCreateInit) {
                onDelayUIUpdate();
                mCreateInit = false;
            }
            onResumeDelayUIUpdate();
        }
    }

    /**
     * call back function that delay partial UI initial. only be called while
     * activity re-create.
     */
    /** Override for execute once */
    public void onDelayUIUpdate() {
    }

    /**
     * call back function that delay partial UI initial. It's called no matter
     * activity be kill or not.
     */
    /** Override for execute onResume */
    public void onResumeDelayUIUpdate() {

    }

    /**
     * @hide
     */
    @Override
    protected void onPause() {
        super.onPause();
        mIsAnimating = false;
    }

    /**
     * @hide
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();

        // reduce UI flashing when launch next activity
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.d(TAG, "InterruptedException", e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     **/
    @Override
    public void onClick(View view) {
        onClick(view.getId());
    }

    /**
     * Called when a view has been clicked.
     *
     * @param id
     *            button id
     *
     **/
    protected void onClick(int id) {
        if (id == com.htc.lib1.cc.R.id.next) {
            if (!isAnimating()) {
                setResult(RESULT_OK);
                finish();
            }
        } else if (id == com.htc.lib1.cc.R.id.back) {
            if (!isAnimating()) {
                setResult(WizardConstants.RESULT_BACK_KEY);
                finish();
            }
        }
    }

    /**
     * check enter/exit activity animation is finished
     *
     * @return true:animation finished
     */
    protected boolean isAnimating() {
        return mIsAnimating;
    }

    /**
     * @hide
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
        case KeyEvent.KEYCODE_BACK:
            if (!isAnimating()) {
                setResult(WizardConstants.RESULT_BACK_KEY);
                return super.onKeyDown(keyCode, event);
            }
            return true;

        default:
            return super.onKeyDown(keyCode, event);
        }
    }

    /**
     * set Action Bar title text
     *
     * @param str_id
     *            resource id of string content
     */
    protected void setTitleText(int str_id) {
        if (mActionBarText != null) {
            mActionBarText.setPrimaryText(str_id);
        }
    }

    /**
     * set Action Bar title text
     *
     * @param str
     *            action bar title content
     */
    protected void setTitleText(String str) {
        if (mActionBarText != null) {
            mActionBarText.setPrimaryText(str);
        }
    }

    /**
     * set Sub title text
     *
     * @param str_id
     *            resource id of string content
     */
    protected void setSubTitle(int str_id) {
        if (mTip != null) {
            mTip.setText(str_id);
            mTip.setVisibility(View.VISIBLE);
        }
    }

    /**
     * set Sub title content text
     *
     * @param str
     *            Sub title string content
     */
    protected void setSubTitle(CharSequence str) {
        if (mTip != null) {
            mTip.setText(str);
            mTip.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Sets the string value of the left footer button
     *
     * @param str_id
     *            resource id of string content
     */
    protected void setBackBtnText(int str_id) {
        mBtnBackString = getResources().getString(str_id);
        if (mBtnBack != null && !TextUtils.isEmpty(mBtnBackString)) {
            mBtnBack.setText(mBtnBackString);
        }
    }

    /**
     * Sets the string value of the left footer button
     *
     * @param charSequence
     *            left footer button string content
     */
    protected void setBackBtnText(CharSequence charSequence) {
        mBtnBackString = charSequence;
        if (mBtnBack != null && !TextUtils.isEmpty(mBtnBackString)) {
            mBtnBack.setText(mBtnBackString);
        }
    }

    /**
     * @deprecated [Not use any longer] Sets Icon resource id of the left footer
     *             button
     * @param icon
     *            left footer button icon resource id
     */
    protected void setBackBtnIcon(int icon) {
        if (mBtnBack != null && 0 != icon) {
            mBtnBack.setImageResource(icon);
        }
    }

    /**
     * @deprecated [Not use any longer] Sets Icon drawable the left footer
     *             button
     * @param icon
     *            left footer button icon drawable
     */
    protected void setBackBtnIcon(Drawable icon) {
        if (mBtnBack != null && icon != null) {
            mBtnBack.setImageDrawable(icon);
        }
    }

    /**
     * Register a callback to be invoked when left footer button is clicked. If
     * this button is not clickable, it becomes clickable.
     *
     * @param listener
     *            The callback that will run
     */
    protected void setBackBtnOnClickListener(OnClickListener listener) {
        mBtnBackOnClickListener = listener;
        if (mBtnBack != null && mBtnBackOnClickListener != null) {
            mBtnBack.setOnClickListener(mBtnBackOnClickListener);
        }
    }

    /**
     * Sets the string value of the right footer button
     *
     * @param str_id
     *            resource id of string content
     */
    protected void setNextBtnText(int str_id) {
        mBtnNextString = getResources().getString(str_id);
        if (mBtnNext != null && !TextUtils.isEmpty(mBtnNextString)) {
            mBtnNext.setText(mBtnNextString);
        }
    }

    /**
     * Sets the string value of the right footer button
     *
     * @param charSequence
     *            right footer button string content
     */
    protected void setNextBtnText(CharSequence charSequence) {
        mBtnNextString = charSequence;
        if (mBtnNext != null && !TextUtils.isEmpty(mBtnNextString)) {
            mBtnNext.setText(mBtnNextString);
        }
    }

    /**
     * @deprecated [Not use any longer] Sets Icon resource id of the right
     *             footer button
     * @param icon
     *            right footer button icon resource id
     */
    protected void setNextBtnIcon(int icon) {
        if (mBtnBack != null && 0 != icon) {
            mBtnNext.setImageResource(icon);
        }
    }

    /**
     * @deprecated [Not use any longer] Sets Icon drawable of the right footer
     *             button
     * @param icon
     *            right footer button icon drawable
     */
    protected void setNextBtnIcon(Drawable icon) {
        if (mBtnBack != null && icon != null) {
            mBtnNext.setImageDrawable(icon);
        }
    }

    /**
     * Register a callback to be invoked when right footer button is clicked. If
     * this button is not clickable, it becomes clickable.
     *
     * @param listener
     *            The callback that will run
     */
    protected void setNextBtnOnClickListener(OnClickListener listener) {
        mBtnNextOnClickListener = listener;
        if (mBtnNext != null && mBtnNextOnClickListener != null) {
            mBtnNext.setOnClickListener(mBtnNextOnClickListener);
        }
    }

    /**
     * To set left button is enabled or not.
     *
     * @param enabled
     *            True to enable, false to disable.
     */
    protected void setBackBtnEnabled(boolean enabled) {
        mbEnableBackBtn = enabled;
        if (mBtnBack != null) {
            mBtnBack.setEnabled(mbEnableBackBtn);
        }
    }

    /**
     * To set right button is enabled or not.
     *
     * @param enabled
     *            True to enable, false to disable.
     */
    protected void setNextBtnEnabled(boolean enabled) {
        mbEnableNextBtn = enabled;
        if (mBtnNext != null) {
            mBtnNext.setEnabled(mbEnableNextBtn);
        }
    }

    /**
     * Set the enabled state of this left footer button.
     *
     * @param hide
     *            true to hide left button, false to show.
     */
    protected void hideBackBtn(boolean hide) {
        mbHideBackBtn = hide;
        if (mBtnBack != null) {
            mBtnBack.setVisibility((mbHideBackBtn) ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Set the enabled state of this right footer button.
     *
     * @param hide
     *            true to hide right button, false to show.
     */
    protected void hideNextBtn(boolean hide) {
        mbHideNextBtn = hide;
        if (mBtnNext != null) {
            mBtnNext.setVisibility((mbHideNextBtn) ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Set the enabled state of HtcFooter.
     *
     * @param hide
     *            true to hide HtcFooter, false to show.
     */
    protected void hideFooter(boolean hide) {
        mbHideFooter = hide;
        if (mFooter != null) {
            mFooter.setVisibility((mbHideFooter) ? View.GONE : View.VISIBLE);
        }

        // if (mActionBarExt != null) {
        // mActionBarExt.enableHTCLandscape(!mbHideFooter);
        // }
    }

    /**
     * Set the enabled state of Progress Bar.
     *
     * @param hide
     *            true to hide Progress, false to show.
     */
    protected void hideProgress(boolean hide) {
        mbHideProgress = hide;
        if (mProgressBar != null) {
            mProgressBar.setVisibility((mbHideProgress) ? View.GONE
                    : View.VISIBLE);
        }
    }

    /**
     * Set the current progress to the specified value.
     *
     * @param progress
     *            current progress of progress bar.
     * @param pageMaxNumber
     *            the upper range of this progress bar
     */
    protected void setProgressBar(int progress, int pageMaxNumber) {
        if (progress < 0 || pageMaxNumber < 0) {
            return;
        }
        mPageIndex = progress;
        mProgressBarMaxNumber = pageMaxNumber;

        if (mProgressBar != null) {
            mProgressBar.setMax(pageMaxNumber);
            mProgressBar.setProgress(progress);
        }
    }

    /**
     * set customize inner layout except action bar, footer button, progress
     * bar, sub title
     *
     * @param res_id
     *            Customized inner layout resource id.
     */
    protected void setSubContentView(int res_id) {
        mSubContentViewResId = res_id;
        if (mBaseLayout != null && mSubContentViewResId != 0) {
            if (mSubContentView != null) {
                mBaseLayout.removeView(mSubContentView);
            }
            mSubContentView = (ViewGroup) LayoutInflater.from(this).inflate(
                    mSubContentViewResId, null);
            LayoutParams params = mSubContentView.getLayoutParams();
            if (params == null) {
                params = new LayoutParams(LayoutParams.MATCH_PARENT,
                        LayoutParams.MATCH_PARENT);
            } else {
                params.width = params.height = LayoutParams.MATCH_PARENT;
            }
            mSubContentView.setLayoutParams(params);
            if (mListView != null) {
                mBaseLayout.removeView(mListView);
                mListView = null;
            }
            mBaseLayout.addView(mSubContentView);
            Log.d(TAG, "setSubContentView done");
            if (mSubContentView instanceof ScrollView) {
                mSubContentView
                        .setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
            }
        }
    }

    /**
     * check if orientation change or not
     *
     * @param newConfig
     *            new configuration
     * @param oldConfig
     *            last time configuration
     * @return true is orientation change, otherwise not change
     */
    public boolean isOrientationChanged(Configuration newConfig,
            Configuration oldConfig) {
        return newConfig.screenWidthDp != oldConfig.screenWidthDp
                || newConfig.screenHeightDp != oldConfig.screenHeightDp
                || newConfig.orientation != oldConfig.orientation;
    }

    /**
     * for customized footer content default set at most two footer button you
     * can get footer to customized more than two button
     *
     * @return footer instance on wizard_activity layout
     */
    public HtcFooter getFooter() {
        return mFooter;
    }
}
