package com.htc.lib1.cc.widget.reminder.ui.footer;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.htc.lib1.cc.widget.reminder.Const;
import com.htc.lib1.cc.widget.reminder.debug.MyLog;
import com.htc.lib1.cc.widget.reminder.ui.FadeAnimator;

/** @hide */
public class Panel extends RelativeLayout {

    private static final String TAG = "Panel";

    private float mTargetAlpha = 1.0f;
    private ObjectAnimator mFadeAnimator;

    public Panel(Context context) {
        super(context);
    }

    public Panel(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Panel(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void init() {
        // Do Nothing
    }

    public void cleanUp() {
        cancelAnimator();
    }

    @Override
    public void setVisibility(int visibility) {
        boolean isShow = (visibility == View.VISIBLE);
        boolean withAnim = false;
        setVisibilityByAlpha(isShow, withAnim, 0);
    }

    public void setFooterVisibility(boolean isShow, boolean withAnimation, int delayTime) {
        setVisibilityByAlpha(isShow, withAnimation, delayTime);
    }

    private void setVisibilityByAlpha(boolean isShow, boolean withAnimation, int delayTime) {
        boolean isShowing = isShowing();
        if (isShow != isShowing) {
            cancelAnimator();
            float endAlpha = Const.PANEL_FADE_IN_END;
            if (!isShow) {
                endAlpha = Const.PANEL_FADE_OUT_END;
            }
            mTargetAlpha = endAlpha;
            if (withAnimation) {
                float curAlpha = this.getAlpha();
                if (isShow) {
                    // Panel (Dragging): 0.5 - 1.0
                    float startAlpha =  Const.PANEL_FADE_IN_START;
                    if (curAlpha < startAlpha) {
                        curAlpha = startAlpha;
                    }
                } else {
                    // Panel (Dragging): 0.5 - 0.0
                    if (curAlpha > Const.PANEL_FADE_OUT_START) {
                        curAlpha = Const.PANEL_FADE_OUT_START;
                    }
                }
                MyLog.i(TAG, "setVisi8Alpha"
                        + " curAl: " + curAlpha
                        + " endAl: " + endAlpha
                        + " delayT: " + delayTime);
                updateAlphaByAnimation(curAlpha, endAlpha, delayTime);
            } else {
                MyLog.i(TAG, "setVisi8Alpha: " + mTargetAlpha);
                this.setAlpha(mTargetAlpha);
            }
        } else {
            if (!withAnimation) {
                cancelAnimator();
            }
        }
    }

    private void updateAlphaByAnimation(float startAlpha, float endAlpha, int delayTime) {
        synchronized (this) {
            if (startAlpha != mTargetAlpha) {
                mFadeAnimator = FadeAnimator.ofFloat(this, "myAlpha", startAlpha, mTargetAlpha);
                if (mFadeAnimator != null) {
                    mFadeAnimator.setDuration(Const.DEFAULT_DURATION_FADE);
                    mFadeAnimator.setStartDelay(delayTime);
                    mFadeAnimator.start();
                }
            }
        }
    }

    private void cancelAnimator() {
        synchronized (this) {
            if (mFadeAnimator != null) {
                if (mFadeAnimator.isRunning()) {
                    mFadeAnimator.cancel();
                }
                mFadeAnimator = null;
            }
        }
    }

    public boolean isShowing() {
        return (mTargetAlpha > 0);
    }
}
