
package com.htc.lib1.cc.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.util.Log;
import android.view.View;

/**
 * @hide
 */
public class ActionBarAnimController {

    private Animator.AnimatorListener mAnimatorListener;
    private View mForegroundView;
    private View mBackgroundView;
    private int mProgress;
    private int mProgressMax = 100;
    private Context mContext;

    private ValueAnimator valueAnimator;

    private static final float INIT_REFRESH_ANIM_ROTATE_ANGLE = 90;
    private static final float INIT_REFRESH_ANIM_ALPHA = 1.0f;

    public ActionBarAnimController(Context context) {
        mContext = context;
    }

    public void setAnimProgress(int progress) {
        if (progress < 0 || mProgress == progress) return;
        if (progress > mProgressMax) {
            mProgress = mProgressMax;
        } else {
            mProgress = progress;
        }
        setProgressInternal(mProgress);
    }

    public void setAnimProgress(int start, int end) {
        if (start > mProgressMax || start == end || start < 0 || end < 0) return;

        if (end > mProgressMax) {
            mProgress = mProgressMax;
        } else {
            mProgress = end;
        }

        animEnd();

        valueAnimator = ValueAnimator.ofInt(start, mProgress);
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                animation.end();
            }
        });
        if (mAnimatorListener != null) {
            valueAnimator.addListener(mAnimatorListener);
        }
        valueAnimator.addUpdateListener(new AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setProgressInternal((Integer) animation.getAnimatedValue());
            }
        });
        valueAnimator.start();

    }

    private void setProgressInternal(int progress) {
        if (mForegroundView != null) {
            mForegroundView.setVisibility(View.VISIBLE);
            mForegroundView.setRotationX(getForegroundRotateAngle(progress));
            mForegroundView.setAlpha(getForegroundAlpha(progress));
        }
        if (mBackgroundView != null) {
            mBackgroundView.setRotationX(getBackgroundRotateAngle(progress));
            mBackgroundView.setAlpha(getBackgroundAlpha(progress));
        }
    }

    private float getForegroundRotateAngle(int value) {
        return -INIT_REFRESH_ANIM_ROTATE_ANGLE * (float) value / mProgressMax;

    }

    private float getBackgroundRotateAngle(int value) {
        return INIT_REFRESH_ANIM_ROTATE_ANGLE * (float) (mProgressMax - value) / mProgressMax;

    }

    private float getForegroundAlpha(int value) {
        return INIT_REFRESH_ANIM_ALPHA * (float) (mProgressMax - value) / (float) mProgressMax;
    }

    private float getBackgroundAlpha(int value) {
        return INIT_REFRESH_ANIM_ALPHA * (float) value / (float) mProgressMax;
    }

    public int getAnimProgress() {
        return mProgress;
    }

    public void setAnimProgressMax(int progressMax) {
        mProgressMax = progressMax;
    }

    public int getAnimProgressMax() {
        return mProgressMax;
    }

    public void setForegroundView(View view) {
        mForegroundView = view;
    }

    public void setBackgroundView(View view) {
        mBackgroundView = view;
    }

    public void setAnimatorListener(Animator.AnimatorListener listener) {
        mAnimatorListener = listener;
    }

    public void animEnd() {
        if (valueAnimator != null) {
            if (valueAnimator.isStarted()) {
                valueAnimator.end();
            }
            valueAnimator.removeAllListeners();
        }
    }

    public void resetProgress() {
        animEnd();
        mProgress = 0;
    }
}
