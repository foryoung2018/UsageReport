/*
 * High Tech Computer Proprietary Rights Acknowledgment
 *
 * Copyright (C) 2008 High Tech Computer Corporation
 *
 * All Rights Reserved.
 *
 * The information contained in this work is the exclusive property of
 * High Tech Computer Corporation ("HTC"). Only the user who is legally
 * authorized by HTC ("Authorized User") has right to employ this work
 * within the scope of this statement. Nevertheless, the Authorized User
 * shall not use this work for any purpose other than the purpose agreed by HTC.
 * Any and all addition or modification to this work shall be unconditionally
 * granted back to HTC and such addition or modification shall be solely owned by HTC.
 * No right is granted under this statement, including but not limited to,
 * distribution, reproduction, and transmission, except as otherwise provided in this statement.
 * Any other usage of this work shall be subject to the further written consent of HTC.
 */

package com.htc.lib1.cc.widget.recipientblock;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewDebug.IntToString;
import android.view.ViewGroup;
import android.view.ViewDebug.ExportedProperty;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.htc.lib1.cc.util.CheckUtil;
import com.htc.lib1.cc.widget.HtcRimButton;
import com.htc.lib1.cc.R;

public class HtcRecipientButton extends LinearLayout {
    private final static String TAG = "HtcRecipientButton";
    public static final int DEFAULT_RECIPIENT_BUTTON_STYLE = 1;
    public static final int ACTION_BAR_BUTTON_STYLE = 2;
    private Resources mRes;
    @ExportedProperty(category = "CommonControl")
    private int mMaxWidth = ViewGroup.LayoutParams.WRAP_CONTENT;
    @ExportedProperty(category = "CommonControl")
    private int mWidth = 0;
    @ExportedProperty(category = "CommonControl", mapping = {
            @IntToString(from = DEFAULT_RECIPIENT_BUTTON_STYLE, to = "DEFAULT_RECIPIENT_BUTTON_STYLE"),
            @IntToString(from = ACTION_BAR_BUTTON_STYLE, to = "ACTION_BAR_BUTTON_STYLE")
    })
    private int mStyle = 0;
    private Drawable mIndicator = null;
    private HtcRimButton mRimButton= null;

    OnClickListener mOnClickListener = null;
    OnLongClickListener mOnLongClickListener = null;

    public HtcRecipientButton(Context context) {
        super(context);
        CheckUtil.isContextThemeWrapper(context);
        CheckUtil.isUIThread(context);
        mRes = context.getResources();
        initView();
    }

