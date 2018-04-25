/**
 * HTC Corporation Proprietary Rights Acknowledgment
 *
 * Copyright (C) 2010 HTC Corporation
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
package com.htc.lib1.masthead.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.htc.lib1.masthead.R;

class WeatherDisplay extends FrameLayout {

	private final static String LOG_TAG = WeatherDisplay.class.getSimpleName();

    private ImageView mIconView;
    private TextView mTextView;
    private String mWebLink;

    
    /** The {@link TouchFeedbackHelper} for touch feedback animations */
    private TouchFeedbackHelper mTouchFeedbackHelper;
    
    public WeatherDisplay(Context context) {
        this(context, null);
    }

    public WeatherDisplay(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WeatherDisplay(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mTouchFeedbackHelper = new TouchFeedbackHelper(0.96f);
        setFocusable(true);
    }
    
    public void setWeatherIcon(Drawable icon) {
    	// remove image
    	if (icon == null) {
    		if (mIconView != null) {
    			mIconView.setVisibility(View.GONE);
    		}
    		return;
    	}

    	// set image
    	if (mIconView == null) {
    		mIconView = (ImageView) findViewById(R.id.sun);
    	}
    	
		mIconView.setVisibility(View.VISIBLE);
    	mIconView.setImageDrawable(icon);    	
    }
    
    TextView getTextView() {
    	if (mTextView == null) {
    		mTextView = (TextView) findViewById(R.id.no_weather_text);
    	}
    	return mTextView;
    }
    
    public void setWeatherText(String condition) {
    	// remove text
    	if (condition == null) { 
    		if (mTextView != null)
    			mTextView.setVisibility(View.GONE);
    		return;
    	}

    	// set text
    	if (mTextView == null) {
    		mTextView = (TextView) findViewById(R.id.no_weather_text);
    	}

    	// content
    	mTextView.setVisibility(View.VISIBLE);
    	Masthead.setTextAndLocale(mTextView, condition);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
    	if (isClickable()) {
    		mTouchFeedbackHelper.onTouchEvent(this, event);
    	}
        return super.onTouchEvent(event);
    }


    private Drawable mFocusIndicator;
    private boolean mDrawFocusIndicator;

    @Override
    public void setFocusable(boolean focusable) {
        super.setFocusable(focusable);
        if (focusable && mFocusIndicator == null) {
            mFocusIndicator = getContext().getResources().getDrawable(com.htc.lib1.masthead.R.drawable.common_focused);

            if (mFocusIndicator != null) {
                mFocusIndicator.mutate();
                mFocusIndicator.setColorFilter(new PorterDuffColorFilter(getResources().getColor(com.htc.lib1.masthead.R.color.overlay_color), PorterDuff.Mode.SRC_ATOP));
            }
        }
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        mDrawFocusIndicator = gainFocus;
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        if (mDrawFocusIndicator) {
            Rect bound = canvas.getClipBounds();
            View parent = (View)getParent();
            if ( parent != null) {
                int nParentRight = parent.getWidth();
                int nParentHeight = parent.getHeight();
                int nX = Math.round(getX());
                int nY = Math.round(getY());

                // left boundary is smaller than parent
                if (nX < 0) {
                    bound.left -= nX;
                }

                // top boundary is smaller than parent
                if (nY < 0) {
                    bound.top -= nY;
                }

                // check right bound of parent
                if (nX + getWidth() > nParentRight) {
                    bound.right -= (nX + getWidth() - nParentRight);
                }

                // check bottom bound of parent
                if (nY + getHeight() > nParentHeight) {
                    bound.bottom -= (nY + getHeight() - nParentHeight);
                }
            }
            mFocusIndicator.setBounds(bound);
            mFocusIndicator.draw(canvas);
        }
    }
    
    void setURL(String url) {
        mWebLink = url;
    }
    
    String getURL() {
    	return mWebLink;
    }           
}
