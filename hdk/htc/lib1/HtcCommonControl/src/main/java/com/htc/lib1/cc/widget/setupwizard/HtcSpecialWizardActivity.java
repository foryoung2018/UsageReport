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
 * further written consent of HTC
 */

package com.htc.lib1.cc.widget.setupwizard;

import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.htc.lib1.cc.R;
import com.htc.lib1.cc.util.HtcWrapConfigurationUtil;
import com.htc.lib1.cc.util.WindowUtil;

/**
 * SetupWizard common UI instead subContentView of HtcWizardActivity origin one
 * it's a special mode define in UI guideline subContentView with one
 * image+TextBody+customized View and there is no padding between subTitle and
 * ImageBody
 *
 * @author chris_wang@htc.com
 *
 */

public class HtcSpecialWizardActivity extends HtcWizardActivity {
    // UI components
    private View mImageLayout;
    private ImageView mImage;
    private View mDivider;
    private LinearLayout mContentLayout;
    private TextView mText;
    // private HtcRimButton mButton;
    private View mCustomView;

    // Stored parameters
    private int mImageResId;
    private Drawable mImageDrawable;
    private CharSequence mDescTextString;
    private int mDescTextResId;
    private Configuration mConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mConfig = new Configuration(getResources().getConfiguration());
    }

    @Override
    public void onDelayUIUpdate() {
        initialize();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        HtcWrapConfigurationUtil.applyHtcFontscale(this);
        if (isOrientationChanged(newConfig, mConfig)) {
            mConfig = new Configuration(newConfig);
            initialize();
        }
    }

    private void initialize() {
        setSubContentView(com.htc.lib1.cc.R.layout.wizard_button_activity);

        if (mContentLayout != null && mCustomView != null) {
            mContentLayout.removeView(mCustomView);
        }

        mContentLayout = (LinearLayout) findViewById(com.htc.lib1.cc.R.id.content_layout);
        mImageLayout = findViewById(com.htc.lib1.cc.R.id.image_layout);
        mImage = (ImageView) findViewById(R.id.image);
        mDivider = findViewById(com.htc.lib1.cc.R.id.divider);
        mText = (TextView) findViewById(com.htc.lib1.cc.R.id.desc);

        if (mImageResId != 0) {
            setImage(mImageResId);
        } else if (mImageDrawable != null) {
            setImage(mImageDrawable);
        }

        if (mDescTextResId != 0) {
            setDescriptionText(mDescTextResId);
        } else if (mDescTextString != null) {
            setDescriptionText(mDescTextString);
        } else if (mText != null)
            mText.setVisibility(View.GONE);

        if (mCustomView != null) {
            addCustomBottomView(mCustomView);
        }
    }

    /**
     * set image of ImageBody which under subTitle and upper two ListItem
     *
     * @param id
     *            resource id of image content
     */
    protected void setImage(int id) {
        mImageDrawable = null;
        mImageResId = id;
        if (mImageLayout == null || mImage == null) {
            return;
        }

        boolean isImageSet = (id != 0);
        mImageLayout.setVisibility(isImageSet ? View.VISIBLE : View.GONE);
        if (mDivider != null) {
            mDivider.setVisibility(WindowUtil.isSuitableForLandscape(getResources()) ? View.VISIBLE
                    : View.GONE);
        }
        mImage.setImageResource(id);
    }

    /**
     * set image of ImageBody which under subTitle and upper two ListItem
     *
     * @param drawable
     *            drawable of image content
     */
    protected void setImage(Drawable drawable) {
        mImageResId = 0;
        mImageDrawable = drawable;
        if (mImageLayout == null || mImage == null) {
            return;
        }

        boolean isImageSet = (drawable != null);
        mImageLayout.setVisibility(isImageSet ? View.VISIBLE : View.GONE);
        if (mDivider != null) {
            mDivider.setVisibility(WindowUtil.isSuitableForLandscape(getResources()) ? View.VISIBLE
                    : View.GONE);
        }
        mImage.setImageDrawable(drawable);
    }

    /**
     * set description of TextBody
     *
     * @param id
     *            resource id of TextBody description content
     */
    protected void setDescriptionText(int id) {
        mDescTextString = null;
        mDescTextResId = id;
        if (mText == null) {
            return;
        }

        boolean isTextSet = (id != 0);
        mText.setVisibility(isTextSet ? View.VISIBLE : View.GONE);
        mText.setText(id);
    }

    /**
     * set description of TextBody
     *
     * @param str
     *            TextBody description content
     */
    protected void setDescriptionText(CharSequence str) {
        mDescTextResId = 0;
        mDescTextString = str;
        if (mText == null) {
            return;
        }

        boolean isTextSet = (str != null);
        mText.setVisibility(isTextSet ? View.VISIBLE : View.GONE);
        mText.setText(str);
    }

    /**
     * set custom View/ViewGroup under TextBody
     *
     * @param view
     *            custom view
     */
    protected void addCustomBottomView(View view) {
        if (mCustomView != null && mCustomView.getParent() != null) {
            ((ViewGroup) mCustomView.getParent()).removeView(mCustomView);
        }
        mCustomView = view;
        if (mContentLayout != null) {
            mContentLayout.addView(view);
        }
    }
}
