package com.htc.lib1.cc.widget.reminder.ui;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewParent;

import com.htc.lib1.cc.widget.reminder.Const;

/** @hide */
public class FadeAnimator {

    private static final String TAG = "FadeAnima";

    public static ObjectAnimator ofPropertyValuesHolder(View target, PropertyValuesHolder holder, float endAlpha, String key){
        ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(target, holder);
        addUpdateListener(animation, target, endAlpha, key);
        return animation;
    }

    public static ObjectAnimator ofFloat(View target, String key, float start, float end){
        ObjectAnimator animation = ObjectAnimator.ofFloat(target, key, start, end);
        addUpdateListener(animation, target, end, key);
        return animation;
    }

    public static ObjectAnimator ofFloat(View target, String key, float[] keyframe){
        if (keyframe == null || keyframe.length == 0) {
            return null;
        }
        ObjectAnimator animation = ObjectAnimator.ofFloat(target, key, keyframe);
        addUpdateListener(animation, target, keyframe[keyframe.length -1], key);
        return animation;
    }

    private static void addUpdateListener(ObjectAnimator animation, final View target, final float endAlpha, final String key) {
        if (target == null || animation == null) {
            return;
        }
        animation.addUpdateListener(new AnimatorUpdateListener() {
            private long mLastTime = 0;
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                long time = SystemClock.elapsedRealtime();
                if (time - mLastTime >= Const.DEFAULT_FADE_TIME_PER_FRAME) {
                    mLastTime = time;
                    if (animator != null) {
                        ViewParent parent = target.getParent();
                        if (parent == null) {
                            animator.cancel();
                        } else {
                            Object value = animator.getAnimatedValue(key);
                            if (value != null) {
                                float alpha = (Float)value;
                                target.setAlpha(alpha);
                            }
                        }
                    }
                }
            }
        });

        animation.addListener(new AnimatorListener() {
            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                target.setAlpha(endAlpha);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }

            @Override
            public void onAnimationStart(Animator animation) {
            }
        });
    }
}
