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

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.view.MotionEvent;
import android.view.View;

class TouchFeedbackHelper {

    /** The scale factor of the view when applying touch feedback. */
    private static final float DEFAULT_PRESS_SCALE = 0.9f;

    /** The duration of the touch feedback down animation. */
    private static final long PRESS_DOWN_FEEDBACK_DURATION = 33l;

    /** The duration of the touch feedback up animation. */
    private static final long PRESS_UP_FEEDBACK_DURATION = 67l;

    /** The animator for the touch feedback down animation. */
    private Animator mTouchDownAnimator;

    /** The animator for the touch feedback up animation. */
    private Animator mTouchUpAnimator;

    /**
     * State flag to remember that the touch feedback down animation is performed, and so the touch feedback up
     * animation should be performed next.
     */
    private boolean mIsTouchUpPending = false;

    /**
     * Custom press scale factor for touch feedback animation.
     */
    private float mPressScale = DEFAULT_PRESS_SCALE;

    /**
     * Whether or not the ACTION_UP touch event occurs while inside the BubbleTextView's bounds, and therefore a click
     * event should be performed.
     */
//    private boolean mIsClickPending = false;

    public TouchFeedbackHelper() {
    }

    public TouchFeedbackHelper(float pressScale) {
        mPressScale = pressScale;
    }

    public void onTouchEvent(View v, MotionEvent event) {
        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
//                mIsClickPending = false;
                startTouchDownFeedback(v);
                break;
            case MotionEvent.ACTION_CANCEL:
                resetTouchFeedback(v);
                break;
            case MotionEvent.ACTION_UP:
//                final int x = (int) event.getX();
//                final int y = (int) event.getY();
//                boolean upInside = true;
//                final int slop = ViewConfiguration.get(v.getContext()).getScaledTouchSlop();
//                if((x < 0 - slop) || (x >= v.getWidth() + slop) || (y < 0 - slop) || (y >= v.getHeight() + slop)) {
//                    upInside = false;
//                }
//                mIsClickPending = upInside;
                if(mTouchDownAnimator != null) { // Down animating, postpone click
                    mIsTouchUpPending = true;
                } else { // not animating
                    startTouchUpFeedback(v);
                }
                break;
        }
    }

    private void startTouchDownFeedback(final View v) {
        if(mTouchDownAnimator != null) {
            mTouchDownAnimator.cancel();
            mTouchDownAnimator = null;
        }
        if(mTouchUpAnimator != null) {
            mTouchUpAnimator.cancel();
            mTouchUpAnimator = null;
        }
        final PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat("scaleX", v.getScaleX(), mPressScale);
        final PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat("scaleY", v.getScaleY(), mPressScale);
        final ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(v, scaleX, scaleY);
        animator.setDuration(PRESS_DOWN_FEEDBACK_DURATION);
        mTouchDownAnimator = animator;
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mTouchDownAnimator = null;
                if(mIsTouchUpPending) {
                    mIsTouchUpPending = false;
                    startTouchUpFeedback(v);
                }
            }
        });
        mTouchDownAnimator.start();
    }

    private void startTouchUpFeedback(final View v) {
        if(mTouchUpAnimator != null) {
            mTouchUpAnimator.cancel();
            mTouchUpAnimator = null;
        }
        final PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat("scaleX", v.getScaleX(), 1.0f);
        final PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat("scaleY", v.getScaleY(), 1.0f);
        final ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(v, scaleX, scaleY);
        animator.setDuration(PRESS_UP_FEEDBACK_DURATION);
        mTouchUpAnimator = animator;
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mTouchUpAnimator = null;
//                if(mIsClickPending && mOnClickListener != null) {
//                    mOnClickListener.onClick(v);
//                }
//                mIsClickPending = false;
                v.setScaleX(1f);
                v.setScaleY(1f);
            }
        });
        mTouchUpAnimator.start();
    }

    private void resetTouchFeedback(View v) {
        if(mTouchDownAnimator != null) {
            mTouchDownAnimator.cancel();
            mTouchDownAnimator = null;
        }
        if(mTouchUpAnimator != null) {
            mTouchUpAnimator.cancel();
            mTouchUpAnimator = null;
        }

        v.setScaleX(1f);
        v.setScaleY(1f);
//        mIsClickPending = false;
    }

}
