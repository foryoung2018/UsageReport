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

package com.htc.lib1.cc.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.content.res.Resources;

/**
 * @deprecated [Not use any longer] Not support class
 */
/**@hide*/
public class HtcPhotoButton extends RelativeLayout {
    private final static String TAG = "HtcPhotoButton";
    private ImageView mPhoto;
    private TextView mText;
    private RelativeLayout mLayout;
    private RelativeLayout mLeft;
    private int mPaddingLeft;
    private int mPaddingRight;
    private int mPaddingTop;
    private int mPaddingBottom;
    OnClickListener mOnClickListener = null;

    public HtcPhotoButton(Context context) {
        super(context);
        try {
            setLayout();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public HtcPhotoButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        try {
            setLayout();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setLayout() throws Exception {
        removeAllViews();

        final LayoutInflater inflater = LayoutInflater.from(getContext());

        ViewGroup layout = (ViewGroup)inflater.inflate(com.htc.lib1.cc.R.layout.photo_button, null);

        if (layout != null) {
            addView(layout);
        } else {
            throw new Exception("no theme resource");
        }

        initView();

    }

    private void setLayout(int inputLayoutid) throws Exception {
        removeAllViews();

        final LayoutInflater inflater = LayoutInflater.from(getContext());

        ViewGroup layout = (ViewGroup)inflater.inflate(com.htc.lib1.cc.R.layout.photo_button, null);

        if (layout != null) {
            addView(layout);
        } else {
            throw new Exception("no theme resource");
        }

        initView();

    }

    private void initView() {
        Resources res = getResources();

        mLayout = (RelativeLayout)findViewById(com.htc.lib1.cc.R.id.main);
        mLeft = (RelativeLayout)findViewById(com.htc.lib1.cc.R.id.left);
        mPhoto = (ImageView)findViewById(com.htc.lib1.cc.R.id.photo);
        mText = (TextView)findViewById(com.htc.lib1.cc.R.id.from);
        mPaddingLeft = mText.getPaddingLeft();
        mPaddingRight = mText.getPaddingRight();
        mPaddingTop = mText.getPaddingTop();
        mPaddingBottom = mText.getPaddingBottom();
        setImageResource(-1);
    }

    public void setImageResource(int resid) {
        if (mPhoto != null && resid >= 0) {
            mPhoto.setImageResource(resid);
            mLeft.setVisibility(View.VISIBLE);
            mText.setPadding(0, mPaddingTop, mPaddingRight, mPaddingBottom);
        } else {
            mLeft.setVisibility(View.GONE);
            mText.setPadding(mPaddingLeft, mPaddingTop, mPaddingRight, mPaddingBottom);
        }
    }

    public void setImageDrawable(Drawable drawable) {
        if (mPhoto != null && drawable != null) {
            mPhoto.setImageDrawable(drawable);
            mLeft.setVisibility(View.VISIBLE);
            mText.setPadding(0, mPaddingTop, mPaddingRight, mPaddingBottom);
        } else {
            mLeft.setVisibility(View.GONE);
            mText.setPadding(mPaddingLeft, mPaddingTop, mPaddingRight, mPaddingBottom);
        }
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public void setImageBitmap(Bitmap bitmap) {
        if (mPhoto != null && bitmap != null) {
            mPhoto.setImageBitmap(bitmap);
            mLeft.setVisibility(View.VISIBLE);
            mText.setPadding(0, mPaddingTop, mPaddingRight, mPaddingBottom);
        } else {
            mLeft.setVisibility(View.GONE);
            mText.setPadding(mPaddingLeft, mPaddingTop, mPaddingRight, mPaddingBottom);
        }
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public void setImageURI(Uri uri) {
        if (mPhoto != null && uri != null) {
            mPhoto.setImageURI(uri);
            mLeft.setVisibility(View.VISIBLE);
            mText.setPadding(0, mPaddingTop, mPaddingRight, mPaddingBottom);
        } else {
            mLeft.setVisibility(View.GONE);
            mText.setPadding(mPaddingLeft, mPaddingTop, mPaddingRight, mPaddingBottom);
        }
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public void setText(CharSequence text) {
        if (mText != null)
            mText.setText(text);
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public void setText(int resid) {
        if (mText != null)
            mText.setText(resid);
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public CharSequence getText() {
        return mText.getText();
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public TextView getTextView() {
        return mText;
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public int getImageWidth() {
        return mPhoto.getMeasuredWidth();
    }

    public void setOnClickListener(OnClickListener l) {
        mOnClickListener = l;
        mLayout.setOnClickListener(l);
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public void setWidth(int width) {
        RelativeLayout.LayoutParams params = null;
        params = (RelativeLayout.LayoutParams)mLayout.getLayoutParams();
        params.width = width;
        mLayout.setLayoutParams(params);
    }

    @Override
    public void setTag(Object tag) {
        mLayout.setTag(tag);
        super.setTag(tag);
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public Object getTag() {
        if (mLayout.getTag() != null)
            return mLayout.getTag();

        return super.getTag();
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
        case MotionEvent.ACTION_DOWN:
            if (mPhoto != null) {
                mPhoto.setPressed(true);
                mPhoto.invalidate();
            }
            break;
        case MotionEvent.ACTION_CANCEL:
        case MotionEvent.ACTION_UP:
            if (mPhoto != null) {
                mPhoto.setPressed(false);
                mPhoto.invalidate();
            }
            break;
        }
        return super.onInterceptTouchEvent(ev);
    }
}