    public HtcRecipientButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        CheckUtil.isContextThemeWrapper(context);
        CheckUtil.isUIThread(context);
        mRes = context.getResources();
        initView();
    }

    private void initView() {
        this.setOrientation(LinearLayout.HORIZONTAL);
        mRimButton= new HtcRimButton(getContext());
        addView(mRimButton);
    }

    public HtcRimButton getButton(){
        return mRimButton;
    }

    /*@hide*/
    @ExportedProperty(category = "CommonControl")
    protected int getBtnWidth() {
        return mWidth;
    }

    /*@hide*/
    protected CharSequence getText() {
        return mRimButton.getText();
    }

    /*@hide*/
    protected void setText(CharSequence text) {
        if (mRimButton!= null)
            mRimButton.setText(text);
    }

    private void setText(int resid) {
        if (mRimButton!= null)
            mRimButton.setText(resid);
    }

    @Override
    public void setOnClickListener(OnClickListener listener) {
        if(mRimButton!= null) {
            mRimButton.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                   viewClicked();
                }
            });
        }
        super.setOnClickListener(listener);

    }

    public void setOnLongClickListener(OnLongClickListener listener) {
        if(mRimButton!= null)
            mRimButton.setOnLongClickListener(listener);
    }

    private void setReceiverTag(Object obj) {
        if(mRimButton!= null)
            mRimButton.setTag(obj);

    }

    // Indicator
    private void createIndicator(boolean expanded) {

        if(expanded) {
            mIndicator = getContext().getResources().getDrawable(R.drawable.common_collapse_small);
        } else {
            mIndicator = getContext().getResources().getDrawable(R.drawable.common_expand_small);
        }
    }

    /*@hide*/
    protected void setStyle(int style) {
        mIndicator = null;
        switch(style) {
        case DEFAULT_RECIPIENT_BUTTON_STYLE:
           /*
            *   +-----------------------+  <- ParentLayout
            *   |           ↑ M2 ↑      |
            *   +      +----------------+  <- HtcRecipientButton (ceiling)
            *   | ← M2 |( aaa@htc.com ) |
            *   +-----------------------+  <- HtcRecipientButton (floor)
            */
            mStyle = DEFAULT_RECIPIENT_BUTTON_STYLE;
            if (mRimButton!= null) {
                removeView(mRimButton);
            }
            initView();
            break;
        case ACTION_BAR_BUTTON_STYLE:
            /*
             *   +------------------------+  <- HtcRecipientButton (ceiling)
             *   |         ↑ M1 ↑         |
             *   | ←   M1 ( Edit )   M1 → |  <- increased touch area
             *   |         ↓ M1 ↓         |
             *   +------------------------+  <- HtcRecipientButton (floor)
             */
            mStyle = ACTION_BAR_BUTTON_STYLE;
            if (mRimButton == null) {
                initView();
            }

            mRimButton.setPadding(ResUtils.getDimenMarginM1(getContext()), ResUtils.getDimenMarginM1(getContext()),
                    ResUtils.getDimenMarginM1(getContext()), ResUtils.getDimenMarginM1(getContext()));
            mRimButton.setBackgroundDrawable(null);
            mRimButton.setClickable(true);
            mRimButton.setTextAppearance(getContext(), R.style.list_secondary_m);
            //mRimButton.setTextAppearance(getContext(), com.htc.R.style.darklist_secondary_m);
            break;
        }
    }

    /*@hide*/
    protected void setIndicatorExpanded(boolean expanded) {
        if (mIndicator == null) {
           createIndicator(expanded);
        }
        if(mIndicator!= null){
            mIndicator.setBounds(0, 0, mIndicator.getIntrinsicWidth(), mIndicator.getIntrinsicWidth());
            mRimButton.setCompoundDrawablePadding(ResUtils.getCompoundDrawablePadding(getContext()));

            //mRimButton.setCompoundDrawables(null, null, mIndicator, null);
            mRimButton.setIconDrawable(null, null, mIndicator, null);
        }
    }

    /*@hide*/
    protected void setWidth(int width) {
        ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) this.getLayoutParams();
        if (params == null) {
            params = new ViewGroup.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        params.width = width;
        this.setLayoutParams(params);

        mRimButton.setWidth(width);
        mWidth = width;
    }

    @ExportedProperty(category = "CommonControl")
    private int getImageWidth() {
        if (mIndicator == null) return 0;

        return (int) mIndicator.getIntrinsicWidth() + ResUtils.getCompoundDrawablePadding(getContext());
    }

    @ExportedProperty(category = "CommonControl")
    private int getTextWidth() {
        return (int) ((TextView) mRimButton).getPaint().measureText(mRimButton.getText().toString())
                + mRimButton.getPaddingLeft() + mRimButton.getPaddingRight();
    }

    /*@hide*/
    @ExportedProperty(category = "CommonControl")
    protected int getButtonWidth() {
        return (int) getTextWidth() + getImageWidth() + this.getPaddingLeft() + this.getPaddingRight();
    }

    /*@hide*/
    @ExportedProperty(category = "CommonControl")
    protected int getButtonHeight(int width) {
        int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
        int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        mRimButton.measure(childWidthMeasureSpec, childHeightMeasureSpec);
        return mRimButton.getMeasuredHeight() + this.getPaddingTop() + this.getPaddingBottom();
    }

    public boolean onInterceptTouchEvent(MotionEvent event) {
        return true;

    }

    public boolean onTouchEvent(MotionEvent event) {

        if(mRimButton!= null) {
            mRimButton.onTouchEvent(event);
        }

        return true;
    }

    private void viewClicked() {
        this.performClick();
    }
}
