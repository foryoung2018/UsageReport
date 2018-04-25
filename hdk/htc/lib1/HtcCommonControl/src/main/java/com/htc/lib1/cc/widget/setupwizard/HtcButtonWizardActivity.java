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

import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.htc.lib1.cc.R;
import com.htc.lib1.cc.util.HtcWrapConfigurationUtil;
import com.htc.lib1.cc.util.WindowUtil;
import com.htc.lib1.cc.widget.HtcRimButton;

/**
 * SetupWizard common UI instead subContentView of HtcWizardActivity origin one
 * subContentView with one image+TextBody+HtcRimButton+customized View
 *
 * @author chris_wang@htc.com
 *
 */
public class HtcButtonWizardActivity extends HtcWizardActivity {
    // UI components
    private View mImageLayout;
    private ImageView mImage;
    private View mDivider;
    private LinearLayout mContentLayout;
    private TextView mText;
    private HtcRimButton mButton;
    private View mCustomView;

    // Stored parameters
    private int mImageResId;
    private Drawable mImageDrawable;
    private CharSequence mDescTextString;
    private int mDescTextResId;
    private CharSequence mButtonTextString;
    private int mButtonTextResId;
    private OnClickListener mButtonOnClickListener;

    private Configuration mConfig;
    private boolean mMinorFontStyle = false;
    private int mMarginM1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mConfig = new Configuration(getResources().getConfiguration());
        mMarginM1 = getResources().getDimensionPixelOffset(com.htc.lib1.cc.R.dimen.margin_l);
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
        setSubContentView(R.layout.wizard_button_activity);

        if (mContentLayout != null && mCustomView != null) {
            mContentLayout.removeView(mCustomView);
        }

        mContentLayout = (LinearLayout) findViewById(com.htc.lib1.cc.R.id.content_layout);
        mImageLayout = findViewById(com.htc.lib1.cc.R.id.image_layout);
        mImage = (ImageView) findViewById(com.htc.lib1.cc.R.id.image);
        mDivider = findViewById(com.htc.lib1.cc.R.id.divider);
        mText = (TextView) findViewById(com.htc.lib1.cc.R.id.desc);
        mButton = (HtcRimButton) this.findViewById(com.htc.lib1.cc.R.id.button);

        if (mImageResId != 0) {
            setImage(mImageResId);
        } else if (mImageDrawable != null) {
            setImage(mImageDrawable);
        }

        if (mDescTextResId != 0) {
            setDescriptionText(mDescTextResId);
        } else if (mDescTextString != null) {
            setDescriptionText(mDescTextString);
        }

        if (mButtonTextResId != 0) {
            setButtonText(mButtonTextResId);
        } else if (mButtonTextString != null) {
            setButtonText(mButtonTextString);
        }

        if (mButtonOnClickListener != null) {
            mButton.setOnClickListener(mButtonOnClickListener);
        }

        if (mCustomView != null) {
            addCustomBottomView(mCustomView);
        }
        if (mMinorFontStyle)
            setMinorDescriptionStyle(mMinorFontStyle);
    }

    private float getScaleRatio(int resid) {
        Drawable drawable = getResources().getDrawable(resid);
        return getScaleRatio(drawable);
    }

    private float getScaleRatio(Drawable drawable) {
        Display display = ((WindowManager) getSystemService(android.content.Context.WINDOW_SERVICE))
                .getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        float ratio = (float) point.x / drawable.getIntrinsicWidth();
        return ratio;
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
            mDivider.setVisibility(isImageSet ? View.VISIBLE : View.GONE);
        }
        if (!WindowUtil.isSuitableForLandscape(getResources())) {
            mImageLayout.setPadding(0, mMarginM1, 0, mMarginM1);
            LayoutParams params = (LayoutParams) mImage.getLayoutParams();
            params.height = (int) (getResources().getDrawable(id)
                    .getIntrinsicHeight() * getScaleRatio(id));
            mImage.setLayoutParams(params);
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
            mDivider.setVisibility(isImageSet ? View.VISIBLE : View.GONE);
        }
        if (!WindowUtil.isSuitableForLandscape(getResources())) {
            mImageLayout.setPadding(0, mMarginM1, 0, mMarginM1);
            LayoutParams params = (LayoutParams) mImage.getLayoutParams();
            params.height = (int) (drawable.getIntrinsicHeight() * getScaleRatio(drawable));
            mImage.setLayoutParams(params);
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
     * set minor description font style
     *
     * @param isMinorStyle
     *            decide set description as minor style
     */
    public void setMinorDescriptionStyle(boolean isMinorStyle) {
        mMinorFontStyle = isMinorStyle;
        if (mText == null) {
            return;
        }
        mText.setVisibility(View.VISIBLE);
        mText.setTextAppearance(this,
                com.htc.lib1.cc.R.style.list_body_primary_xs);
    }

    /**
     * set subContentView button content text
     *
     * @param id
     *            resource id of button content text
     */
    protected void setButtonText(int id) {
        mButtonTextString = null;
        mButtonTextResId = id;
        if (mButton == null) {
            return;
        }

        boolean isTextSet = (id != 0);
        mButton.setVisibility(isTextSet ? View.VISIBLE : View.GONE);
        mButton.setText(id);
    }

    /**
     * set subContentView button content text
     *
     * @param str
     *            button content text string
     */
    protected void setButtonText(CharSequence str) {
        mButtonTextResId = 0;
        mButtonTextString = str;
        if (mButton == null) {
            return;
        }

        boolean isTextSet = (str != null);
        mButton.setVisibility(isTextSet ? View.VISIBLE : View.GONE);
        mButton.setText(str);
    }

    /**
     * set subContentView button listener Register a callback to be invoked when
     * this button is clicked. If this view is not clickable, it becomes
     * clickable.
     *
     * @param listener
     *            The callback that will run.
     */
    protected void setButtonOnClickListener(OnClickListener listener) {
        mButtonOnClickListener = listener;
        if (mButton != null) {
            mButton.setOnClickListener(listener);
        }
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
