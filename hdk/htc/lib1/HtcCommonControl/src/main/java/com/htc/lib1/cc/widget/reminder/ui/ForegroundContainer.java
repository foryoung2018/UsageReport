package com.htc.lib1.cc.widget.reminder.ui;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.htc.lib1.cc.widget.reminder.Const;
import com.htc.lib1.cc.widget.reminder.debug.MyLog;

/** @hide */
public class ForegroundContainer extends RelativeLayout {

    private static final String TAG = "FgContainer";

    private float mTargetAlpha = 1;
    private ObjectAnimator mFadeAnimator;

    public ForegroundContainer(Context context) {
        super(context);
    }

    public ForegroundContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ForegroundContainer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /** @hide */
    public void cleanUp() {
        cancelAnimator();
    }

    /** @hide */
    public void setVisibility(int visibility) {
        setVisibilityByAlpha(visibility, false);
    }

    /** @hide */
    public void setVisibilityByAlpha(int visibility, boolean withAnimation) {
        boolean show = visibility == View.VISIBLE;
        boolean showing = isShowing();
        if (show != showing) {
            cancelAnimator();
            float endAlpha = 0;
            if (show) {
                endAlpha = 1;
            }
            mTargetAlpha = endAlpha;
            MyLog.i(TAG, "tarAlpha: " + mTargetAlpha);
            if (withAnimation) {
                updateAlphaByAnimation();
            } else {
                this.setAlpha(mTargetAlpha);
            }
        } else {
            if (!withAnimation) {
                cancelAnimator();
            }
        }
    }

    /** @hide */
    public boolean isShowing() {
        return mTargetAlpha  > 0;
    }

    private void updateAlphaByAnimation() {
        float startAlpha = this.getAlpha();
        if (startAlpha != mTargetAlpha) {
            mFadeAnimator = FadeAnimator.ofFloat(this, "myAlpha", startAlpha, mTargetAlpha);
            if (mFadeAnimator != null) {
                mFadeAnimator.setDuration(Const.DEFAULT_DURATION_FADE);
                mFadeAnimator.start();
            }
        }
    }

    private void cancelAnimator() {
        if (mFadeAnimator != null) {
            if (mFadeAnimator.isRunning()) {
                mFadeAnimator.cancel();
            }
            mFadeAnimator = null;
        }
    }
}
